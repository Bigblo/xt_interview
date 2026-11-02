package com.xt.interview.repository;

import com.xt.interview.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 账号数据访问层。
 *
 * <p>继承 {@link JpaSpecificationExecutor} 以支持分页接口的动态条件查询。</p>
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long>, JpaSpecificationExecutor<Account> {

    /** 查询指定删除标记下的所有账号 */
    List<Account> findByDeleted(Integer deleted);

    /** 按ID查询未删除的账号，不存在时返回空 Optional */
    Optional<Account> findByIdAndDeleted(Long id, Integer deleted);

    /** 按用户名查询未删除的账号（登录、查重使用） */
    Optional<Account> findByUsernameAndDeleted(String username, Integer deleted);

    /** 按用户名 + 密码（MD5 密文）查询未删除的账号 */
    Optional<Account> findByUsernameAndPasswordAndDeleted(String username, String password, Integer deleted);

    /** 判断未删除的账号中用户名是否已存在 */
    boolean existsByUsernameAndDeleted(String username, Integer deleted);
}
