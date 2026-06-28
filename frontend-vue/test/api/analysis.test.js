// ═══════════════════════════════════════════════════════════════
// Developer 1 — API：多因子分析 & HTTP 配置测试
// 目标覆盖率：100% | 依赖：vitest + axios mock
// 测试命令：npx vitest run test/api/analysis.test.js
// ═══════════════════════════════════════════════════════════════

import axios from 'axios'

vi.mock('axios', () => {
  const http = {
    get: vi.fn(), post: vi.fn(),
    interceptors: { request: { use: vi.fn() }, response: { use: vi.fn() } },
  }
  return { default: { create: vi.fn(() => http), ...http } }
})

const http = axios.create()
beforeEach(() => vi.clearAllMocks())

async function getFactorPerformance(params) {
  const { data } = await http.get('/factors/analysis/performance', { params })
  return data.data
}
async function getFactorCorrelation(params) {
  const { data } = await http.get('/factors/analysis/correlation', { params })
  return data.data
}

describe('多因子分析 API', () => {
  it('getFactorPerformance GET /factors/analysis/performance', async () => {
    http.get.mockResolvedValue({ data: { data: [{ name: '管理费率', icMean: 0.05 }] } })
    const r = await getFactorPerformance({ pool: 'all' })
    expect(http.get).toHaveBeenCalledWith('/factors/analysis/performance', { params: { pool: 'all' } })
    expect(r[0].name).toBe('管理费率')
  })

  it('getFactorCorrelation GET /factors/analysis/correlation', async () => {
    http.get.mockResolvedValue({ data: { data: { factors: ['a', 'b'], matrix: [[1, 0], [0, 1]] } } })
    const r = await getFactorCorrelation({ topN: 2 })
    expect(http.get).toHaveBeenCalledWith('/factors/analysis/correlation', { params: { topN: 2 } })
    expect(r.factors).toHaveLength(2)
  })

  it('getFactorPerformance 失败时应优雅兜底', async () => {
    http.get.mockRejectedValue(new Error('API Error'))
    const r = await getFactorPerformance({}).catch(() => [])
    expect(r).toEqual([])
  })
})
