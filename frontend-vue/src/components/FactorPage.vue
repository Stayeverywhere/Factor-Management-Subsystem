<template>
  <section class="panel">
    <div class="panel-head">
      <h2>因子管理中心</h2>
      <span class="muted">左侧树形菜单 + 顶部筛选 + 折线图 + 表格</span>
    </div>
    <div class="factor-layout">
      <div class="factor-tree">
        <div v-for="node in categories" :key="node.id" class="tree-node">
          <div class="tree-node-title" :class="{ selected: selectedId === node.id }" @click="selectNode(node.id)">{{ node.name }}</div>
          <div v-if="node.children?.length" class="tree-children">
            <div v-for="child in node.children" :key="child.id" class="tree-child" :class="{ selected: selectedId === child.id }" @click="selectNode(child.id)">{{ child.name }}</div>
          </div>
        </div>
      </div>
      <div class="factor-main">
        <div class="filter-bar">
          <el-select v-model="localFilter.fundCode" placeholder="选择基金" style="width: 220px" @change="emitSearch">
            <el-option v-for="fund in funds" :key="fund.fundCode" :label="fund.fundName" :value="fund.fundCode" />
          </el-select>
          <el-select v-model="localFilter.factorId" placeholder="选择因子" style="width: 220px" @change="emitSearch">
            <el-option v-for="factor in baseFactors" :key="factor.id" :label="factor.name" :value="factor.id" />
          </el-select>
          <el-date-picker v-model="localFilter.dateRange" type="daterange" value-format="YYYY-MM-DD" start-placeholder="开始日期" end-placeholder="结束日期" @change="emitSearch" />
          <el-button type="primary" @click="$emit('create-derived')">创建衍生因子</el-button>
          <el-button @click="$emit('create-style')">创建风格投资因子</el-button>
        </div>
        <div ref="chartRef" class="chart-placeholder factor-chart"></div>
        <el-table :data="valueTable" stripe>
          <el-table-column prop="dataDate" label="日期" width="130" />
          <el-table-column prop="fundCode" label="基金代码" width="120" />
          <el-table-column prop="factorName" label="因子名称" />
          <el-table-column prop="value" label="因子数值" width="140" />
          <el-table-column prop="updatedAt" label="更新时间" width="180" />
        </el-table>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, reactive, ref, watch } from 'vue'
import * as echarts from 'echarts'

const props = defineProps({ categories: Array, funds: Array, baseFactors: Array, valueTable: Array, filter: Object })
const emit = defineEmits(['update:filter', 'search', 'create-derived', 'create-style'])
const localFilter = reactive({ fundCode: '', factorId: '', dateRange: [] })
const selectedId = ref('')
const chartRef = ref(null)
let chartInstance = null

watch(() => props.filter, (v) => Object.assign(localFilter, v || {}), { immediate: true, deep: true })
watch(localFilter, () => emit('update:filter', { ...localFilter }), { deep: true })

function selectNode(id) { selectedId.value = id; emit('search') }
function emitSearch() { emit('search') }
function renderChart() { if (!chartRef.value) return; chartInstance?.dispose(); chartInstance = echarts.init(chartRef.value); chartInstance.setOption({ tooltip: { trigger: 'axis' }, grid: { left: 24, right: 24, top: 30, bottom: 24 }, xAxis: { type: 'category', data: props.valueTable.map(i => i.dataDate) }, yAxis: { type: 'value' }, series: [{ type: 'line', smooth: true, data: props.valueTable.map(i => Number(i.value)) }] }) }
onMounted(renderChart)
watch(() => props.valueTable, () => setTimeout(renderChart, 0), { deep: true })
onBeforeUnmount(() => chartInstance?.dispose())
</script>
