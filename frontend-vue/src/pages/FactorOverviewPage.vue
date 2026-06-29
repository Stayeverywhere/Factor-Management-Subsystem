<template>
  <section class="panel factor-overview-page">
    <div class="factor-actions">
      <div class="factor-actions-left">
        <el-button type="primary" @click="openCreateDialog('derived')">+ 创建衍生因子</el-button>
        <el-button @click="openCreateDialog('style')">+ 创建风格因子</el-button>
      </div>
      <el-button plain @click="exportData">数据导出</el-button>
    </div>

    <div class="factor-filter-grid">
      <el-select v-model="query.fundCode" filterable clearable placeholder="基金选择" style="width: 100%" @change="handleSearch" @visible-change="loadFunds">
        <el-option v-for="fund in funds" :key="fund.fundCode" :label="fund.fundName" :value="fund.fundCode" />
      </el-select>
      <el-select v-model="query.factorId" filterable clearable placeholder="因子选择" style="width: 100%" @change="handleSearch">
        <el-option v-for="factor in factorOptions" :key="factor.id" :label="factor.name" :value="factor.id" />
      </el-select>
      <el-date-picker v-model="query.dateRange" type="daterange" value-format="YYYY-MM-DD" start-placeholder="开始日期" end-placeholder="结束日期" @change="handleSearch" />
      <div class="factor-filter-actions">
        <el-button type="primary" :loading="loading" @click="handleSearch">查询</el-button>
        <el-button @click="resetFilter">重置</el-button>
      </div>
    </div>

    <div class="factor-content-grid">
      <aside class="factor-tree">
        <el-input v-model="treeKeyword" placeholder="搜索因子树" clearable />
        <el-tree
          ref="treeRef"
          class="tree-section"
          :data="filteredTree"
          node-key="id"
          :props="treeProps"
          default-expand-all
          highlight-current
          :expand-on-click-node="false"
          @node-click="chooseFactor"
        >
          <template #default="{ data }">
            <span class="tree-node-label" :class="{ active: query.factorId === data.id }">{{ data.name }}</span>
          </template>
        </el-tree>
      </aside>

      <div class="factor-main">
        <div ref="chartRef" class="chart-placeholder factor-chart"></div>

        <el-table :data="pagedRows" stripe v-loading="loading">
          <el-table-column prop="tradeDate" label="日期" width="140" />
          <el-table-column prop="fundCode" label="基金代码" width="120" />
          <el-table-column prop="fundName" label="基金名称" min-width="160" />
          <el-table-column prop="factorName" label="因子名称" min-width="160" />
          <el-table-column prop="value" label="因子数值" width="140" />
        </el-table>

        <div class="factor-pagination">
          <span class="pagination-total">共 {{ rows.length }} 条</span>
          <el-pagination
            v-model:current-page="page.page"
            v-model:page-size="page.pageSize"
            :page-sizes="[10, 20, 50]"
            :total="rows.length"
            layout="sizes, prev, pager, next, jumper"
            @size-change="renderChart"
            @current-change="renderChart"
          />
        </div>
      </div>
    </div>

    <el-dialog v-model="dialog.visible" :title="dialogTitle" width="760px" destroy-on-close>
      <el-form :model="dialog.form" label-width="120px">
        <el-form-item label="因子名称">
          <el-input v-model="dialog.form.name" placeholder="请输入因子名称" />
        </el-form-item>
        <el-form-item :label="dialogType === 'derived' ? '基础因子' : '衍生因子'">
          <el-transfer
            v-model="dialog.selectedIds"
            filterable
            :data="dialog.transferOptions"
            :titles="['可选项', '已选项']"
            :props="transferProps"
          />
        </el-form-item>
        <el-form-item label="权重设置">
          <el-table :data="dialog.weightRows" border>
            <el-table-column prop="name" label="名称" />
            <el-table-column label="权重">
              <template #default="{ row }">
                <el-input-number v-model="row.weight" :min="0" :max="100" :step="1" />
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.visible = false">取消</el-button>
        <el-button type="primary" :loading="dialog.saving" @click="submitDialog">创建</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, reactive, ref, watch } from 'vue'
import * as echarts from 'echarts'
import {
  createDerivativeFactor,
  createStyleFactor,
  getBaseFactorValues,
  getBaseFactors,
  getDerivativeFactors,
  getFactorCategories,
  getFunds,
  getStyleFactors
} from '../api'

const loading = ref(false)
const chartRef = ref(null)
const chartInstance = ref(null)
const treeRef = ref(null)
const treeKeyword = ref('')
const treeData = ref([])
const factorOptions = ref([])
const funds = ref([])
const rows = ref([])
const query = reactive({ fundCode: '', factorId: '', dateRange: [] })
const page = reactive({ page: 1, pageSize: 10 })
const dialog = reactive({ visible: false, type: 'derived', form: { name: '' }, selectedIds: [], transferOptions: [], weightRows: [], saving: false })

