package com.xt.interview.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 部门实体，对应数据表 {@code sys_department}。
 *
 * <p>删除方式为软删除（{@code deleted} 置 1），因此所有业务查询都必须带 {@code deleted} 条件。</p>
 */
@Entity
@Table(name = "sys_department")
public class Department {

    /** 部门ID，自增主键 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 部门名称，未删除范围内不允许重复（由 service 校验） */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /** 部门编码，数据库唯一键；软删除时会被改写成 {@code 原编码#DEL#id} 以释放编码 */
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    /** 部门描述，可为空 */
    @Column(name = "description", length = 500)
    private String description;

    /** 状态：0-禁用，1-启用 */
    @Column(name = "status")
    private Integer status;

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
     * 插入前的默认值填充：时间戳 + status/deleted 的兜底值。
     */
    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
        if (status == null) status = 1;
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

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Integer getDeleted() { return deleted; }
    public void setDeleted(Integer deleted) { this.deleted = deleted; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
