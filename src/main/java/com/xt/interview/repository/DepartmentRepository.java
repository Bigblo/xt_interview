package com.xt.interview.repository;

import com.xt.interview.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 部门数据访问层。
 *
 * <p>方法均以 {@code deleted} 作为软删除过滤条件，删除标记为 0 表示数据有效。</p>
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    /** 查询指定删除标记下的所有部门 */
    List<Department> findByDeleted(Integer deleted);

    /** 按ID查询未删除的部门，不存在时返回空 Optional */
    Optional<Department> findByIdAndDeleted(Long id, Integer deleted);

    /** 判断未删除的部门中是否存在该ID（用于校验用户关联的部门是否有效） */
    boolean existsByIdAndDeleted(Long id, Integer deleted);

    /** 判断未删除的部门中是否存在该编码（新增时使用） */
    boolean existsByCodeAndDeleted(String code, Integer deleted);

    /** 判断未删除的部门中是否存在该编码，并排除指定ID（更新时使用） */
    boolean existsByCodeAndDeletedAndIdNot(String code, Integer deleted, Long id);

    /** 判断未删除的部门中是否存在该名称（新增时使用） */
    boolean existsByNameAndDeleted(String name, Integer deleted);

    /** 判断未删除的部门中是否存在该名称，并排除指定ID（更新时使用） */
    boolean existsByNameAndDeletedAndIdNot(String name, Integer deleted, Long id);
}
