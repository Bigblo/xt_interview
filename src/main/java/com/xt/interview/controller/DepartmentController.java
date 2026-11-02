package com.xt.interview.controller;

import com.xt.interview.dto.DepartmentDTO;
import com.xt.interview.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门管理接口。
 *
 * <p>基础路径 {@code /api/departments}，受 JWT 过滤器保护（/api/auth/** 之外都需要 token）。</p>
 */
@RestController
@RequestMapping("/api/departments")
@CrossOrigin(origins = "*")
public class DepartmentController {

    /** 部门业务层 */
    @Autowired
    private DepartmentService departmentService;

    /**
     * 查询所有部门。
     *
     * @return 未删除的部门列表
     */
    @GetMapping
    public ResponseEntity<List<DepartmentDTO>> getAll() {
        return ResponseEntity.ok(departmentService.getAllDepartments());
    }

    /**
     * 按ID查询部门。
     *
     * @param id 部门ID
     * @return 部门信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<DepartmentDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(departmentService.getDepartmentById(id));
    }

    /**
     * 新增部门。
     *
     * @param dto 部门信息
     * @return 新增后的部门
     */
    @PostMapping
    public ResponseEntity<DepartmentDTO> create(@RequestBody DepartmentDTO dto) {
        return ResponseEntity.ok(departmentService.createDepartment(dto));
    }

    /**
     * 更新部门。
     *
     * @param id  部门ID
     * @param dto 待更新的部门信息
     * @return 更新后的部门
     */
    @PutMapping("/{id}")
    public ResponseEntity<DepartmentDTO> update(@PathVariable Long id, @RequestBody DepartmentDTO dto) {
        return ResponseEntity.ok(departmentService.updateDepartment(id, dto));
    }

    /**
     * 删除部门（软删除）。
     *
     * @param id 部门ID
     * @return 空响应体
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.ok().build();
    }
}
