package com.xt.interview.service.impl;

import com.xt.interview.dto.AccountDTO;
import com.xt.interview.dto.PageResult;
import com.xt.interview.entity.Account;
import com.xt.interview.entity.Department;
import com.xt.interview.repository.AccountRepository;
import com.xt.interview.repository.DepartmentRepository;
import com.xt.interview.service.AccountService;
import com.xt.interview.util.MD5Util;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 账号管理业务实现。
 *
 * <ul>
 *   <li>密码统一以 MD5 存储，新增时为空则使用默认密码；修改时为空表示不改；</li>
 *   <li>账号与部门通过 {@code departmentId} 关联，部门为可选字段；</li>
 *   <li>返回 DTO 时批量补齐部门名称，避免列表逐行查库。</li>
 * </ul>
 */
@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    /** 未删除标记 */
    private static final Integer NOT_DELETED = 0;

    /** 已删除标记 */
    private static final Integer DELETED = 1;

    /** 新增账号时的默认密码 */
    private static final String DEFAULT_PASSWORD = "123456";

    /** 分页默认每页条数 */
    private static final int DEFAULT_PAGE_SIZE = 10;

    /** 分页最大每页条数，防止一次拉取过多数据 */
    private static final int MAX_PAGE_SIZE = 100;

    /** 账号数据访问层 */
    @Autowired
    private AccountRepository accountRepository;

    /** 部门数据访问层，用于校验部门有效性、补齐部门名称 */
    @Autowired
    private DepartmentRepository departmentRepository;

    /**
     * 新增账号：用户名校验、部门有效性校验、密码加密、默认删除标记。
     */
    @Override
    public AccountDTO createAccount(AccountDTO dto) {
        if (accountRepository.existsByUsernameAndDeleted(dto.getUsername(), NOT_DELETED)) {
            throw new RuntimeException("用户名已存在");
        }
        Account account = new Account();
        account.setUsername(dto.getUsername());
        account.setName(dto.getName());
        account.setGender(dto.getGender());
        account.setPhone(dto.getPhone());
        account.setDepartmentId(validateDepartmentId(dto.getDepartmentId()));
        String password = (dto.getPassword() == null || dto.getPassword().isEmpty())
                ? DEFAULT_PASSWORD : dto.getPassword();
        account.setPassword(MD5Util.encode(password));
        account.setRoleLevel(dto.getRoleLevel());
        account.setStatus(dto.getStatus());
        account.setIsSuper(dto.getIsSuper());
        account.setDeleted(NOT_DELETED);
        account = accountRepository.save(account);
        return convertToDTO(account, loadDepartmentNames(Collections.singletonList(account.getDepartmentId())));
    }

    /**
     * 更新账号：用户名重复校验排除自身；密码为空则保留原密码；部门可清空。
     */
    @Override
    public AccountDTO updateAccount(Long id, AccountDTO dto) {
        Account account = accountRepository.findByIdAndDeleted(id, NOT_DELETED)
                .orElseThrow(() -> new RuntimeException("账号不存在"));

        if (!account.getUsername().equals(dto.getUsername()) &&
            accountRepository.existsByUsernameAndDeleted(dto.getUsername(), NOT_DELETED)) {
            throw new RuntimeException("用户名已存在");
        }

        account.setUsername(dto.getUsername());
        account.setName(dto.getName());
        account.setGender(dto.getGender());
        account.setPhone(dto.getPhone());
        account.setDepartmentId(validateDepartmentId(dto.getDepartmentId()));
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            account.setPassword(MD5Util.encode(dto.getPassword()));
        }
        account.setRoleLevel(dto.getRoleLevel());
        account.setStatus(dto.getStatus());
        account.setIsSuper(dto.getIsSuper());
        account = accountRepository.save(account);
        return convertToDTO(account, loadDepartmentNames(Collections.singletonList(account.getDepartmentId())));
    }

    /**
     * 软删除账号：仅把 deleted 置 1，保留数据便于追溯。
     */
    @Override
    public void deleteAccount(Long id) {
        Account account = accountRepository.findByIdAndDeleted(id, NOT_DELETED)
                .orElseThrow(() -> new RuntimeException("账号不存在"));
        account.setDeleted(DELETED);
        accountRepository.save(account);
    }

    /**
     * 按ID查询未删除的账号，并补齐所属部门名称。
     */
    @Override
    @Transactional(readOnly = true)
    public AccountDTO getAccountById(Long id) {
        Account account = accountRepository.findByIdAndDeleted(id, NOT_DELETED)
                .orElseThrow(() -> new RuntimeException("账号不存在"));
        return convertToDTO(account, loadDepartmentNames(Collections.singletonList(account.getDepartmentId())));
    }

    /**
     * 查询所有未删除的账号，部门名称一次性批量加载。
     */
    @Override
    @Transactional(readOnly = true)
    public List<AccountDTO> getAllAccounts() {
        List<Account> accounts = accountRepository.findByDeleted(NOT_DELETED);
        Map<Long, String> departmentNames = loadDepartmentNames(
                accounts.stream().map(Account::getDepartmentId).collect(Collectors.toList()));
        return accounts.stream()
                .map(account -> convertToDTO(account, departmentNames))
                .collect(Collectors.toList());
    }

    /**
     * 分页 + 条件查询：固定过滤 deleted=0，可选用户名模糊、状态、权限级别，按创建时间倒序。
     *
     * <p>页码与条数做了兜底：页码小于 0 归 0，条数非正数取默认值，超过上限按上限截断。</p>
     */
    @Override
    @Transactional(readOnly = true)
    public PageResult<AccountDTO> pageAccounts(String username, Integer status, Integer roleLevel, int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = size <= 0 ? DEFAULT_PAGE_SIZE : Math.min(size, MAX_PAGE_SIZE);

        String keyword = StringUtils.hasText(username) ? username.trim() : null;

        Specification<Account> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            // 软删除过滤
            predicates.add(cb.equal(root.get("deleted"), NOT_DELETED));
            // 用户名模糊搜索
            if (keyword != null) {
                predicates.add(cb.like(root.get("username"), "%" + keyword + "%"));
            }
            // 状态精确筛选
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            // 权限级别精确筛选
            if (roleLevel != null) {
                predicates.add(cb.equal(root.get("roleLevel"), roleLevel));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Pageable pageable = PageRequest.of(safePage, safeSize,
                Sort.by(Sort.Order.desc("createTime"), Sort.Order.desc("id")));
        Page<Account> result = accountRepository.findAll(specification, pageable);

        // 当前页涉及的部门名称一次性查回
        Map<Long, String> departmentNames = loadDepartmentNames(
                result.getContent().stream().map(Account::getDepartmentId).collect(Collectors.toList()));

        List<AccountDTO> list = result.getContent().stream()
                .map(account -> convertToDTO(account, departmentNames))
                .collect(Collectors.toList());

        return PageResult.of(list, result.getTotalElements(), result.getTotalPages(),
                result.getNumber(), result.getSize());
    }

    /**
     * 登录校验：用户名 + MD5 密码匹配且未删除，账号被禁用时拒绝登录。
     */
    @Override
    @Transactional(readOnly = true)
    public AccountDTO login(String username, String password) {
        Account account = accountRepository.findByUsernameAndPasswordAndDeleted(
                username, MD5Util.encode(password), NOT_DELETED)
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));
        if (account.getStatus() != 1) {
            throw new RuntimeException("账号已禁用");
        }
        return convertToDTO(account, loadDepartmentNames(Collections.singletonList(account.getDepartmentId())));
    }

    /**
     * 校验部门是否存在（未删除）。部门为可选字段，传 null 表示不设置部门。
     *
     * @param departmentId 部门ID
     * @return 校验通过的部门ID（可能为 null）
     */
    private Long validateDepartmentId(Long departmentId) {
        if (departmentId == null) {
            return null;
        }
        if (!departmentRepository.existsByIdAndDeleted(departmentId, NOT_DELETED)) {
            throw new RuntimeException("所属部门不存在");
        }
        return departmentId;
    }

    /**
     * 一次性批量查询部门名称，返回 ID → 名称 的映射。
     *
     * <p>已软删除的部门同样会返回名称，保证历史用户仍能正常展示所属部门。</p>
     *
     * @param departmentIds 部门ID集合，允许包含 null
     * @return 部门名称映射，无有效ID时返回空 Map
     */
    private Map<Long, String> loadDepartmentNames(Collection<Long> departmentIds) {
        List<Long> ids = departmentIds == null ? Collections.emptyList()
                : departmentIds.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if (ids.isEmpty()) {
            return Collections.emptyMap();
        }
        return departmentRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(Department::getId, Department::getName));
    }

    /**
     * 实体转 DTO，并按部门映射填充部门名称。
     *
     * @param account         账号实体
     * @param departmentNames 部门名称映射
     * @return 账号DTO
     */
    private AccountDTO convertToDTO(Account account, Map<Long, String> departmentNames) {
        AccountDTO dto = new AccountDTO();
        dto.setId(account.getId());
        dto.setUsername(account.getUsername());
        dto.setName(account.getName());
        dto.setGender(account.getGender());
        dto.setPhone(account.getPhone());
        dto.setDepartmentId(account.getDepartmentId());
        dto.setDepartmentName(account.getDepartmentId() == null
                ? null : departmentNames.get(account.getDepartmentId()));
        dto.setRoleLevel(account.getRoleLevel());
        dto.setStatus(account.getStatus());
        dto.setIsSuper(account.getIsSuper());
        dto.setCreateTime(account.getCreateTime());
        dto.setUpdateTime(account.getUpdateTime());
        return dto;
    }
}
