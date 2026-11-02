package com.xt.interview.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 账号实体，对应数据表 {@code sys_account}。
 *
 * <p>删除方式为软删除（{@code deleted} 置 1）；
 * 通过 {@code departmentId} 关联部门表（数据库外键 fk_account_department）。</p>
 */
@Entity
@Table(name = "sys_account")
public class Account {

    /** 账号ID，自增主键 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 登录用户名，唯一 */
    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    /** 姓名 */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /** 性别：男/女 */
    @Column(name = "gender", length = 10)
    private String gender;

    /** 联系方式 */
    @Column(name = "phone", length = 20)
    private String phone;

    /** 所属部门ID，可空；仅保存关联ID，避免实体间双向关联带来的懒加载与循环序列化问题 */
    @Column(name = "department_id")
    private Long departmentId;

    /** 密码（MD5 密文） */
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    /** 权限级别：1-用户权限，2-管理员权限，3-超级管理员权限 */
    @Column(name = "role_level")
    private Integer roleLevel;

    /** 状态：0-禁用，1-启用 */
    @Column(name = "status")
    private Integer status;

    /** 是否超级管理员：0-否，1-是 */
    @Column(name = "is_super")
    private Integer isSuper;

    /** 是否删除：0-未删除，1-已删除 */
    @Column(name = "deleted")
    private Integer deleted;

    /** 创建时间，仅在插入时写入 */
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    /** 更新时间，插入和更新时自动刷新 */
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    /**
     * 插入前的默认值填充：时间戳 + 权限/状态/是否超管/删除标记的兜底值。
     */
    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
        if (roleLevel == null) roleLevel = 1;
        if (status == null) status = 1;
        if (isSuper == null) isSuper = 0;
        if (deleted == null) deleted = 0;
    }

    /**
     * 更新前刷新更新时间。
     */
    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Integer getRoleLevel() { return roleLevel; }
    public void setRoleLevel(Integer roleLevel) { this.roleLevel = roleLevel; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Integer getIsSuper() { return isSuper; }
    public void setIsSuper(Integer isSuper) { this.isSuper = isSuper; }

    public Integer getDeleted() { return deleted; }
    public void setDeleted(Integer deleted) { this.deleted = deleted; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