const treeProps = { children: 'children', label: 'name' }
const transferProps = { key: 'id', label: 'label' }
const dialogType = computed(() => dialog.type)
const dialogTitle = computed(() => dialog.type === 'derived' ? '创建衍生因子' : '创建风格因子')
const pagedRows = computed(() => rows.value.slice((page.page - 1) * page.pageSize, page.page * page.pageSize))
const filteredTree = computed(() => filterTree(treeData.value, treeKeyword.value))

function normalizeRows(payload) {
  const source = Array.isArray(payload) ? payload : (payload?.items || payload?.records || payload?.data || [])
  const result = []
  for (let i = 0; i < source.length; i++) {
    const item = source[i]
    const factorInfo = factorOptions.value.find(f => f.id === (item.factorId ?? item.baseFactorId ?? item.derivativeFactorId ?? item.styleFactorId ?? item.id ?? ''))
    const fundInfo = funds.value.find(f => f.fundCode === item.fundCode)
    result.push({
      tradeDate: item.tradeDate ?? item.dataDate ?? item.date ?? '',
      fundCode: item.fundCode ?? item.code ?? '',
      fundName: fundInfo?.fundName || item.fundName || item.fundCode || '',
      factorId: factorInfo?.id || item.factorId || item.baseFactorId || '',
      factorName: factorInfo?.name || item.factorName || item.factorId || item.baseFactorId || item.derivativeFactorId || item.styleFactorId || '',
      value: item.value ?? item.factorValue ?? item.val ?? '',
      updatedAt: item.updatedAt ?? item.updateTime ?? ''
    })
  }
  return result
}

function filterTree(nodes, keyword) {
  if (!keyword) return nodes
  return (nodes || []).map((node) => {
    const children = filterTree(node.children || [], keyword)
    const hit = String(node.name || '').includes(keyword)
    if (hit || children.length) return { ...node, children }
    return null
  }).filter(Boolean)
}

