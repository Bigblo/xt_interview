package com.xt.interview.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * 账号数据传输对象（入参、出参共用）。
 *
 * <p>出参不包含 password 字段；额外返回 {@code departmentId}/{@code departmentName}，
 * 让前端可以直接展示“所属部门”而无需再查一次部门接口。</p>
 */
public class AccountDTO {

    /** 账号ID */
    private Long id;

    /** 登录用户名，必填且唯一 */
    private String username;

    /** 姓名，必填 */
    private String name;

    /** 性别：男/女 */
    private String gender;

    /** 联系方式 */
    private String phone;

    /** 密码（入参为明文，仅新增或修改时传；出参不返回） */
    private String password;

    /** 所属部门ID，可空（部门为可选字段） */
    private Long departmentId;

    /** 所属部门名称（出参，由后端按 departmentId 关联查询填充） */
    private String departmentName;

    /** 权限级别：1-用户权限，2-管理员权限，3-超级管理员权限 */
    private Integer roleLevel;

    /** 状态：0-禁用，1-启用 */
    private Integer status;

    /** 是否超级管理员：0-否，1-是 */
    private Integer isSuper;

    /** 创建时间（出参），格式化为 yyyy-MM-dd HH:mm:ss */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /** 更新时间（出参），格式化为 yyyy-MM-dd HH:mm:ss */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    public AccountDTO() {}

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

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public Integer getRoleLevel() { return roleLevel; }
    public void setRoleLevel(Integer roleLevel) { this.roleLevel = roleLevel; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Integer getIsSuper() { return isSuper; }
    public void setIsSuper(Integer isSuper) { this.isSuper = isSuper; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
