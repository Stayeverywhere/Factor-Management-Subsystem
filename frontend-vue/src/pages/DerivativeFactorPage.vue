<template>
  <section class="panel factor-manage-page">
    <div class="panel-head">
      <h2>衍生因子管理</h2>
      <el-button type="primary" @click="openCreate">+ 创建衍生因子</el-button>
    </div>

    <el-table :data="factors" stripe v-loading="loading" style="width:100%">
      <el-table-column type="index" label="#" width="50" />
      <el-table-column prop="name" label="名称" min-width="140" />
      <el-table-column prop="formula" label="公式" min-width="260" show-overflow-tooltip>
        <template #default="{ row }">
          <code style="font-size:12px;background:#f5f7fa;padding:2px 6px;border-radius:3px;">{{ row.formula || '-' }}</code>
        </template>
      </el-table-column>
      <el-table-column prop="description" label="说明" min-width="160" show-overflow-tooltip />
      <el-table-column prop="createdAt" label="创建时间" width="160">
        <template #default="{ row }">{{ row.createdAt?.substring(0, 16) }}</template>
      </el-table-column>
      <el-table-column label="状态" width="70">
        <template #default="{ row }">
          <el-tag :type="row.enabled ? 'success' : 'info'" size="small">{{ row.enabled ? '启用' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" plain @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 创建/编辑 弹窗 -->
    <el-dialog v-model="dialog.visible" :title="dialog.isEdit ? '编辑衍生因子' : '创建衍生因子'" width="800px" destroy-on-close>
      <el-form :model="dialog.form" label-width="100px">
        <el-form-item label="因子名称">
          <el-input v-model="dialog.form.name" placeholder="如：5日动量因子" />
        </el-form-item>
        <el-form-item label="因子编码">
          <el-input v-model="dialog.form.code" placeholder="英文编码如 mom_5" :disabled="dialog.isEdit" />
        </el-form-item>

        <!-- 公式编辑器 -->
        <el-form-item label="计算公式">
          <div class="formula-editor">
            <div class="formula-toolbar">
              <span class="toolbar-label">数据字段</span>
              <el-tag v-for="f in availableFields" :key="f" size="small" class="formula-chip" @click="insertText(f)">{{ f }}</el-tag>
            </div>
            <div class="formula-toolbar">
              <span class="toolbar-label">函数</span>
              <el-tag v-for="fn in functions" :key="fn" size="small" class="formula-chip" @click="insertText(fn)">{{ fn }}</el-tag>
            </div>
            <div class="formula-toolbar">
              <span class="toolbar-label">运算符</span>
              <el-tag v-for="op in operators" :key="op" size="small" class="formula-chip" @click="insertText(op)">{{ op }}</el-tag>
              <el-tag size="small" class="formula-chip" @click="insertText(',')">,</el-tag>
            </div>
            <el-input
              v-model="dialog.form.formula"
              type="textarea"
              :rows="3"
              placeholder="点击上方标签构建公式，如: (close_price / shift(close_price, 1) - 1) * 100"
              class="formula-input"
            />
            <div class="formula-hint">
              数据库字段: close_price(收盘价) open_price(开盘价) high_price(最高价) low_price(最低价) volume(成交量) turnover(成交额) value(因子值)<br/>
              函数: shift(field,n) 平移 mean(field,n) 均值 max(field,n) 最大值 min(field,n) 最小值 std(field,n) 标准差 abs(x) 绝对值
            </div>
          </div>
        </el-form-item>

        <el-form-item label="权重组合" v-if="dialog.weightRows.length">
          <el-table :data="dialog.weightRows" border size="small" max-height="200">
            <el-table-column prop="name" label="因子名称" />
            <el-table-column label="权重(%)" width="150">
              <template #default="{ row }">
                <el-input-number v-model="row.weight" :min="0" :max="100" :step="5" size="small" />
              </template>
            </el-table-column>
          </el-table>
          <div style="margin-top:6px;font-size:12px;color:#909399;">
            权重和: {{ weightSum }}%{{ weightSum !== 100 ? '（需=100%）' : ' ✓' }}
          </div>
        </el-form-item>

        <el-form-item label="关联因子（可选）">
          <el-select v-model="dialog.selectedBaseIds" multiple filterable placeholder="选择基础因子（权重组合用）" style="width:100%">
            <el-option v-for="f in allBaseFactors" :key="f.id" :label="f.label" :value="f.id" />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialog.visible = false">取消</el-button>
        <el-button type="primary" :loading="dialog.saving" @click="submitDialog" :disabled="!dialog.form.name">
          {{ dialog.isEdit ? '保存' : '创建' }}
        </el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getDerivativeFactors, getBaseFactors,
  createDerivativeFactor, updateDerivativeFactor, deleteDerivativeFactor
} from '../api'

const loading = ref(false)
const factors = ref([])
const allBaseFactors = ref([])
const formulaInput = ref(null)

// 公式编辑器数据 — 字段名与数据库表 ak_market_quote / ak_index_quote / base_factor_value 一致
const availableFields = ['close_price', 'open_price', 'high_price', 'low_price', 'volume', 'turnover', 'value']
const functions = ['shift(field,n)', 'mean(field,n)', 'max(field,n)', 'min(field,n)', 'std(field,n)', 'abs(x)']
const operators = ['+', '-', '*', '/', '(', ')']

const dialog = reactive({
  visible: false, isEdit: false, saving: false,
  form: { name: '', code: '', description: '', formula: '' },
  selectedBaseIds: [],
  weightRows: []
})

const weightSum = computed(() => dialog.weightRows.reduce((s, r) => s + Number(r.weight || 0), 0))

// 插入文本到公式
function insertText(text) {
  dialog.form.formula = (dialog.form.formula || '') + text
}

async function load() {
  loading.value = true
  try {
    const [f, bf] = await Promise.all([
      getDerivativeFactors().catch(() => []),
      getBaseFactors().catch(() => ({ items: [] }))
    ])
    factors.value = Array.isArray(f) ? f : []
    const items = bf?.items || bf || []
    allBaseFactors.value = items.map(item => ({ id: item.id, label: `${item.name} (${item.code})` }))
  } finally {
    loading.value = false
  }
}

function openCreate() {
  dialog.isEdit = false
  dialog.visible = true
  dialog.form = { name: '', code: '', description: '', formula: '' }
  dialog.selectedBaseIds = []
  dialog.weightRows = []
}

function openEdit(row) {
  dialog.isEdit = true
  dialog.visible = true
  dialog.form = {
    name: row.name || '',
    code: row.code || '',
    description: row.description || '',
    formula: row.formula || ''
  }
  dialog.selectedBaseIds = []
  dialog.weightRows = []
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定删除「${row.name}」？`, '确认', { type: 'warning' })
    ElMessage.success('已删除')
    await load()
  } catch (e) { /* 取消 */ }
}

// 选中关联因子时自动分配权重
watch(() => dialog.selectedBaseIds, (ids) => {
  dialog.weightRows = ids.map(id => {
    const info = allBaseFactors.value.find(i => i.id === id)
    return { id, name: info?.label || id, weight: ids.length ? Number((100 / ids.length).toFixed(2)) : 0 }
  })
}, { deep: true })

async function submitDialog() {
  if (!dialog.form.name) return
  dialog.saving = true
  try {
    const payload = {
      name: dialog.form.name,
      code: dialog.form.code || dialog.form.name.replace(/\s+/g, '_').toLowerCase(),
      description: dialog.form.description || (dialog.form.formula ? `公式: ${dialog.form.formula}` : ''),
      formula: dialog.form.formula || null,
      items: dialog.weightRows.map(r => ({ baseFactorId: r.id, weight: r.weight }))
    }
    if (dialog.isEdit) {
      // 找匹配的因子 ID 更新
      const target = factors.value.find(f => f.code === dialog.form.code || f.name === dialog.form.name)
      if (target) await updateDerivativeFactor(target.id, payload)
    } else {
      await createDerivativeFactor(payload)
    }
    dialog.visible = false
    ElMessage.success(dialog.isEdit ? '已更新' : '已创建')
    await load()
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '操作失败')
  } finally {
    dialog.saving = false
  }
}

onMounted(load)
</script>

<style scoped>
.formula-editor { width: 100%; }
.formula-toolbar {
  display: flex; align-items: center; gap: 6px; flex-wrap: wrap;
  margin-bottom: 8px; padding: 6px 8px; background: #fafafa;
  border-radius: 4px; border: 1px solid #ebeef5;
}
.toolbar-label {
  font-size: 12px; color: #909399; min-width: 56px; flex-shrink: 0;
}
.formula-chip {
  cursor: pointer; user-select: none; transition: all 0.15s;
}
.formula-chip:hover { transform: scale(1.05); }
.formula-input { margin-top: 4px; }
.formula-hint {
  margin-top: 6px; font-size: 11px; color: #a8abb2; line-height: 1.6;
}
</style>
