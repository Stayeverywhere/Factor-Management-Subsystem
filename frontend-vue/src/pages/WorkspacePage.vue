<template>
  <div v-if="session" class="app-shell">
    <aside class="sidebar">
      <div class="brand"><div class="brand-mark">F</div><div><div class="brand-title">因子管理平台</div><div class="brand-subtitle">Factor & Advisory</div></div></div>
      <div class="session-box"><div class="session-name">{{ session.account.displayName }}</div><div class="session-role">{{ session.role.name }} · {{ session.account.userType }}</div></div>
      <el-menu :default-active="activeMenu" class="menu" @select="handleMenuSelect">
        <el-menu-item v-for="menu in session.menus" :key="menu.id" :index="menu.name">{{ menu.name }}</el-menu-item>
      </el-menu>
      <div class="sidebar-footer"><div class="pill success">在线运行</div><div class="footer-note">专业 · 克制 · 金融感 · 数据化</div></div>
    </aside>
    <main class="main">
      <header class="topbar">
        <div><div class="page-kicker">{{ dashboard.kicker }}</div><h1>{{ dashboard.title }}</h1></div>
        <div class="topbar-actions"><el-input v-model="searchText" placeholder="全局搜索 / 快捷命令" class="search-box" /><el-button type="primary" @click="logout">退出登录</el-button></div>
      </header>
      <section class="hero-grid">
        <div v-for="item in dashboard.stats" :key="item.label" class="stat-card"><div class="stat-label">{{ item.label }}</div><div class="stat-value">{{ item.value }}</div><div class="stat-delta">{{ item.delta }}</div></div>
      </section>
      <section class="content-grid">
        <div class="panel panel-large"><div class="panel-head"><h2>{{ dashboard.title }}</h2><span class="muted">最近 7 天</span></div><div ref="chartRef" class="chart-placeholder"></div></div>
        <div class="panel"><div class="panel-head"><h2>待处理事项</h2></div><div class="task-list"><div v-for="item in dashboard.tasks" :key="item" class="task"><div class="task-title">{{ item }}</div><div class="task-meta">建议尽快处理</div></div></div></div>
        <div class="panel"><div class="panel-head"><h2>快捷入口</h2></div><div class="shortcut-list"><div v-for="item in dashboard.shortcuts" :key="item" class="shortcut">{{ item }}</div></div></div>
      </section>
      <FactorPage v-if="factorMode" :categories="factorCategories" :funds="funds" :base-factors="baseFactors.items || []" :value-table="currentValueTable" :filter="factorFilter" @update:filter="factorFilter = $event" @search="loadFactorData" @create-derived="showDerivedDialog = true" @create-style="showStyleDialog = true" />
      <section class="table-panel panel" v-else>
        <div class="panel-head"><h2>{{ dashboard.tableTitle }}</h2><span class="muted">{{ dashboard.tableHint }}</span></div>
        <div class="table-wrap"><table><thead><tr><th>对象</th><th>动作</th><th>金额/状态</th><th>结果</th><th>时间</th></tr></thead><tbody><tr v-for="(row, idx) in dashboard.table" :key="idx"><td v-for="(cell, cIdx) in row" :key="cIdx"><span v-if="cIdx === 3" class="badge" :class="badgeClass(cell)">{{ cell }}</span><template v-else>{{ cell }}</template></td></tr></tbody></table></div>
      </section>
    </main>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import FactorPage from '../components/FactorPage.vue'
import { getDashboard, getFactorCategories, getFunds, getBaseFactors, getBaseFactorValues, getDerivativeFactors, getStyleFactors } from '../api'

const router = useRouter()
const session = ref(JSON.parse(localStorage.getItem('session') || 'null'))
const dashboard = ref({ title: '', kicker: '', stats: [], tasks: [], shortcuts: [], tableTitle: '', tableHint: '', table: [] })
const activeMenu = ref('')
const searchText = ref('')
const chartRef = ref(null)
const chartInstance = ref(null)
const factorCategories = ref([])
const funds = ref([])
const baseFactors = ref({ items: [] })
const currentValueTable = ref([])
const factorFilter = ref({ fundCode: '', factorId: '', dateRange: [] })
const factorMode = computed(() => activeMenu.value === '因子管理' || activeMenu.value === '首页' || !activeMenu.value)
if (!session.value) router.replace('/login')
activeMenu.value = session.value?.menus?.[0]?.name || ''

function badgeClass(text) { if (['成功', '正常', '已发布', '持有中'].some(t => text.includes(t))) return 'green'; if (['待处理', '待确认', '待签署', '进行中'].some(t => text.includes(t))) return 'orange'; return 'blue' }
async function loadDashboard() { dashboard.value = await getDashboard(session.value.account.userType) }
async function loadFactorData() { factorCategories.value = await getFactorCategories(); funds.value = await getFunds(); const bf = await getBaseFactors({ page: 1, size: 50 }); baseFactors.value = bf; const first = bf.items?.[0]; if (!factorFilter.value.fundCode && funds.value[0]) factorFilter.value.fundCode = funds.value[0].fundCode; if (!factorFilter.value.factorId && first) factorFilter.value.factorId = first.id; const values = await getBaseFactorValues({ ...factorFilter.value, startDate: '', endDate: '', page: 1, size: 10 }); currentValueTable.value = values.map(v => ({ dataDate: v.dataDate, fundCode: v.fundCode, factorName: first?.name || '', value: v.value, updatedAt: v.updatedAt })) }
function renderChart() { if (!chartRef.value) return; chartInstance.value?.dispose(); chartInstance.value = echarts.init(chartRef.value); chartInstance.value.setOption({ grid: { left: 24, right: 24, top: 30, bottom: 24 }, xAxis: { type: 'category', data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'] }, yAxis: { type: 'value' }, series: [{ type: 'line', smooth: true, data: [120, 132, 101, 134, 90, 230, 210], areaStyle: {} }] }) }
function handleMenuSelect(name) { activeMenu.value = name; if (name === '因子管理') loadFactorData() }
function logout() { localStorage.removeItem('session'); router.replace('/login') }
watch(() => session.value, () => setTimeout(renderChart, 0))
onMounted(async () => { await loadDashboard(); await loadFactorData(); renderChart() })
</script>
