// ═══════════════════════════════════════════════════════════════
// Developer 4 — 集成测试：登录 → 跳转流程
// 目标覆盖率：核心流程 | 依赖：@vue/test-utils + vue-router
// 环境：@vitest-environment happy-dom
// 测试命令：npx vitest run test/integration/loginFlow.test.js
// ═══════════════════════════════════════════════════════════════

import { nextTick } from 'vue'
import { shallowMount } from '@vue/test-utils'
import { createRouter, createWebHistory } from 'vue-router'
import LoginPage from '@pages/LoginPage.vue'

// 模拟 api.js 的 login 方法
vi.mock('../../src/api.js', () => ({
  login: vi.fn(),
}))

// 模拟 ElMessage
vi.mock('element-plus', async () => {
  const actual = await vi.importActual('element-plus')
  return {
    ...actual,
    ElMessage: { success: vi.fn(), error: vi.fn() },
  }
})

import { login } from '../../src/api.js'
import { ElMessage } from 'element-plus'

// 创建测试路由
const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', component: LoginPage },
    { path: '/workspace/:role/home', component: { template: '<div>Workspace</div>' } },
  ],
})

function factory() {
  return shallowMount(LoginPage, { global: { plugins: [router] } })
}

describe('登录流程集成测试', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    localStorage.clear()
  })

  it('登录成功：存储 session 并跳转到 workspace', async () => {
    const mockResponse = {
      token: 'jwt-token-xxx',
      account: { displayName: '管理员', userType: 'SYSTEM_ADMIN' },
    }
    login.mockResolvedValue(mockResponse)
    router.push = vi.fn()

    const wrapper = factory()
    await wrapper.vm.handleLogin()
    await nextTick()

    // 验证 session 存入 localStorage
    const stored = JSON.parse(localStorage.getItem('session'))
    expect(stored.token).toBe('jwt-token-xxx')
    expect(stored.account.displayName).toBe('管理员')

    // 验证路由跳转
    expect(router.push).toHaveBeenCalledWith('/workspace/system_admin/home')
  })

  it('登录失败：显示错误提示信息', async () => {
    const error = { response: { data: { message: '账号或密码错误' } } }
    login.mockRejectedValue(error)

    const wrapper = factory()
    await wrapper.vm.handleLogin()
    await nextTick()

    // 验证错误消息被设置
    expect(wrapper.vm.message).toBe('账号或密码错误')
    // 验证 ElMessage.error 被调用
    expect(ElMessage.error).toHaveBeenCalledWith('账号或密码错误')
    // 不应存储 session
    expect(localStorage.getItem('session')).toBeNull()
  })

  it('登录异常：无 response.data 时使用 error.message', async () => {
    const error = new Error('网络连接失败')
    login.mockRejectedValue(error)

    const wrapper = factory()
    await wrapper.vm.handleLogin()
    await nextTick()

    // 验证回退到 error.message
    expect(wrapper.vm.message).toBe('网络连接失败')
    expect(ElMessage.error).toHaveBeenCalledWith('网络连接失败')
    // loading 应已重置
    expect(wrapper.vm.loading).toBe(false)
  })
})
