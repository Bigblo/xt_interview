package com.xt.interview.service;

import com.xt.interview.dto.AccountDTO;
import com.xt.interview.dto.PageResult;

import java.util.List;

/**
 * 账号管理业务接口。
 *
 * <p>除登录外均为后台管理能力：查询只返回未删除数据，删除为软删除。</p>
 */
public interface AccountService {

    /**
     * 新增账号，密码为空时使用默认密码 123456。
     *
     * @param dto 账号信息
     * @return 新增后的账号
     */
    AccountDTO createAccount(AccountDTO dto);

    /**
     * 更新账号，密码为空表示不修改。
     *
     * @param id  账号ID
     * @param dto 待更新的账号信息
     * @return 更新后的账号
     */
    AccountDTO updateAccount(Long id, AccountDTO dto);

    /**
     * 软删除账号。
     *
     * @param id 账号ID
     */
    void deleteAccount(Long id);

    /**
     * 按ID查询未删除的账号。
     *
     * @param id 账号ID
     * @return 账号信息
     */
    AccountDTO getAccountById(Long id);

    /**
     * 查询所有未删除的账号。
     *
     * @return 账号列表
     */
    List<AccountDTO> getAllAccounts();

    /**
     * 分页查询账号，支持用户名模糊、状态、权限级别筛选。
     *
     * @param username  用户名关键字，可为空
     * @param status    状态（0-禁用，1-启用），可为空
     * @param roleLevel 权限级别，可为空
     * @param page      页码，从 0 开始
     * @param size      每页条数
     * @return 分页结果
     */
    PageResult<AccountDTO> pageAccounts(String username, Integer status, Integer roleLevel, int page, int size);

    /**
     * 登录校验（当前登录入口为 AuthController，此方法保留给业务层复用）。
     *
     * @param username 用户名
     * @param password 明文密码
     * @return 登录成功的账号信息
     */
    AccountDTO login(String username, String password);
}