function formatDate(date) {
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

function defaultDateRange() {
  const end = new Date()
  const start = new Date()
  start.setMonth(start.getMonth() - 3)
  return [formatDate(start), formatDate(end)]
}

function flattenTree(nodes, acc = []) {
  for (const node of nodes || []) {
    acc.push(node)
    flattenTree(node.children || [], acc)
  }
  return acc
}

async function loadMeta() {
  const [categories, baseFactorsResp, derivedFactors, styleFactors] = await Promise.all([
    getFactorCategories().catch(() => []),
    getBaseFactors().catch(() => ({ items: [] })),
    getDerivativeFactors().catch(() => []),
    getStyleFactors().catch(() => [])
  ])

  // getBaseFactors 返回 PageResult { items, page, size, total }
  const baseFactors = baseFactorsResp?.items || baseFactorsResp || []

  // 基金列表改为点击下拉框时懒加载
  funds.value = []

  factorOptions.value = [
    ...(baseFactors || []).map(item => ({ id: item.id, name: item.name, type: 'base' })),
    ...(derivedFactors || []).map(item => ({ id: item.id, name: item.name, type: 'derived' })),
    ...(styleFactors || []).map(item => ({ id: item.id, name: item.name, type: 'style' }))
  ]

  // 构建因子树：分类下挂载对应的基础因子
  function attachBaseFactors(catNodes) {
    return (catNodes || []).map(cat => {
      const kids = attachBaseFactors(cat.children)
      // 查找属于该分类的基础因子
      const factors = (baseFactors || [])
        .filter(f => f.categoryId === cat.id)
        .map(f => ({ id: f.id, name: f.name }))
      const children = [...kids, ...factors]
      return { ...cat, children }
    })
  }

  treeData.value = [
    { id: 'base-root', name: '基础因子库', children: attachBaseFactors(categories || []) },
    { id: 'derived-root', name: '衍生因子库', children: (derivedFactors || []).map(item => ({ id: item.id, name: item.name })) },
    { id: 'style-root', name: '风格因子库', children: (styleFactors || []).map(item => ({ id: item.id, name: item.name })) }
  ]

  if (!query.dateRange?.length) query.dateRange = defaultDateRange()
  // 默认选中第一个因子并查询
  if (!query.factorId && factorOptions.value[0]) {
    query.factorId = factorOptions.value[0].id
  }
}

// 点击基金下拉框时懒加载（只加载前 500 条）
async function loadFunds(visible) {
  if (!visible || funds.value.length > 0) return
  const list = await getFunds({ limit: 500 }).catch(() => [])
  funds.value = list || []
}

async function handleSearch() {
  if (!query.factorId) return
  loading.value = true
  try {
    // 根据因子类型调用对应接口
    const factor = factorOptions.value.find(f => f.id === query.factorId)
    const factorType = factor?.type || 'base'
    const params = {
      fundCode: query.fundCode,
      factorId: query.factorId,
      startDate: query.dateRange?.[0],
      endDate: query.dateRange?.[1],
      page: page.page,
      size: page.pageSize
    }
    let data
    if (factorType === 'derived') {
      data = await getDerivativeFactorValues(params)
    } else if (factorType === 'style') {
      data = await getStyleFactorValues(params)
    } else {
      data = await getBaseFactorValues(params)
    }
    rows.value = normalizeRows(data)
    page.page = 1
    await nextTick()
    renderChart()
  } finally {
    loading.value = false
  }
}

function chooseFactor(node) {
  if (node?.children?.length) return
  query.factorId = node.id
  handleSearch()
}

function resetFilter() {
  query.fundCode = ''
  query.factorId = factorOptions.value[0]?.id || ''
  query.dateRange = defaultDateRange()
  handleSearch()
}

function exportData() {
  const csv = ['tradeDate,fundCode,fundName,factorName,value', ...rows.value.map(r => [r.tradeDate, r.fundCode, r.fundName, r.factorName, r.value].join(','))].join('\n')
  const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = 'factor-data.csv'
  link.click()
  URL.revokeObjectURL(link.href)
}

function buildSeries(list) {
  const grouped = new Map()
  for (const item of list) {
    const key = item.factorName || item.factorId || 'series'
    if (!grouped.has(key)) grouped.set(key, [])
    grouped.get(key).push(item)
  }
  return [...grouped.entries()].map(([name, items]) => {
    const ordered = items.sort((a, b) => String(a.tradeDate).localeCompare(String(b.tradeDate)))
    return {
      name,
      type: 'line',
      smooth: true,
      showSymbol: false,
      data: ordered.map(i => Number(i.value) || 0)
    }
  })
}

function renderChart() {
  if (!chartRef.value) return
  chartInstance.value?.dispose()
  chartInstance.value = echarts.init(chartRef.value)
  const sorted = [...rows.value].sort((a, b) => String(a.tradeDate).localeCompare(String(b.tradeDate)))
  const dates = [...new Set(sorted.map(i => i.tradeDate))].sort()
  const factorName = sorted[0]?.factorName || '因子数值'
  chartInstance.value.setOption({
    tooltip: {
      trigger: 'axis',
      formatter: function(params) {
        const p = params[0]
        if (!p) return ''
        return `<strong>${p.axisValue}</strong><br/>${p.marker} ${factorName}: ${p.value}`
      }
    },
    grid: { left: 60, right: 30, top: 20, bottom: 70 },
    xAxis: {
      type: 'category',
      data: dates,
      axisLabel: { rotate: 45, fontSize: 11 }
    },
    yAxis: { type: 'value', scale: true },
    series: [{
      type: 'line',
      smooth: true,
      showSymbol: false,
      data: sorted.map(i => Number(i.value) || 0),
      lineStyle: { width: 2 },
      areaStyle: { opacity: 0.08 }
    }]
  })
}

function openCreateDialog(type) {
  dialog.type = type
  dialog.visible = true
  dialog.form = { name: '' }
  dialog.selectedIds = []
  dialog.transferOptions = type === 'derived'
    ? factorOptions.value.filter(i => i.type === 'base').map(i => ({ id: i.id, label: i.name }))
    : factorOptions.value.filter(i => i.type === 'derived').map(i => ({ id: i.id, label: i.name }))
  dialog.weightRows = []
}

watch(() => dialog.selectedIds, () => {
  dialog.weightRows = dialog.selectedIds.map((id) => ({
    id,
    name: dialog.transferOptions.find(i => i.id === id)?.label || id,
    weight: dialog.selectedIds.length ? Number((100 / dialog.selectedIds.length).toFixed(2)) : 0
  }))
}, { deep: true })

async function submitDialog() {
  if (!dialog.form.name || !dialog.selectedIds.length) return
  dialog.saving = true
  try {
    const payload = {
      name: dialog.form.name,
      items: dialog.weightRows.map(row => ({
        [dialog.type === 'derived' ? 'baseFactorId' : 'derivativeFactorId']: row.id,
        weight: row.weight
      }))
    }
    const created = dialog.type === 'derived'
      ? await createDerivativeFactor(payload)
      : await createStyleFactor(payload)
    dialog.visible = false
    await loadMeta()
    if (created?.id) {
      query.factorId = created.id
    }
    await handleSearch()
  } finally {
    dialog.saving = false
  }
}

watch(() => query.factorId, () => {
  page.page = 1
  handleSearch()
})
watch(() => query.dateRange, () => {
  page.page = 1
  handleSearch()
}, { deep: true })

onMounted(async () => {
  await loadMeta()
  await handleSearch()
  window.addEventListener('resize', renderChart)
})

onBeforeUnmount(() => {
  chartInstance.value?.dispose()
  window.removeEventListener('resize', renderChart)
})
</script>
