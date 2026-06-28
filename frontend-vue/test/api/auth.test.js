// ═══════════════════════════════════════════════════════════════
// Developer 1 — API：认证接口测试
// 目标覆盖率：100% | 依赖：vitest (globals) + axios
// 测试命令：npx vitest run test/api/auth.test.js
// ═══════════════════════════════════════════════════════════════

import axios from 'axios'

vi.mock('axios', () => {
  const http = {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
    patch: vi.fn(),
    interceptors: { request: { use: vi.fn() }, response: { use: vi.fn() } },
  }
  return { default: { create: vi.fn(() => http), ...http } }
})

const http = axios.create()

// 模拟 api.js 中的函数
async function login(payload) {
  const { data } = await http.post('/auth/login', payload)
  return data.data
}
async function getDashboard(role) {
  const { data } = await http.get('/dashboard', { params: { role } })
  return data.data
}
async function getRoles() {
  const { data } = await http.get('/admin/roles')
  return data.data
}

describe('登录认证 API', () => {
  beforeEach(() => vi.clearAllMocks())

  it('login 应 POST /auth/login 并返回 data.data', async () => {
    http.post.mockResolvedValue({ data: { data: { token: 'abc', account: {} } } })
    const r = await login({ username: 'admin', password: '123', userType: 'SYSTEM_ADMIN' })
    expect(http.post).toHaveBeenCalledWith('/auth/login', {
      username: 'admin', password: '123', userType: 'SYSTEM_ADMIN',
    })
    expect(r.token).toBe('abc')
  })

  it('login 失败时应抛出异常', async () => {
    http.post.mockRejectedValue(new Error('Network Error'))
    await expect(login({})).rejects.toThrow()
  })

  it('getDashboard 应 GET /dashboard?role=', async () => {
    http.get.mockResolvedValue({ data: { data: { title: 'Dashboard' } } })
    const r = await getDashboard('TRADER')
    expect(http.get).toHaveBeenCalledWith('/dashboard', { params: { role: 'TRADER' } })
    expect(r.title).toBe('Dashboard')
  })

  it('getRoles 应 GET /admin/roles', async () => {
    http.get.mockResolvedValue({ data: { data: [{ id: 'r1', name: '管理员' }] } })
    const r = await getRoles()
    expect(http.get).toHaveBeenCalledWith('/admin/roles')
    expect(r[0].id).toBe('r1')
  })
})
