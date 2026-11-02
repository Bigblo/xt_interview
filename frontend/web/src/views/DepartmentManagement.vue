<template>
  <div class="department-management">
    <div class="page-header">
      <el-button type="primary" :icon="Plus" @click="openCreateDialog">
        添加部门
      </el-button>
      <el-button :icon="Refresh" @click="loadDepartments">刷新</el-button>
    </div>

    <el-table
      v-loading="loading"
      :data="departments"
      border
      stripe
      style="width: 100%"
    >
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="name" label="部门名称" width="160" />
      <el-table-column prop="code" label="部门编码" width="140" />
      <el-table-column
        prop="description"
        label="部门描述"
        min-width="220"
        show-overflow-tooltip
      />
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link :icon="Edit" @click="openEditDialog(row)">
            编辑
          </el-button>
          <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑部门' : '添加部门'"
      width="520px"
      @close="resetForm"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="90px"
      >
        <el-form-item label="部门名称" prop="name">
          <el-input
            v-model="formData.name"
            placeholder="请输入部门名称"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="部门编码" prop="code">
          <el-input
            v-model="formData.code"
            placeholder="请输入部门编码，如 DEV"
            maxlength="50"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="部门描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入部门描述（可不填）"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="formData.status" placeholder="请选择状态" style="width: 100%">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { departmentApi } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Refresh } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'

/** 表格加载状态 */
const loading = ref(false)
/** 保存按钮 loading 状态 */
const submitting = ref(false)
/** 新增/编辑弹窗是否显示 */
const dialogVisible = ref(false)
/** 当前是否为编辑模式 */
const isEdit = ref(false)
/** 正在编辑的部门ID，新增时为 0 */
const editId = ref<number>(0)

/** 部门列表数据 */
const departments = ref<any[]>([])

/** 表单实例，用于校验与重置 */
const formRef = ref<FormInstance>()

/** 弹窗表单数据（新增/编辑共用） */
const formData = reactive({
  /** 部门名称 */
  name: '',
  /** 部门编码 */
  code: '',
  /** 部门描述 */
  description: '',
  /** 状态：0-禁用，1-启用 */
  status: 1
})

/** 表单校验规则：名称必填、编码必填且只允许字母数字下划线短横线 */
const formRules: FormRules = {
  name: [{ required: true, message: '请输入部门名称', trigger: 'blur' }],
  code: [
    { required: true, message: '请输入部门编码', trigger: 'blur' },
    {
      pattern: /^[A-Za-z0-9_-]+$/,
      message: '编码只能包含字母、数字、下划线和短横线',
      trigger: 'blur'
    }
  ],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

/** 加载部门列表 */
async function loadDepartments() {
  loading.value = true
  try {
    departments.value = await departmentApi.getAll()
  } catch (error: any) {
    ElMessage.error(error.message || '加载部门列表失败')
  } finally {
    loading.value = false
  }
}

/** 重置表单为新增时的默认值 */
function resetForm() {
  formData.name = ''
  formData.code = ''
  formData.description = ''
  formData.status = 1
  isEdit.value = false
  editId.value = 0
  formRef.value?.resetFields()
}

/** 打开新增部门弹窗 */
function openCreateDialog() {
  resetForm()
  dialogVisible.value = true
}

/** 打开编辑部门弹窗，回填当前行数据 */
function openEditDialog(row: any) {
  resetForm()
  isEdit.value = true
  editId.value = row.id
  formData.name = row.name
  formData.code = row.code
  formData.description = row.description || ''
  formData.status = row.status
  dialogVisible.value = true
}

/** 提交表单：编辑调用更新，否则调用新增 */
async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return

    submitting.value = true
    try {
      const data = {
        name: formData.name.trim(),
        code: formData.code.trim(),
        description: formData.description.trim(),
        status: formData.status
      }

      if (isEdit.value) {
        await departmentApi.update(editId.value, data)
        ElMessage.success('部门更新成功')
      } else {
        await departmentApi.create(data)
        ElMessage.success('部门创建成功')
      }
      dialogVisible.value = false
      await loadDepartments()
    } catch (error: any) {
      ElMessage.error(error.message || '操作失败')
    } finally {
      submitting.value = false
    }
  })
}

/** 删除部门：二次确认后调用软删除接口并刷新列表 */
async function handleDelete(row: any) {
  try {
    await ElMessageBox.confirm(
      `确定要删除部门 "${row.name}" 吗？`,
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await departmentApi.delete(row.id)
    ElMessage.success('部门删除成功')
    await loadDepartments()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

onMounted(() => {
  loadDepartments()
})
</script>

<style scoped>
.department-management {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
}

.page-header {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}
</style>
