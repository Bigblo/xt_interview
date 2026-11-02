package com.xt.interview.service.impl;

import com.xt.interview.dto.DepartmentDTO;
import com.xt.interview.entity.Department;
import com.xt.interview.repository.DepartmentRepository;
import com.xt.interview.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 部门管理业务实现。
 *
 * <ul>
 *   <li>查询与校验只在未删除（deleted=0）范围内进行；</li>
 *   <li>删除为软删除，并把编码改写成 {@code 原编码#DEL#id}，避免占用唯一键；</li>
 *   <li>业务异常统一抛 RuntimeException，由 GlobalExceptionHandler 转成 400 响应。</li>
 * </ul>
 */
@Service
@Transactional
public class DepartmentServiceImpl implements DepartmentService {

    /** 未删除标记 */
    private static final Integer NOT_DELETED = 0;

    /** 已删除标记 */
    private static final Integer DELETED = 1;

    /** 默认状态：启用 */
    private static final Integer STATUS_ENABLED = 1;

    /** 部门编码字段最大长度（与建表语句保持一致，用于软删除改写编码时截断） */
    private static final int MAX_CODE_LENGTH = 50;

    /** 部门数据访问层 */
    @Autowired
    private DepartmentRepository departmentRepository;

    /**
     * 新增部门：校验名称/编码非空、未删除范围内不重复，状态缺省为启用。
     */
    @Override
    public DepartmentDTO createDepartment(DepartmentDTO dto) {
        String name = trimToNull(dto.getName());
        String code = trimToNull(dto.getCode());
        if (name == null) {
            throw new RuntimeException("部门名称不能为空");
        }
        if (code == null) {
            throw new RuntimeException("部门编码不能为空");
        }
        if (departmentRepository.existsByCodeAndDeleted(code, NOT_DELETED)) {
            throw new RuntimeException("部门编码已存在");
        }
        if (departmentRepository.existsByNameAndDeleted(name, NOT_DELETED)) {
            throw new RuntimeException("部门名称已存在");
        }

        Department department = new Department();
        department.setName(name);
        department.setCode(code);
        department.setDescription(trimToNull(dto.getDescription()));
        department.setStatus(dto.getStatus() == null ? STATUS_ENABLED : dto.getStatus());
        department.setDeleted(NOT_DELETED);
        department = departmentRepository.save(department);
        return convertToDTO(department);
    }

    /**
     * 更新部门：查重时排除自身；status 不传则保持原值。
     */
    @Override
    public DepartmentDTO updateDepartment(Long id, DepartmentDTO dto) {
        Department department = departmentRepository.findByIdAndDeleted(id, NOT_DELETED)
                .orElseThrow(() -> new RuntimeException("部门不存在"));

        String name = trimToNull(dto.getName());
        String code = trimToNull(dto.getCode());
        if (name == null) {
            throw new RuntimeException("部门名称不能为空");
        }
        if (code == null) {
            throw new RuntimeException("部门编码不能为空");
        }
        if (departmentRepository.existsByCodeAndDeletedAndIdNot(code, NOT_DELETED, id)) {
            throw new RuntimeException("部门编码已存在");
        }
        if (departmentRepository.existsByNameAndDeletedAndIdNot(name, NOT_DELETED, id)) {
            throw new RuntimeException("部门名称已存在");
        }

        department.setName(name);
        department.setCode(code);
        department.setDescription(trimToNull(dto.getDescription()));
        if (dto.getStatus() != null) {
            department.setStatus(dto.getStatus());
        }
        department = departmentRepository.save(department);
        return convertToDTO(department);
    }

    /**
     * 软删除部门，并释放编码：改写为 {@code 原编码#DEL#id}。
     *
     * <p>表上 code 是唯一键，若不改写，已删除部门的编码将永远无法复用；
     * 保留后缀也便于直接在数据库里追溯是哪条记录。</p>
     */
    @Override
    public void deleteDepartment(Long id) {
        Department department = departmentRepository.findByIdAndDeleted(id, NOT_DELETED)
                .orElseThrow(() -> new RuntimeException("部门不存在"));
        department.setDeleted(DELETED);

        String suffix = "#DEL#" + department.getId();
        String baseCode = department.getCode() == null ? "" : department.getCode();
        // 原编码过长时截断，保证拼接后不超过字段长度
        int maxBaseLength = Math.max(MAX_CODE_LENGTH - suffix.length(), 0);
        if (baseCode.length() > maxBaseLength) {
            baseCode = baseCode.substring(0, maxBaseLength);
        }
        department.setCode(baseCode + suffix);
        departmentRepository.save(department);
    }

    /**
     * 按ID查询未删除的部门。
     */
    @Override
    @Transactional(readOnly = true)
    public DepartmentDTO getDepartmentById(Long id) {
        Department department = departmentRepository.findByIdAndDeleted(id, NOT_DELETED)
                .orElseThrow(() -> new RuntimeException("部门不存在"));
        return convertToDTO(department);
    }

    /**
     * 查询所有未删除的部门。
     */
    @Override
    @Transactional(readOnly = true)
    public List<DepartmentDTO> getAllDepartments() {
        return departmentRepository.findByDeleted(NOT_DELETED).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 去掉首尾空白，空字符串统一转成 null，便于统一判空。
     *
     * @param value 原始字符串
     * @return 去空白后的字符串，空内容返回 null
     */
    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    /**
     * 实体转 DTO。
     *
     * @param department 部门实体
     * @return 部门DTO
     */
    private DepartmentDTO convertToDTO(Department department) {
        DepartmentDTO dto = new DepartmentDTO();
        dto.setId(department.getId());
        dto.setName(department.getName());
        dto.setCode(department.getCode());
        dto.setDescription(department.getDescription());
        dto.setStatus(department.getStatus());
        dto.setCreateTime(department.getCreateTime());
        dto.setUpdateTime(department.getUpdateTime());
        return dto;
    }
}
