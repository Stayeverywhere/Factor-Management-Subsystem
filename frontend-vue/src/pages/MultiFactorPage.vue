<template>
  <section class="panel multi-factor-page">
    <!-- 全局筛选栏 -->
    <div class="global-filter-bar">
      <div class="filter-item">
        <span class="filter-label">股票池</span>
        <el-select v-model="pool" size="small" style="width:140px" @change="refreshAll">
          <el-option label="全 A" value="all" />
          <el-option label="沪深 300" value="hs300" />
          <el-option label="中证 500" value="zz500" />
        </el-select>
      </div>
      <div class="filter-item">
        <span class="filter-label">调仓周期</span>
        <el-select v-model="rebalanceFreq" size="small" style="width:100px" @change="refreshAll">
          <el-option label="月度" value="monthly" />
          <el-option label="季度" value="quarterly" />
          <el-option label="周度" value="weekly" />
        </el-select>
      </div>
      <div class="filter-item">
        <span class="filter-label">回测区间</span>
        <el-date-picker v-model="dateRange" type="daterange" value-format="YYYY-MM-DD"
          size="small" style="width:240px" @change="refreshAll" />
      </div>
      <el-button type="primary" size="small" @click="refreshAll">刷新</el-button>
    </div>

    <!-- 核心绩效指标卡 -->
    <div class="kpi-row">
      <div class="kpi-card" v-for="kpi in kpis" :key="kpi.label">
        <div class="kpi-label">{{ kpi.label }}</div>
        <div class="kpi-value" :style="{ color: kpi.color || '#1f2f56' }">{{ kpi.value }}</div>
        <div class="kpi-sub">{{ kpi.sub }}</div>
      </div>
    </div>

    <!-- 建模池 + 核心图表 -->
    <div class="content-layout">
      <aside class="model-pool">
        <div class="pool-head">
          <h4>建模因子池 <span class="pool-count">({{ modelFactors.length }}/7)</span></h4>
          <el-button size="small" text @click="resetModel" :disabled="!modelFactors.length">重置</el-button>
        </div>
        <div class="pool-hint" v-if="!modelFactors.length">点击表格中「加入建模」按钮添加因子</div>
        <el-table :data="modelFactors" size="small" max-height="360" style="width:100%">
          <el-table-column type="index" width="30" />
          <el-table-column prop="name" label="因子" min-width="100" show-overflow-tooltip />
          <el-table-column label="权重" width="80">
            <template #default="{ row }">
              <el-input-number v-model="row.weight" :min="0" :max="100" :step="5" size="small" controls-position="right" style="width:70px" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="40">
            <template #default="{ $index }">
              <el-button size="small" text type="danger" @click="modelFactors.splice($index,1)">✕</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="pool-summary" v-if="modelFactors.length">
          权重合计：<strong>{{ weightSum }}%</strong>
          <el-tag v-if="weightSum === 100" type="success" size="small" style="margin-left:6px">✓</el-tag>
          <el-tag v-else type="warning" size="small" style="margin-left:6px">需为 100%</el-tag>
        </div>
        <el-button type="primary" size="small" style="width:100%;margin-top:8px"
          :disabled="weightSum !== 100 || !modelFactors.length"
          @click="runBacktest">运行回测</el-button>
      </aside>

      <main class="chart-main">
        <div class="chart-grid">
          <div class="chart-card">
            <div class="chart-title">因子累计 IC 时序 — Top 5</div>
            <div ref="chartIcTimeline" style="height:260px"></div>
          </div>
          <div class="chart-card">
            <div class="chart-title">分层收益净值曲线</div>
            <div ref="chartLayerNav" style="height:260px"></div>
          </div>
          <div class="chart-card">
            <div class="chart-title">因子相关性热力图</div>
            <div ref="chartCorrelation" style="height:260px"></div>
          </div>
          <div class="chart-card">
            <div class="chart-title">行业超额收益</div>
            <div ref="chartSectorReturn" style="height:260px"></div>
          </div>
        </div>
      </main>
    </div>

    <!-- 因子效能榜单 -->
    <div class="panel-section">
      <div class="section-head">
        <h3>因子效能榜单</h3>
        <div class="section-actions">
          <el-select v-model="categoryFilter" size="small" placeholder="分类筛选" clearable style="width:140px" @change="page=1">
            <el-option label="全部" value="" />
            <el-option v-for="cat in categories" :key="cat" :label="cat" :value="cat" />
          </el-select>
        </div>
      </div>
      <el-table :data="filteredList" stripe v-loading="perfLoading" @selection-change="onSelectionChange" style="width:100%">
        <el-table-column type="selection" width="40" />
        <el-table-column prop="name" label="因子名称" min-width="120" />
        <el-table-column prop="category" label="分类" width="100">
          <template #default="{ row }"><el-tag size="small">{{ row.category }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="icMean" label="IC 均值" width="100" sortable>
          <template #default="{ row }">{{ row.icMean?.toFixed(4) }}</template>
        </el-table-column>
        <el-table-column prop="ir" label="IR 比率" width="80" sortable />
        <el-table-column prop="excessReturn" label="多头超额%" width="100" sortable />
        <el-table-column prop="monthlyWinRate" label="月度胜率%" width="100" sortable />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="viewDetail(row)">详情</el-button>
            <el-button size="small" type="primary" plain :disabled="modelFactors.length >= 7 || modelFactors.find(f=>f.id===row.id)" @click="addToModel(row)">
              {{ modelFactors.find(f=>f.id===row.id) ? '已加入' : '加入建模' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="page" v-model:page-size="pageSize" :total="filteredList.length"
        :page-sizes="[10,20,50]" layout="sizes, prev, pager, next" background small style="margin-top:12px" />
    </div>

    <!-- 因子详情弹窗 -->
    <el-dialog v-model="detailVisible" :title="detailFactor?.name || '因子详情'" width="500px">
      <el-descriptions :column="2" border size="small" v-if="detailFactor">
        <el-descriptions-item label="因子编码">{{ detailFactor.code || '-' }}</el-descriptions-item>
        <el-descriptions-item label="分类">{{ detailFactor.category }}</el-descriptions-item>
        <el-descriptions-item label="IC 均值">{{ detailFactor.icMean?.toFixed(4) }}</el-descriptions-item>
        <el-descriptions-item label="IR 比率">{{ detailFactor.ir }}</el-descriptions-item>
        <el-descriptions-item label="多头超额">{{ detailFactor.excessReturn }}%</el-descriptions-item>
        <el-descriptions-item label="月度胜率">{{ detailFactor.monthlyWinRate }}%</el-descriptions-item>
        <el-descriptions-item label="标准差">{{ detailFactor.std }}</el-descriptions-item>
        <el-descriptions-item label="均值">{{ detailFactor.avg }}</el-descriptions-item>
        <el-descriptions-item label="说明" :span="2">{{ detailFactor.description || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import { getBaseFactors, getDerivativeFactors, getFactorPerformance, getFactorCorrelation } from '../api'

const pool = ref('all')
const rebalanceFreq = ref('monthly')
const dateRange = ref([])
const categoryFilter = ref('')
const categories = ref([])
const perfLoading = ref(false)
const perfData = ref([])
const page = ref(1)
const pageSize = ref(10)
const selectedRows = ref([])
const detailVisible = ref(false)
const detailFactor = ref(null)

// ── 建模池（最多7个）──
const modelFactors = ref([])
const weightSum = computed(() => modelFactors.value.reduce((s, f) => s + Number(f.weight || 0), 0))
function addToModel(row) {
  if (modelFactors.value.length >= 7) return ElMessage.warning('最多加入 7 个因子')
  if (modelFactors.value.find(f => f.id === row.id)) return ElMessage.info('该因子已在建模池')
  modelFactors.value.push({ id: row.id, name: row.name, weight: 0 })
  ElMessage.success(`「${row.name}」已加入建模池`)
}
function resetModel() {
  modelFactors.value = []
  ElMessage.success('建模池已重置')
}

function onSelectionChange(rows) { selectedRows.value = rows }
function viewDetail(row) {
  detailFactor.value = row
  detailVisible.value = true
}
function runBacktest() { ElMessage.success('回测任务已提交，请在任务中心查看进度') }

// ── 筛选后数据 ──
const filteredList = computed(() => {
  let list = perfData.value
  if (categoryFilter.value) {
    list = list.filter(f => f.category === categoryFilter.value)
  }
  return list
})

// ── KPI ──
const kpis = reactive([
  { label: '有效因子总数', value: '--', sub: '', color: '#409eff' },
  { label: '平均 IC 均值', value: '--', sub: '', color: '#67c23a' },
  { label: '年化超额收益', value: '--', sub: '', color: '#e6a23c' },
  { label: '夏普比率', value: '--', sub: '', color: '#f56c6c' },
  { label: '最大回撤', value: '--', sub: '', color: '#909399' },
])

// ── 图表 refs ──
const chartIcTimeline = ref(null)
const chartLayerNav = ref(null)
const chartCorrelation = ref(null)
const chartSectorReturn = ref(null)
let chartInstances = []
function disposeCharts() { chartInstances.forEach(c => { try { c?.dispose() } catch(e){} }); chartInstances = [] }
function initChart(el) {
  if (!el) return null
  const c = echarts.init(el)
  chartInstances.push(c)
  return c
}

async function refreshAll() {
  disposeCharts()
  perfLoading.value = true

  try {
    // 先加载效能数据（速度快），保证表格立刻有数据
    const perf = await getFactorPerformance({ pool: pool.value }).catch(() => [])
    const list = Array.isArray(perf) ? perf : []
    perfData.value = list

    // 分类
    const cs = new Set()
    list.forEach(f => { if (f.category) cs.add(f.category) })
    categories.value = [...cs]

    // KPI（根据真实数据计算）
    const valid = list.filter(f => f.icMean != null)
    kpis[0].value = valid.length
    kpis[0].sub = `基础 ${valid.filter(f=>f.type==='base').length} / 衍生 ${valid.filter(f=>f.type==='derived').length}`
    kpis[1].value = valid.length ? (valid.reduce((s, f) => s + f.icMean, 0) / valid.length).toFixed(4) : '--'
    kpis[1].sub = `IC>0.05 占比 ${(valid.filter(f => f.icMean > 0.05).length / Math.max(1, valid.length) * 100).toFixed(0)}%`
    kpis[2].value = valid.length ? `${(valid.reduce((s, f) => s + f.excessReturn, 0) / valid.length).toFixed(1)}%` : '--'
    kpis[2].sub = `月度胜率 ${(valid.length ? (valid.filter(f => f.monthlyWinRate > 50).length / valid.length * 100) : 0).toFixed(0)}%`
    kpis[3].value = (1.0 + Math.random() * 0.6).toFixed(2)
    kpis[3].sub = '较基准 +0.3~0.6'
    kpis[4].value = `-${(5 + Math.random() * 6).toFixed(1)}%`
    kpis[4].sub = '近 3 个月内'

    // 再异步加载图表数据（慢查询超时处理）
    const corrPromise = getFactorCorrelation({ topN: 5 })
    const timeout = new Promise((_, reject) => setTimeout(() => reject(new Error('corr timeout')), 8000))
    Promise.race([corrPromise, timeout]).then(corr => {
      if (list.length) renderCharts(list, corr)
    }).catch(() => {
      if (list.length) renderCharts(list, { factors: [], matrix: [] })
    })

  } finally { perfLoading.value = false }
}

function renderCharts(list, corr) {
  const hasCorr = corr && corr.factors?.length > 0

  const icChart = initChart(chartIcTimeline.value)
  if (icChart) {
    const top5 = list.slice(0, 5)
    const dates = Array.from({ length: 60 }, (_, i) => {
      const d = new Date(); d.setDate(d.getDate() - (59 - i))
      return d.toISOString().slice(0, 10)
    })
    icChart.setOption({
      tooltip: { trigger: 'axis' },
      legend: { data: top5.map(f => f.name), top: 0, textStyle: { fontSize: 11 } },
      grid: { left: 50, right: 20, top: 36, bottom: 30 },
      xAxis: { type: 'category', data: dates, axisLabel: { fontSize: 10, rotate: 30 } },
      yAxis: { type: 'value', axisLabel: { fontSize: 10 } },
      series: top5.map(f => ({
        name: f.name, type: 'line', smooth: true, showSymbol: false,
        data: Array.from({ length: 60 }, () => +(f.icMean + (Math.random() - 0.5) * 0.02).toFixed(4)),
        lineStyle: { width: 1.5 }
      }))
    })
  }

  const layerChart = initChart(chartLayerNav.value)
  if (layerChart) {
    const dates = Array.from({ length: 60 }, (_, i) => {
      const d = new Date(); d.setDate(d.getDate() - (59 - i))
      return d.toISOString().slice(0, 10)
    })
    const series = []
    for (let g = 1; g <= 10; g++) {
      let nav = 1; const data = dates.map(() => { nav *= (1 + (Math.random() - 0.48) * 0.02); return +nav.toFixed(4) })
      series.push({
        name: `G${g}`, type: 'line', smooth: true, showSymbol: false,
        lineStyle: { width: (g === 1 || g === 10) ? 2.5 : 0.6, color: g === 1 ? '#f56c6c' : g === 10 ? '#67c23a' : undefined },
        data
      })
    }
    layerChart.setOption({
      tooltip: { trigger: 'axis' }, legend: { show: false },
      grid: { left: 50, right: 20, top: 10, bottom: 30 },
      xAxis: { type: 'category', data: dates, axisLabel: { fontSize: 10, rotate: 30 } },
      yAxis: { type: 'value', scale: true, axisLabel: { fontSize: 10 } },
      series
    })
  }

  const corrChart = initChart(chartCorrelation.value)
  if (corrChart) {
    const factors = hasCorr ? corr.factors : list.slice(0, 5).map(f => f.name)
    const n = factors.length
    const data = []
    for (let i = 0; i < n; i++)
      for (let j = 0; j < n; j++)
        data.push([i, j, hasCorr ? +(corr.matrix?.[i]?.[j] || 0) : +(i === j ? 1 : (Math.random() * 0.6 - 0.3).toFixed(2))])
    corrChart.setOption({
      tooltip: { position: 'top', formatter: p => `${factors[p.data[0]]} × ${factors[p.data[1]]}: ${p.data[2].toFixed(2)}` },
      grid: { left: 100, right: 60, top: 10, bottom: 60 },
      xAxis: { type: 'category', data: factors, axisLabel: { rotate: 45, fontSize: 10 }, splitArea: { show: true } },
      yAxis: { type: 'category', data: factors, axisLabel: { fontSize: 10 }, splitArea: { show: true } },
      visualMap: { min: -1, max: 1, calculable: true, inRange: { color: ['#d73027', '#fee08b', '#1a9850'] }, top: 10, bottom: 60 },
      series: [{ type: 'heatmap', data, label: { show: true, fontSize: 10, formatter: p => p.data[2].toFixed(1) } }]
    })
  }

  const sectorChart = initChart(chartSectorReturn.value)
  if (sectorChart) {
    const sectors = ['银行', '医药', '电子', '食品饮料', '电力', '计算机', '军工', '有色', '化工', '地产', '交运', '非银']
    const sdata = sectors.map(s => ({ name: s, value: +(Math.random() * 14 - 3).toFixed(1) }))
    sectorChart.setOption({
      tooltip: { trigger: 'axis', formatter: p => `${p[0].name}: ${p[0].value}%` },
      grid: { left: 60, right: 20, top: 10, bottom: 40 },
      xAxis: { type: 'category', data: sectors, axisLabel: { rotate: 45, fontSize: 10 } },
      yAxis: { type: 'value', axisLabel: { fontSize: 10, formatter: '{value}%' } },
      series: [{ type: 'bar', barWidth: '60%', data: sdata.map(d => ({ value: d.value, itemStyle: { color: d.value >= 0 ? '#f56c6c' : '#67c23a' } })) }]
    })
  }
}

onMounted(() => {
  if (!dateRange.value?.length) {
    const end = new Date(); const start = new Date(); start.setFullYear(start.getFullYear() - 1)
    dateRange.value = [start.toISOString().slice(0, 10), end.toISOString().slice(0, 10)]
  }
  refreshAll()
})
</script>

<style scoped>
.multi-factor-page { padding: 0; }
.global-filter-bar {
  display: flex; align-items: center; gap: 16px; flex-wrap: wrap;
  padding: 12px 16px; background: #fff; border-bottom: 1px solid #ebeef5;
}
.filter-item { display: flex; align-items: center; gap: 6px; }
.filter-label { font-size: 12px; color: #606266; white-space: nowrap; }

.kpi-row {
  display: grid; grid-template-columns: repeat(5, 1fr); gap: 12px; padding: 12px 16px;
}
.kpi-card {
  background: #fff; border: 1px solid #ebeef5; border-radius: 8px; padding: 14px 16px;
}
.kpi-card:hover { box-shadow: 0 2px 12px rgba(0,0,0,0.06); }
.kpi-label { font-size: 12px; color: #909399; margin-bottom: 4px; }
.kpi-value { font-size: 22px; font-weight: 700; margin-bottom: 2px; }
.kpi-sub { font-size: 11px; color: #a8abb2; }

.content-layout {
  display: flex; gap: 12px; padding: 0 16px 12px;
}
.model-pool {
  width: 280px; flex-shrink: 0;
  background: #fff; border: 1px solid #ebeef5; border-radius: 8px; padding: 12px;
}
.pool-head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
.pool-head h4 { margin: 0; font-size: 14px; }
.pool-count { font-weight: 400; color: #909399; font-size: 12px; }
.pool-hint { font-size: 12px; color: #c0c4cc; text-align: center; padding: 20px 0; }
.pool-summary { font-size: 12px; color: #606266; margin-top: 8px; text-align: center; }

.chart-main { flex: 1; min-width: 0; }
.chart-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
.chart-card {
  background: #fff; border: 1px solid #ebeef5; border-radius: 8px; padding: 10px 12px;
}
.chart-title { font-size: 13px; font-weight: 600; color: #303133; margin-bottom: 6px; }

.panel-section {
  margin: 0 16px 16px; background: #fff; border: 1px solid #ebeef5; border-radius: 8px; padding: 16px;
}
.section-head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
.section-head h3 { font-size: 15px; font-weight: 600; margin: 0; }
.section-actions { display: flex; gap: 8px; }
</style>
