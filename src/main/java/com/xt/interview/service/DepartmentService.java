package com.xt.interview.service;

import com.xt.interview.dto.DepartmentDTO;

import java.util.List;

/**
 * 部门管理业务接口。
 *
 * <p>约定：查询只返回未删除数据；删除为软删除；编码、名称在未删除范围内唯一。</p>
 */
public interface DepartmentService {

    /**
     * 新增部门。
     *
     * @param dto 部门信息（name、code 必填，status 不传默认启用）
     * @return 新增后的部门（含ID与时间字段）
     */
    DepartmentDTO createDepartment(DepartmentDTO dto);

    /**
     * 更新部门。
     *
     * @param id  部门ID
     * @param dto 待更新的部门信息
     * @return 更新后的部门
     */
    DepartmentDTO updateDepartment(Long id, DepartmentDTO dto);

    /**
     * 软删除部门（同时释放唯一编码）。
     *
     * @param id 部门ID
     */
    void deleteDepartment(Long id);

    /**
     * 按ID查询未删除的部门。
     *
     * @param id 部门ID
     * @return 部门信息
     */
    DepartmentDTO getDepartmentById(Long id);

    /**
     * 查询所有未删除的部门。
     *
     * @return 部门列表
     */
    List<DepartmentDTO> getAllDepartments();
}
