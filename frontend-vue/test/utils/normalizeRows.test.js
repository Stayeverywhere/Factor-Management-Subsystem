// ═══════════════════════════════════════════════════════════════
// Developer 1 — 工具函数：normalizeRows 测试
// 目标覆盖率：100% | 依赖：无
// 测试命令：npx vitest run test/utils/normalizeRows.test.js
// ═══════════════════════════════════════════════════════════════

// 模拟被测试函数（从 FactorOverviewPage.vue 提取的核心逻辑）
function normalizeRows(payload, factorOptions = [], funds = []) {
  const source = Array.isArray(payload)
    ? payload
    : (payload?.items || payload?.records || payload?.data || [])
  const result = []
  for (let i = 0; i < source.length; i++) {
    const item = source[i]
    const factorInfo = factorOptions.find(
      (f) =>
        f.id ===
        (item.factorId ??
          item.baseFactorId ??
          item.derivativeFactorId ??
          item.styleFactorId ??
          item.id ??
          '')
    )
    const fundInfo = funds.find((f) => f.fundCode === item.fundCode)
    result.push({
      tradeDate: item.tradeDate ?? item.dataDate ?? item.date ?? '',
      fundCode: item.fundCode ?? item.code ?? '',
      fundName: fundInfo?.fundName || item.fundName || '',
      factorId: factorInfo?.id || item.factorId || item.baseFactorId || '',
      factorName: factorInfo?.name || item.factorName || item.name || '',
      value: item.value ?? item.factorValue ?? item.val ?? '',
      updatedAt: item.updatedAt ?? item.updateTime ?? '',
    })
  }
  return result
}

// ----- 以下为测试用例，请补充完整 -----

describe('normalizeRows — 基础因子值', () => {
  it('应将 baseFactorId 映射为 factorId，dataDate 映射为 tradeDate', () => {
    const input = [{ baseFactorId: 'bf-1', dataDate: '2026-06-28', value: 1.23 }]
    const result = normalizeRows(input)
    expect(result).toHaveLength(1)
    expect(result[0].tradeDate).toBe('2026-06-28')
    expect(result[0].factorId).toBe('bf-1')
    expect(result[0].value).toBe(1.23)
  })

  it('当 factorOptions 传入时应正确匹配 factorName', () => {
    const input = [{ baseFactorId: 'bf-1', dataDate: '2026-06-28', value: 1.23 }]
    const options = [{ id: 'bf-1', name: '管理费率', type: 'base' }]
    const result = normalizeRows(input, options)
    expect(result[0].factorName).toBe('管理费率')
  })

  it('当 funds 传入时应正确匹配 fundName', () => {
    const input = [{ baseFactorId: 'bf-1', dataDate: '2026-06-28', fundCode: '000001', value: 1 }]
    const funds = [{ fundCode: '000001', fundName: '华夏成长混合' }]
    const result = normalizeRows(input, [], funds)
    expect(result[0].fundName).toBe('华夏成长混合')
  })

  it('输入空数组应返回空数组', () => {
    expect(normalizeRows([])).toEqual([])
  })

  it('输入 null 应返回空数组', () => {
    expect(normalizeRows(null)).toEqual([])
  })

  it('应处理衍生因子值结构（derivativeFactorId）', () => {
    const input = [{ derivativeFactorId: 'df-daily-ret', dataDate: '2026-06-28', value: 0.5 }]
    const options = [{ id: 'df-daily-ret', name: '日收益率', type: 'derived' }]
    const result = normalizeRows(input, options)
    expect(result[0].factorName).toBe('日收益率')
  })

  it('应处理风格因子值结构（styleFactorId）', () => {
    const input = [{ styleFactorId: 'sf-1', dataDate: '2026-06-28', value: 0.12 }]
    const options = [{ id: 'sf-1', name: '稳健风格因子', type: 'style' }]
    const result = normalizeRows(input, options)
    expect(result[0].factorName).toBe('稳健风格因子')
  })

  it('应处理 PageResult 结构 { items: [...] }', () => {
    const input = { items: [{ baseFactorId: 'bf-1', dataDate: '2026-01-01', value: 1 }] }
    const result = normalizeRows(input)
    expect(result).toHaveLength(1)
  })

  it('缺失字段时应使用空字符串默认值', () => {
    const result = normalizeRows([{ fundCode: '000001' }])
    expect(result[0].tradeDate).toBe('')
    expect(result[0].fundName).toBe('')
    expect(result[0].factorName).toBe('')
    expect(result[0].value).toBe('')
  })

  it('应处理多条混合数据', () => {
    const input = [
      { baseFactorId: 'bf-1', dataDate: '2026-06-28', fundCode: '000001', value: 1.1 },
      { baseFactorId: 'bf-2', dataDate: '2026-06-27', fundCode: '000002', value: 2.2 },
    ]
    const result = normalizeRows(input)
    expect(result).toHaveLength(2)
    expect(result[0].value).toBe(1.1)
    expect(result[1].value).toBe(2.2)
  })
})
