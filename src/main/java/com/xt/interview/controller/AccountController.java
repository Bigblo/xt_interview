package com.xt.interview.controller;

import com.xt.interview.dto.AccountDTO;
import com.xt.interview.dto.PageResult;
import com.xt.interview.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 账号管理接口。
 *
 * <p>基础路径 {@code /api/accounts}，受 JWT 过滤器保护。</p>
 */
@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "*")
public class AccountController {

    /** 账号业务层 */
    @Autowired
    private AccountService accountService;

    /**
     * 查询所有账号（不分页，保留给需要全量的场景）。
     *
     * @return 未删除的账号列表
     */
    @GetMapping
    public ResponseEntity<List<AccountDTO>> getAll() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    /**
     * 分页 + 条件查询账号。
     *
     * @param username  用户名关键字（模糊匹配），可为空
     * @param status    状态（0-禁用，1-启用），可为空
     * @param roleLevel 权限级别，可为空
     * @param page      页码，从 0 开始
     * @param size      每页条数，默认 10
     * @return 分页结果 {list,totalElements,totalPages,currentPage,pageSize}
     */
    @GetMapping("/page")
    public ResponseEntity<PageResult<AccountDTO>> page(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer roleLevel,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(accountService.pageAccounts(username, status, roleLevel, page, size));
    }

    /**
     * 按ID查询账号。
     *
     * @param id 账号ID
     * @return 账号信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<AccountDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    /**
     * 新增账号。
     *
     * @param dto 账号信息
     * @return 新增后的账号
     */
    @PostMapping
    public ResponseEntity<AccountDTO> create(@RequestBody AccountDTO dto) {
        return ResponseEntity.ok(accountService.createAccount(dto));
    }

    /**
     * 更新账号。
     *
     * @param id  账号ID
     * @param dto 待更新的账号信息
     * @return 更新后的账号
     */
    @PutMapping("/{id}")
    public ResponseEntity<AccountDTO> update(@PathVariable Long id, @RequestBody AccountDTO dto) {
        return ResponseEntity.ok(accountService.updateAccount(id, dto));
    }

    /**
     * 删除账号（软删除）。
     *
     * @param id 账号ID
     * @return 空响应体
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.ok().build();
    }
}
