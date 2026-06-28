// ═══════════════════════════════════════════════════════════════
// Developer 1 — 工具函数：树形数据处理测试
// 目标覆盖率：100% | 依赖：无
// 测试命令：npx vitest run test/utils/treeUtils.test.js
// ═══════════════════════════════════════════════════════════════

function filterTree(nodes, keyword) {
  if (!keyword) return nodes
  return (nodes || [])
    .map((node) => {
      const children = filterTree(node.children || [], keyword)
      const hit = String(node.name || '').includes(keyword)
      if (hit || children.length) return { ...node, children }
      return null
    })
    .filter(Boolean)
}

function flattenTree(nodes, acc = []) {
  for (const node of nodes || []) {
    acc.push(node)
    flattenTree(node.children || [], acc)
  }
  return acc
}

const sampleTree = [
  {
    id: '1',
    name: '费率水平',
    children: [
      { id: '1-1', name: '管理费率', children: [] },
      { id: '1-2', name: '运作费率', children: [] },
      {
        id: '1-3',
        name: '托管费率',
        children: [{ id: '1-3-1', name: '银行托管', children: [] }],
      },
    ],
  },
  { id: '2', name: '规模与仓位', children: [] },
]

describe('filterTree', () => {
  it('空关键字应返回原树', () => {
    expect(filterTree(sampleTree, '')).toEqual(sampleTree)
  })

  it('按名称过滤应返回匹配节点及其父节点', () => {
    const result = filterTree(sampleTree, '管理')
    expect(result).toHaveLength(1)
    expect(result[0].children).toHaveLength(1)
    expect(result[0].children[0].name).toBe('管理费率')
  })

  it('多层匹配应保留完整路径', () => {
    const result = filterTree(sampleTree, '银行')
    expect(result).toHaveLength(1)
    expect(result[0].children).toHaveLength(1)
    expect(result[0].children[0].children[0].name).toBe('银行托管')
  })

  it('无匹配应返回空数组', () => {
    expect(filterTree(sampleTree, '不存在的')).toEqual([])
  })

  it('输入 null 应返回空数组', () => {
    expect(filterTree(null, 'test')).toEqual([])
  })
})

describe('flattenTree', () => {
  it('应展开多级树为平面数组', () => {
    const flat = flattenTree(sampleTree)
    // 费率水平(1) + 管理费率(1) + 运作费率(1) + 托管费率(1) + 银行托管(1) + 规模与仓位(1) = 6
    expect(flat).toHaveLength(6)
    expect(flat[0].id).toBe('1')
    expect(flat[5].id).toBe('2')
  })

  it('空树应返回空数组', () => {
    expect(flattenTree([])).toEqual([])
  })

  it('null 输入应返回空数组', () => {
    expect(flattenTree(null)).toEqual([])
  })

  it('单层树应返回相同元素', () => {
    const tree = [{ id: 'a', name: 'A', children: [] }]
    expect(flattenTree(tree)).toHaveLength(1)
  })
})
