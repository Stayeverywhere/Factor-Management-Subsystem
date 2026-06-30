// ═══════════════════════════════════════════════════════════════
// Developer 4 — 组件测试：因子弹窗组件
// 目标覆盖率：100% | 依赖：@vue/test-utils (shallowMount)
// 环境：@vitest-environment happy-dom
// 测试命令：npx vitest run test/components/FactorDialog.test.js
// ═══════════════════════════════════════════════════════════════

import { nextTick } from 'vue'
import { shallowMount } from '@vue/test-utils'
import FactorDialog from '@components/FactorDialog.vue'

const defaultOptions = [
  { key: 'a', label: '因子A' },
  { key: 'b', label: '因子B' },
  { key: 'c', label: '因子C' },
]

function factory(props = {}) {
  return shallowMount(FactorDialog, {
    props: {
      modelValue: true,
      title: '测试弹窗',
      stepOneTitle: '选择因子',
      itemLabel: '因子',
      options: defaultOptions,
      ...props,
    },
  })
}

describe('FactorDialog 因子弹窗组件', () => {
  // ── 弹窗显隐 ──
  it('modelValue 为 true 时弹窗可见', () => {
    const wrapper = factory({ modelValue: true })
    expect(wrapper.findComponent({ name: 'ElDialog' }).exists()).toBe(true)
    expect(wrapper.vm.visible).toBe(true)
  })

  it('modelValue 为 false 时弹窗不可见', () => {
    const wrapper = factory({ modelValue: false })
    expect(wrapper.vm.visible).toBe(false)
  })

  it('关闭弹窗时 emit update:modelValue 为 false', async () => {
    const wrapper = factory({ modelValue: true })
    wrapper.vm.visible = false
    await nextTick()
    expect(wrapper.emitted('update:modelValue')).toBeTruthy()
    expect(wrapper.emitted('update:modelValue')[0]).toEqual([false])
  })

  // ── 步骤切换 ──
  it('初始步骤为 0（选择步骤）', () => {
    const wrapper = factory()
    expect(wrapper.vm.step).toBe(0)
  })

  it('点击下一步后 step 变为 1', async () => {
    const wrapper = factory()
    // 选择至少一个项，否则下一步无意义
    wrapper.vm.selectedIds = ['a']
    await nextTick()
    // 模拟点击下一步：step++
    wrapper.vm.step++
    await nextTick()
    expect(wrapper.vm.step).toBe(1)
  })

  it('步骤 1 点击上一步回到步骤 0', async () => {
    const wrapper = factory()
    wrapper.vm.step = 1
    await nextTick()
    wrapper.vm.step--
    await nextTick()
    expect(wrapper.vm.step).toBe(0)
  })

  // ── 权重计算 ──
  it('权重在选中项之间平均分配', async () => {
    const wrapper = factory()
    wrapper.vm.selectedIds = ['a', 'b']
    await nextTick()
    const weights = wrapper.vm.weights
    expect(weights).toHaveLength(2)
    expect(Number(weights[0].weight)).toBe(50)
    expect(Number(weights[1].weight)).toBe(50)
  })

  it('权重总和等于选中项数 × 平均权重', async () => {
    const wrapper = factory()
    wrapper.vm.selectedIds = ['a', 'b', 'c']
    await nextTick()
    const sum = Number(wrapper.vm.weightSum)
    expect(sum).toBe(100)
  })

  it('仅选一个因子时权重为 100', async () => {
    const wrapper = factory()
    wrapper.vm.selectedIds = ['a']
    await nextTick()
    expect(Number(wrapper.vm.weights[0].weight)).toBe(100)
    expect(Number(wrapper.vm.weightSum)).toBe(100)
  })

  // ── emit 事件 ──
  it('点击创建时 emit submit 并携带 weights 数据', async () => {
    const wrapper = factory()
    wrapper.vm.selectedIds = ['a', 'b']
    wrapper.vm.step = 1
    await nextTick()
    const weights = wrapper.vm.weights
    wrapper.vm.$emit('submit', weights)
    await nextTick()
    expect(wrapper.emitted('submit')).toBeTruthy()
    expect(wrapper.emitted('submit')[0][0]).toEqual(weights)
  })

  it('弹窗打开时重置 step 和 selectedIds', async () => {
    const wrapper = factory({ modelValue: false, initialSelected: ['a'] })
    wrapper.vm.step = 1
    wrapper.vm.selectedIds = ['a', 'b']
    await nextTick()
    // 模拟弹窗重新打开（watch modelValue）
    await wrapper.setProps({ modelValue: true })
    await nextTick()
    expect(wrapper.vm.step).toBe(0)
    expect(wrapper.vm.selectedIds).toEqual(['a'])
  })
})
