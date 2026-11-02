<template>
  <div class="user-management">
    <div class="page-header">
      <el-button type="primary" :icon="Plus" @click="openCreateDialog">
        添加用户
      </el-button>
      <el-button :icon="Refresh" @click="loadUsers">刷新</el-button>
    </div>

    <div class="search-bar">
      <el-input
        v-model="searchForm.username"
        placeholder="按用户名搜索"
        clearable
        style="width: 200px"
        @keyup.enter="handleSearch"
      />
      <el-select
        v-model="searchForm.status"
        placeholder="状态"
        clearable
        style="width: 120px"
      >
        <el-option label="启用" :value="1" />
        <el-option label="禁用" :value="0" />
      </el-select>
      <el-select
        v-model="searchForm.roleLevel"
        placeholder="权限级别"
        clearable
        style="width: 180px"
      >
        <el-option label="用户权限" :value="1" />
        <el-option label="管理员权限" :value="2" />
        <el-option label="超级管理员权限" :value="3" />
      </el-select>
      <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
      <el-button :icon="RefreshLeft" @click="handleReset">重置</el-button>
    </div>

    <el-table
      v-loading="loading"
      :data="users"
      border
      stripe
      style="width: 100%"
    >
      <el-table-column prop="username" label="用户名" width="120" />
      <el-table-column prop="name" label="姓名" width="100" />
      <el-table-column prop="gender" label="性别" width="80" />
      <el-table-column prop="phone" label="联系方式" width="140" />
      <el-table-column label="所属部门" width="130">
        <template #default="{ row }">
          {{ row.departmentName || '未分配' }}
        </template>
      </el-table-column>
      <el-table-column label="权限级别" width="120">
        <template #default="{ row }">
          <el-tag :type="roleTagType(row.roleLevel)">
            {{ roleText(row.roleLevel) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="80">
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

    <div class="pagination-bar">
      <el-pagination
        :current-page="pagination.page"
        :page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
      />
    </div>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑用户' : '添加用户'"
      width="500px"
      @close="resetForm"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="90px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="formData.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="formData.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-select v-model="formData.gender" placeholder="请选择性别" style="width: 100%">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="联系方式" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入联系方式" />
        </el-form-item>
        <el-form-item label="所属部门" prop="departmentId">
          <el-select
            v-model="formData.departmentId"
            placeholder="请选择所属部门（可不选）"
            clearable
            style="width: 100%"
          >
            <el-option
              v-for="dept in departments"
              :key="dept.id"
              :label="dept.status === 1 ? dept.name : `${dept.name}（已禁用）`"
              :value="dept.id"
              :disabled="dept.status !== 1"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="formData.password"
            type="password"
            show-password
            :placeholder="isEdit ? '留空则不修改密码' : '留空则默认123456'"
          />
        </el-form-item>
        <el-form-item label="权限级别" prop="roleLevel">
          <el-select v-model="formData.roleLevel" placeholder="请选择权限级别" style="width: 100%">
            <el-option label="用户权限" :value="1" />
            <el-option label="管理员权限" :value="2" />
            <el-option label="超级管理员权限" :value="3" />
          </el-select>
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
import { accountApi, departmentApi } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Refresh, Search, RefreshLeft } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'

/** 表格加载状态 */
const loading = ref(false)
/** 保存按钮 loading 状态 */
const submitting = ref(false)
/** 新增/编辑弹窗是否显示 */
const dialogVisible = ref(false)
/** 当前是否为编辑模式 */
const isEdit = ref(false)
/** 正在编辑的用户ID，新增时为 0 */
const editId = ref<number>(0)

/** 当前页的用户列表 */
const users = ref<any[]>([])
/** 部门下拉选项（用于“所属部门”选择与展示） */
const departments = ref<any[]>([])

/** 搜索条件 */
const searchForm = reactive({
  /** 用户名关键字（模糊匹配） */
  username: '',
  /** 状态：0-禁用，1-启用，undefined 表示不筛选 */
  status: undefined as number | undefined,
  /** 权限级别，undefined 表示不筛选 */
  roleLevel: undefined as number | undefined
})

/** 分页状态：page 从 1 开始（传给后端时减 1） */
const pagination = reactive({ page: 1, size: 10, total: 0 })

/** 表单实例，用于校验与重置 */
const formRef = ref<FormInstance>()

/** 弹窗表单数据（新增/编辑共用） */
const formData = reactive({
  /** 登录用户名 */
  username: '',
  /** 姓名 */
  name: '',
  /** 性别 */
  gender: '男',
  /** 联系方式 */
  phone: '',
  /** 密码，编辑时留空表示不修改 */
  password: '',
  /** 所属部门ID，null 表示未分配 */
  departmentId: null as number | null,
  /** 权限级别 */
  roleLevel: 1,
  /** 状态：0-禁用，1-启用 */
  status: 1,
  /** 是否超级管理员 */
  isSuper: 0
})

const formRules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  roleLevel: [{ required: true, message: '请选择权限级别', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

function roleText(level: number): string {
  const map: Record<number, string> = { 1: '用户权限', 2: '管理员权限', 3: '超级管理员' }
  return map[level] || '未知'
}

function roleTagType(level: number): 'info' | 'warning' | 'danger' {
  const map: Record<number, 'info' | 'warning' | 'danger'> = {
    1: 'info',
    2: 'warning',
    3: 'danger'
  }
  return map[level] || 'info'
}

/** el-select 清空后可能得到空串，统一转成 undefined，避免后端类型转换失败 */
function toNumberOrUndefined(value: unknown): number | undefined {
  if (value === '' || value === null || value === undefined) return undefined
  const num = Number(value)
  return Number.isNaN(num) ? undefined : num
}

/** 按当前搜索条件与页码加载用户列表 */
async function loadUsers() {
  loading.value = true
  try {
    const res: any = await accountApi.page({
      username: searchForm.username.trim() || undefined,
      status: toNumberOrUndefined(searchForm.status),
      roleLevel: toNumberOrUndefined(searchForm.roleLevel),
      page: pagination.page - 1,
      size: pagination.size
    })
    users.value = res?.list || []
    pagination.total = res?.totalElements || 0
  } catch (error: any) {
    ElMessage.error(error.message || '加载用户列表失败')
  } finally {
    loading.value = false
  }
}

/** 加载部门下拉数据 */
async function loadDepartments() {
  try {
    departments.value = await departmentApi.getAll()
  } catch (error: any) {
    ElMessage.error(error.message || '加载部门列表失败')
  }
}

/** 点击搜索：回到第一页后查询 */
function handleSearch() {
  pagination.page = 1
  loadUsers()
}

/** 重置搜索条件并回到第一页 */
function handleReset() {
  searchForm.username = ''
  searchForm.status = undefined
  searchForm.roleLevel = undefined
  pagination.page = 1
  loadUsers()
}

/** 翻页：分页组件给的是 1 起的页码 */
function handlePageChange(page: number) {
  pagination.page = page
  loadUsers()
}

/** 修改每页条数：回到第一页后重新查询 */
function handleSizeChange(size: number) {
  pagination.size = size
  pagination.page = 1
  loadUsers()
}

/** 重置表单为新增时的默认值 */
function resetForm() {
  formData.username = ''
  formData.name = ''
  formData.gender = '男'
  formData.phone = ''
  formData.password = ''
  formData.departmentId = null
  formData.roleLevel = 1
  formData.status = 1
  formData.isSuper = 0
  isEdit.value = false
  editId.value = 0
  formRef.value?.resetFields()
}

/** 打开新增用户弹窗 */
function openCreateDialog() {
  resetForm()
  dialogVisible.value = true
}

/** 打开编辑用户弹窗，回填当前行数据 */
function openEditDialog(row: any) {
  resetForm()
  isEdit.value = true
  editId.value = row.id
  formData.username = row.username
  formData.name = row.name
  formData.gender = row.gender || '男'
  formData.phone = row.phone || ''
  formData.departmentId = row.departmentId ?? null
  formData.roleLevel = row.roleLevel
  formData.status = row.status
  formData.isSuper = row.isSuper
  dialogVisible.value = true
}

/** 提交表单：编辑调用更新，否则调用新增；密码留空则不提交该字段 */
async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return

    submitting.value = true
    try {
      const data: any = {
        username: formData.username.trim(),
        name: formData.name.trim(),
        gender: formData.gender,
        phone: formData.phone.trim(),
        departmentId: formData.departmentId ?? null,
        roleLevel: formData.roleLevel,
        status: formData.status,
        isSuper: formData.isSuper
      }
      if (formData.password !== '') {
        data.password = formData.password
      }

      if (isEdit.value) {
        await accountApi.update(editId.value, data)
        ElMessage.success('用户更新成功')
      } else {
        await accountApi.create(data)
        ElMessage.success('用户创建成功')
      }
      dialogVisible.value = false
      await loadUsers()
    } catch (error: any) {
      ElMessage.error(error.message || '操作失败')
    } finally {
      submitting.value = false
    }
  })
}

/** 删除用户：二次确认后软删除，并处理当前页被删空的情况 */
async function handleDelete(row: any) {
  try {
    await ElMessageBox.confirm(
      `确定要删除用户 "${row.username}" 吗？`,
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await accountApi.delete(row.id)
    ElMessage.success('用户删除成功')
    // 删除后若当前页已为空，回退一页
    if (users.value.length === 1 && pagination.page > 1) {
      pagination.page -= 1
    }
    await loadUsers()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

onMounted(() => {
  loadDepartments()
  loadUsers()
})
</script>

<style scoped>
.user-management {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
}

.page-header {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.search-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.pagination-bar {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
