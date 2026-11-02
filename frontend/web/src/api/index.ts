import request from '@/utils/request'

// ==================== 认证相关 API ====================
export const authApi = {
  /** 登录，返回 { success, message, token, user } */
  login(data: { username: string; password: string }) {
    return request.post('/auth/login', data)
  },
  /** 退出登录（服务端无状态，仅返回成功标识） */
  logout() {
    return request.post('/auth/logout')
  },
  /** 校验当前 token 是否有效 */
  validate() {
    return request.get('/auth/validate')
  }
}

// ==================== 账号管理 API ====================
export const accountApi = {
  /** 查询全部账号（不分页） */
  getAll() {
    return request.get('/accounts')
  },
  /** 按ID查询账号 */
  getById(id: number) {
    return request.get(`/accounts/${id}`)
  },
  /** 新增账号 */
  create(data: any) {
    return request.post('/accounts', data)
  },
  /** 更新账号 */
  update(id: number, data: any) {
    return request.put(`/accounts/${id}`, data)
  },
  /** 删除账号（后端为软删除） */
  delete(id: number) {
    return request.delete(`/accounts/${id}`)
  },
  /**
   * 分页 + 条件查询账号。
   * page 从 0 开始；username 为模糊匹配；status/roleLevel 传 undefined 表示不筛选。
   * 返回 { list, totalElements, totalPages, currentPage, pageSize }
   */
  page(params: {
    username?: string
    status?: number
    roleLevel?: number
    page: number
    size: number
  }) {
    return request.get('/accounts/page', { params })
  }
}

// ==================== 部门管理 API ====================
export const departmentApi = {
  /** 查询全部未删除的部门 */
  getAll() {
    return request.get('/departments')
  },
  /** 按ID查询部门 */
  getById(id: number) {
    return request.get(`/departments/${id}`)
  },
  /** 新增部门 */
  create(data: any) {
    return request.post('/departments', data)
  },
  /** 更新部门 */
  update(id: number, data: any) {
    return request.put(`/departments/${id}`, data)
  },
  /** 删除部门（后端为软删除） */
  delete(id: number) {
    return request.delete(`/departments/${id}`)
  }
}
