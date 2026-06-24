<template>
  <section class="panel factor-overview-page">
    <div class="factor-actions">
      <div class="factor-actions-left">
        <el-button type="primary" @click="$emit('create-derived')">+ 创建衍生因子</el-button>
        <el-button @click="$emit('create-style')">+ 创建风格因子</el-button>
      </div>
      <el-button plain @click="$emit('export')">数据导出</el-button>
    </div>

    <div class="factor-filter-grid">
      <el-select v-model="localFilter.factorId" filterable clearable placeholder="因子选择" style="width: 100%">
        <el-option v-for="factor in factorOptions" :key="factor.id" :label="factor.name" :value="factor.id" />
      </el-select>
      <el-input v-model="localFilter.targetKeyword" clearable placeholder="标的选择：基金代码 / 名称" />
      <el-date-picker v-model="localFilter.dateRange" type="daterange" value-format="YYYY-MM-DD" start-placeholder="开始日期" end-placeholder="结束日期" />
      <div class="factor-filter-actions">
        <el-button type="primary" @click="$emit('search')">查询</el-button>
        <el-button @click="resetFilter">重置</el-button>
      </div>
    </div>

    <div class="factor-content-grid">
      <aside class="factor-tree">
        <el-input v-model="treeKeyword" placeholder="搜索因子名称" clearable />
        <div class="tree-section">
          <div v-for="node in filteredCategories" :key="node.id" class="tree-node">
            <div class="tree-node-title">{{ node.name }}</div>
            <div v-if="node.children?.length" class="tree-children">
              <div v-for="child in node.children" :key="child.id" class="tree-child">{{ child.name }}</div>
            </div>
          </div>
        </div>
      </aside>

      <div class="factor-main">
        <div class="chart-placeholder factor-chart"></div>
        <el-table :data="valueTable" stripe>
          <el-table-column prop="dataDate" label="发布日期" width="130" />
          <el-table-column prop="value" label="对应因子数值" width="180" />
        </el-table>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed, reactive, ref, watch } from 'vue'

const props = defineProps({ categories: Array, funds: Array, baseFactors: Array, derivedFactors: Array, styleFactors: Array, valueTable: Array, filter: Object })
const emit = defineEmits(['update:filter', 'search', 'create-derived', 'create-style', 'export'])
const localFilter = reactive({ factorId: '', targetKeyword: '', dateRange: [] })
const treeKeyword = ref('')

watch(() => props.filter, v => Object.assign(localFilter, v || {}), { immediate: true, deep: true })
watch(localFilter, () => emit('update:filter', { ...localFilter }), { deep: true })
const factorOptions = computed(() => [...(props.baseFactors || []), ...(props.derivedFactors || []), ...(props.styleFactors || [])])
const filteredCategories = computed(() => (props.categories || []).filter(n => !treeKeyword.value || JSON.stringify(n).includes(treeKeyword.value)))
function resetFilter() { localFilter.factorId = ''; localFilter.targetKeyword = ''; localFilter.dateRange = []; emit('update:filter', { ...localFilter }) }
</script>
