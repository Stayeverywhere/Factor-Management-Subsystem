// ═══════════════════════════════════════════════════════════════
// Developer 4 — 集成测试：因子查询 → 渲染流程
// 目标覆盖率：核心查询流程 | 依赖：@vue/test-utils + api mock
// 环境：@vitest-environment happy-dom
// 测试命令：npx vitest run test/integration/factorQuery.test.js
// ═══════════════════════════════════════════════════════════════

import { nextTick } from 'vue'
import { shallowMount } from '@vue/test-utils'
import FactorOverviewPage from '@pages/FactorOverviewPage.vue'

// 模拟 api.js 中的因子接口
vi.mock('../../src/api.js', () => ({
  getFactorCategories: vi.fn(),
  getBaseFactors: vi.fn(),
  getDerivativeFactors: vi.fn(),
  getStyleFactors: vi.fn(),
  getBaseFactorValues: vi.fn(),
  getDerivativeFactorValues: vi.fn(),
  getStyleFactorValues: vi.fn(),
  getFunds: vi.fn(),
  createDerivativeFactor: vi.fn(),
  createStyleFactor: vi.fn(),
}))

import {
  getFactorCategories,
  getBaseFactors,
  getDerivativeFactors,
  getStyleFactors,
  getBaseFactorValues,
} from '../../src/api.js'

// 模拟 echarts（组件用 import * as echarts，需导出 init 命名导出）
vi.mock('echarts', () => ({
  init: vi.fn(() => ({
    setOption: vi.fn(),
    dispose: vi.fn(),
  })),
}))

function factory() {
  return shallowMount(FactorOverviewPage, {
    global: {
      stubs: {
        ElButton: true,
        ElSelect: true,
        ElOption: true,
        ElDatePicker: true,
        ElInput: true,
        ElTree: true,
        ElTable: true,
        ElTableColumn: true,
        ElPagination: true,
        ElDialog: true,
        ElForm: true,
        ElFormItem: true,
        ElTransfer: true,
        ElInputNumber: true,
      },
    },
  })
}

// 设置 loadMeta 的默认 mock 返回
function setupMetaMocks() {
  getFactorCategories.mockResolvedValue([
    { id: 'cat-1', name: '费率水平', children: [] },
  ])
  getBaseFactors.mockResolvedValue({
    items: [{ id: 'bf-1', name: '管理费率', categoryId: 'cat-1' }],
    total: 1,
  })
  getDerivativeFactors.mockResolvedValue([
    { id: 'df-1', name: '衍生因子1' },
  ])
  getStyleFactors.mockResolvedValue([
    { id: 'sf-1', name: '风格因子1' },
  ])
}

describe('因子查询 → 渲染流程集成测试', () => {
  beforeEach(() => vi.clearAllMocks())

  it('基础因子查询：loadMeta 后 handleSearch 调用 getBaseFactorValues', async () => {
    setupMetaMocks()
    getBaseFactorValues.mockResolvedValue([
      { tradeDate: '2025-01-01', fundCode: '000001', factorId: 'bf-1', value: 1.23 },
    ])

    const wrapper = factory()
    await nextTick()
    await new Promise(r => setTimeout(r, 0))
    await nextTick()

    expect(getBaseFactors).toHaveBeenCalled()
    expect(getBaseFactorValues).toHaveBeenCalled()
  })

  it('衍生因子查询：factorOptions 中包含衍生因子条目', async () => {
    setupMetaMocks()
    getBaseFactorValues.mockResolvedValue([])

    const wrapper = factory()
    await nextTick()
    await new Promise(r => setTimeout(r, 0))
    await nextTick()

    // 验证 loadMeta 将衍生因子加载到 factorOptions 中
    const derived = wrapper.vm.factorOptions.find(f => f.type === 'derived')
    expect(derived).toBeDefined()
    expect(derived.id).toBe('df-1')
    expect(derived.name).toBe('衍生因子1')
  })

  it('风格因子查询：factorOptions 中包含风格因子条目', async () => {
    setupMetaMocks()
    getBaseFactorValues.mockResolvedValue([])

    const wrapper = factory()
    await nextTick()
    await new Promise(r => setTimeout(r, 0))
    await nextTick()

    // 验证 loadMeta 将风格因子加载到 factorOptions 中
    const style = wrapper.vm.factorOptions.find(f => f.type === 'style')
    expect(style).toBeDefined()
    expect(style.id).toBe('sf-1')
    expect(style.name).toBe('风格因子1')
  })

  it('日期过滤：查询参数包含 startDate 和 endDate', async () => {
    setupMetaMocks()
    getBaseFactorValues.mockResolvedValue([])

    const wrapper = factory()
    await nextTick()
    await new Promise(r => setTimeout(r, 0))
    await nextTick()

    wrapper.vm.query.dateRange = ['2025-01-01', '2025-06-30']
    await wrapper.vm.handleSearch()
    await nextTick()

    const lastCall = getBaseFactorValues.mock.calls.at(-1)
    const params = lastCall?.[0]
    expect(params).toBeDefined()
    expect(params.startDate).toBe('2025-01-01')
    expect(params.endDate).toBe('2025-06-30')
  })

  it('无 factorId 时 handleSearch 不发起请求', async () => {
    setupMetaMocks()
    getBaseFactorValues.mockResolvedValue([])

    const wrapper = factory()
    await nextTick()
    await new Promise(r => setTimeout(r, 0))
    await nextTick()

    wrapper.vm.query.factorId = ''
    vi.clearAllMocks()

    await wrapper.vm.handleSearch()
    await nextTick()

    expect(getBaseFactorValues).not.toHaveBeenCalled()
  })
})
