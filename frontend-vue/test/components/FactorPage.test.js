// ═══════════════════════════════════════════════════════════════
// Developer 4 — 组件测试：因子页面组件
// 目标覆盖率：100% | 依赖：@vue/test-utils (shallowMount)
// 环境：@vitest-environment happy-dom
// 测试命令：npx vitest run test/components/FactorPage.test.js
// ═══════════════════════════════════════════════════════════════

import { shallowMount } from '@vue/test-utils'
import FactorPage from '@components/FactorPage.vue'
import FactorOverviewPage from '@pages/FactorOverviewPage.vue'

function factory() {
  return shallowMount(FactorPage, {
    global: {
      stubs: { FactorOverviewPage: true },
    },
  })
}

describe('FactorPage 因子页面组件', () => {
  it('应渲染 FactorOverviewPage 子组件', () => {
    const wrapper = factory()
    const child = wrapper.findComponent({ name: 'FactorOverviewPage' })
    expect(child.exists()).toBe(true)
  })

  it('FactorOverviewPage 子组件应以 stub 形式存在', () => {
    const wrapper = factory()
    // shallowMount 下 FactorOverviewPage 被替换为 stub
    const stub = wrapper.findComponent({ name: 'FactorOverviewPage-stub' })
    // stub 形式下组件名带 -stub 后缀，或者通过原组件名查找
    const child = wrapper.findComponent(FactorOverviewPage)
    expect(child.exists()).toBe(true)
  })

  it('根模板应包含 FactorOverviewPage', () => {
    const wrapper = factory()
    // FactorPage 模板只有 <FactorOverviewPage />
    expect(wrapper.html()).toContain('factor-overview-page-stub')
  })
})
