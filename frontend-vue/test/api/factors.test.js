// ═══════════════════════════════════════════════════════════════
// Developer 1 — API：因子接口测试
// 目标覆盖率：100% | 依赖：vitest + axios mock
// 测试命令：npx vitest run test/api/factors.test.js
// ═══════════════════════════════════════════════════════════════

import axios from 'axios'

vi.mock('axios', () => {
  const http = {
    get: vi.fn(), post: vi.fn(), put: vi.fn(), delete: vi.fn(), patch: vi.fn(),
    interceptors: { request: { use: vi.fn() }, response: { use: vi.fn() } },
  }
  return { default: { create: vi.fn(() => http), ...http } }
})

const http = axios.create()
beforeEach(() => vi.clearAllMocks())

// ---------- 待实现的测试函数 ----------
// 请将 api.js 中的函数复制到这里，然后编写测试
async function getFactorCategories() { const { data } = await http.get('/factors/categories'); return data.data }
async function getFunds(params) { const { data } = await http.get('/factors/funds', { params }); return data.data }
async function getBaseFactors(params) { const { data } = await http.get('/factors/base', { params }); return data.data }
async function getBaseFactorValues(params) { const { data } = await http.get('/factors/base/value', { params }); return data.data }
async function getDerivativeFactors() { const { data } = await http.get('/factors/derived'); return data.data }
async function getStyleFactors() { const { data } = await http.get('/factors/style'); return data.data }
async function createDerivativeFactor(payload) { const { data } = await http.post('/factors/derived', payload); return data.data }
async function createStyleFactor(payload) { const { data } = await http.post('/factors/style', payload); return data.data }
async function updateDerivativeFactor(id, payload) { const { data } = await http.put(`/factors/derived/${id}`, payload); return data.data }
async function deleteDerivativeFactor(id) { const { data } = await http.delete(`/factors/derived/${id}`); return data.data }
async function updateStyleFactor(id, payload) { const { data } = await http.put(`/factors/style/${id}`, payload); return data.data }
async function deleteStyleFactor(id) { const { data } = await http.delete(`/factors/style/${id}`); return data.data }

describe('因子查询 API', () => {
  it('getFactorCategories GET /factors/categories', async () => {
    http.get.mockResolvedValue({ data: { data: [{ id: 'cat-1', name: '费率水平' }] } })
    const r = await getFactorCategories()
    expect(http.get).toHaveBeenCalledWith('/factors/categories')
    expect(Array.isArray(r)).toBe(true)
  })

  it('getFunds 支持 params', async () => {
    http.get.mockResolvedValue({ data: { data: [] } })
    await getFunds({ limit: 100 })
    expect(http.get).toHaveBeenCalledWith('/factors/funds', { params: { limit: 100 } })
  })

  it('getBaseFactors 应返回 PageResult 结构', async () => {
    http.get.mockResolvedValue({ data: { data: { items: [{ id: 'bf-1' }], total: 1 } } })
    const r = await getBaseFactors()
    expect(r.items).toBeDefined()
    expect(r.items[0].id).toBe('bf-1')
  })

  it('getBaseFactorValues 应返回数组', async () => {
    http.get.mockResolvedValue({ data: { data: [{ id: 'v1' }] } })
    const r = await getBaseFactorValues({ fundCode: '000001', factorId: 'bf-1' })
    expect(http.get).toHaveBeenCalledWith('/factors/base/value', { params: { fundCode: '000001', factorId: 'bf-1' } })
    expect(Array.isArray(r)).toBe(true)
  })

  it('getDerivativeFactors / getStyleFactors', async () => {
    http.get.mockResolvedValue({ data: { data: [] } })
    await getDerivativeFactors(); await getStyleFactors()
    expect(http.get).toHaveBeenCalledWith('/factors/derived')
    expect(http.get).toHaveBeenCalledWith('/factors/style')
  })
})

describe('因子管理 CRUD API', () => {
  it('createDerivativeFactor POST /factors/derived', async () => {
    http.post.mockResolvedValue({ data: { data: { id: 'df-new' } } })
    const r = await createDerivativeFactor({ name: 'test', items: [] })
    expect(http.post).toHaveBeenCalledWith('/factors/derived', { name: 'test', items: [] })
    expect(r.id).toBe('df-new')
  })

  it('createStyleFactor POST /factors/style', async () => {
    http.post.mockResolvedValue({ data: { data: { id: 'sf-new' } } })
    const r = await createStyleFactor({ name: 'test', items: [] })
    expect(http.post).toHaveBeenCalledWith('/factors/style', { name: 'test', items: [] })
  })

  it('updateDerivativeFactor PUT /factors/derived/:id', async () => {
    http.put.mockResolvedValue({ data: { data: { id: 'df-1' } } })
    await updateDerivativeFactor('df-1', { name: 'new' })
    expect(http.put).toHaveBeenCalledWith('/factors/derived/df-1', { name: 'new' })
  })

  it('deleteDerivativeFactor DELETE /factors/derived/:id', async () => {
    http.delete.mockResolvedValue({ data: { data: 'ok' } })
    await deleteDerivativeFactor('df-1')
    expect(http.delete).toHaveBeenCalledWith('/factors/derived/df-1')
  })

  it('updateStyleFactor PUT /factors/style/:id', async () => {
    http.put.mockResolvedValue({ data: { data: { id: 'sf-1' } } })
    await updateStyleFactor('sf-1', { name: 'new' })
    expect(http.put).toHaveBeenCalledWith('/factors/style/sf-1', { name: 'new' })
  })

  it('deleteStyleFactor DELETE /factors/style/:id', async () => {
    http.delete.mockResolvedValue({ data: { data: 'ok' } })
    await deleteStyleFactor('sf-1')
    expect(http.delete).toHaveBeenCalledWith('/factors/style/sf-1')
  })
})
