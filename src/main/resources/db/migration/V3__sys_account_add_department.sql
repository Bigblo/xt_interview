-- ==================== 账号表新增所属部门 ====================
ALTER TABLE `sys_account`
    ADD COLUMN `department_id` BIGINT DEFAULT NULL COMMENT '所属部门ID' AFTER `phone`,
    ADD INDEX `idx_account_department` (`department_id`);

-- 外键关联部门表（部门为软删除，实际不会触发级联置空）
ALTER TABLE `sys_account`
    ADD CONSTRAINT `fk_account_department`
        FOREIGN KEY (`department_id`) REFERENCES `sys_department` (`id`)
        ON DELETE SET NULL ON UPDATE CASCADE;
