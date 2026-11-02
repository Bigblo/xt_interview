package com.xt.interview.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * 部门数据传输对象（入参、出参共用）。
 *
 * <p>入参只使用 name/code/description/status 四个字段；
 * 出参额外返回 id 与时间字段，方便前端直接展示。</p>
 */
public class DepartmentDTO {

    /** 部门ID */
    private Long id;

    /** 部门名称，必填 */
    private String name;

    /** 部门编码，必填且唯一 */
    private String code;

    /** 部门描述，可空 */
    private String description;

    /** 状态：0-禁用，1-启用，不传默认启用 */
    private Integer status;

    /** 创建时间（出参），格式化为 yyyy-MM-dd HH:mm:ss */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /** 更新时间（出参），格式化为 yyyy-MM-dd HH:mm:ss */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    public DepartmentDTO() {}

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

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
