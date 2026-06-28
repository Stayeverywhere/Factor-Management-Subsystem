// ═══════════════════════════════════════════════════════════════
// Developer 1 — 工具函数：日期工具测试
// 目标覆盖率：100% | 依赖：无
// 测试命令：npx vitest run test/utils/dateUtils.test.js
// ═══════════════════════════════════════════════════════════════

function formatDate(date) {
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  return `${y}-${m}-${d}`
}

function defaultDateRange() {
  const end = new Date()
  const start = new Date()
  start.setMonth(start.getMonth() - 3)
  return [formatDate(start), formatDate(end)]
}

describe('formatDate', () => {
  it('应将 Date 转为 YYYY-MM-DD 格式', () => {
    const date = new Date(2026, 5, 15)
    expect(formatDate(date)).toBe('2026-06-15')
  })

  it('月份小于10时应补零', () => {
    expect(formatDate(new Date(2026, 0, 5))).toBe('2026-01-05')
  })

  it('日期小于10时应补零', () => {
    expect(formatDate(new Date(2026, 11, 3))).toBe('2026-12-03')
  })

  it('跨年日期应正确', () => {
    expect(formatDate(new Date(2025, 11, 31))).toBe('2025-12-31')
  })
})

describe('defaultDateRange', () => {
  it('应返回长度为2的数组', () => {
    expect(defaultDateRange()).toHaveLength(2)
  })

  it('第一个日期应早于第二个', () => {
    const [start, end] = defaultDateRange()
    expect(start < end).toBe(true)
  })

  it('两个日期均为 YYYY-MM-DD 格式字符串', () => {
    const [start, end] = defaultDateRange()
    expect(start).toMatch(/^\d{4}-\d{2}-\d{2}$/)
    expect(end).toMatch(/^\d{4}-\d{2}-\d{2}$/)
  })

  it('起始日期约为当前日期的3个月前', () => {
    const [start] = defaultDateRange()
    const now = new Date()
    const expectedStart = new Date(now)
    expectedStart.setMonth(expectedStart.getMonth() - 3)
    const expectedStr = formatDate(expectedStart)
    expect(start).toBe(expectedStr)
  })
})
