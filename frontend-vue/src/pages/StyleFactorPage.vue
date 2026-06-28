<template>
  <section class="panel factor-manage-page">
    <div class="panel-head">
      <h2>风格因子管理</h2>
      <el-button type="primary" @click="openCreate">+ 创建风格因子</el-button>
    </div>

    <el-table :data="factors" stripe v-loading="loading" style="width:100%">
      <el-table-column type="index" label="#" width="50" />
      <el-table-column prop="name" label="名称" min-width="160" />
      <el-table-column prop="description" label="说明" min-width="240" show-overflow-tooltip />
      <el-table-column prop="createdAt" label="创建时间" width="180">
        <template #default="{ row }">{{ row.createdAt?.substring(0, 16) }}</template>
      </el-table-column>
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.enabled ? 'success' : 'info'" size="small">{{ row.enabled ? '启用' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" :type="row.enabled ? 'warning' : 'default'" @click="togglePin(row)">置顶</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialog.visible" :title="dialog.isEdit ? '编辑风格因子' : '创建风格因子'" width="600px" destroy-on-close>
      <el-form :model="dialog.form" label-width="100px">
        <el-form-item label="因子名称">
          <el-input v-model="dialog.form.name" placeholder="请输入风格因子名称" />
        </el-form-item>
        <el-form-item label="因子说明">
          <el-input v-model="dialog.form.description" type="textarea" :rows="2" placeholder="风格因子的描述和投资逻辑" />
        </el-form-item>
        <el-form-item label="衍生因子">
          <el-transfer
            v-model="dialog.selectedDerivedIds"
            filterable
            :data="allDerived"
            :titles="['可选衍生因子', '已选']"
            :props="{ key: 'id', label: 'label' }"
          />
        </el-form-item>
        <el-form-item label="权重设置" v-if="dialog.weightRows.length">
          <el-table :data="dialog.weightRows" border size="small">
            <el-table-column prop="name" label="因子名称" />
            <el-table-column label="权重(%)" width="160">
              <template #default="{ row }">
                <el-input-number v-model="row.weight" :min="0" :max="100" :step="5" size="small" />
              </template>
            </el-table-column>
          </el-table>
          <div style="margin-top:8px;color:#909399;font-size:13px;">
            权重总和：{{ weightSum }}%{{ weightSum !== 100 ? '（需等于 100%）' : '' }}
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.visible = false">取消</el-button>
        <el-button type="primary" :loading="dialog.saving" @click="submitDialog" :disabled="weightSum !== 100">
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
  getStyleFactors, getDerivativeFactors,
  createStyleFactor, updateStyleFactor, deleteStyleFactor
} from '../api'

const loading = ref(false)
const factors = ref([])
const allDerived = ref([])
const dialog = reactive({
  visible: false, isEdit: false, saving: false,
  form: { name: '', description: '' },
  selectedDerivedIds: [],
  weightRows: []
})

const weightSum = computed(() => dialog.weightRows.reduce((s, r) => s + Number(r.weight || 0), 0))

async function load() {
  loading.value = true
  try {
    const [f, d] = await Promise.all([
      getStyleFactors().catch(() => []),
      getDerivativeFactors().catch(() => [])
    ])
    factors.value = Array.isArray(f) ? f : []
    allDerived.value = (Array.isArray(d) ? d : []).map(item => ({
      id: item.id, label: `${item.name} (${item.code || item.id})`
    }))
  } finally {
    loading.value = false
  }
}

function openCreate() {
  dialog.isEdit = false
  dialog.visible = true
  dialog.form = { name: '', description: '' }
  dialog.selectedDerivedIds = []
  dialog.weightRows = []
}

function openEdit(row) {
  dialog.isEdit = true
  dialog.visible = true
  dialog.form = { name: row.name, description: row.description || '' }
  dialog.selectedDerivedIds = []
  dialog.weightRows = []
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定删除风格因子「${row.name}」？`, '确认删除', { type: 'warning' })
    await deleteStyleFactor(row.id)
    ElMessage.success('已删除')
    await load()
  } catch (e) { if (e !== 'cancel') throw e }
}

function togglePin(row) {
  ElMessage.info(`置顶功能示意`)
}

watch(() => dialog.selectedDerivedIds, (ids) => {
  dialog.weightRows = ids.map(id => {
    const info = allDerived.value.find(i => i.id === id)
    return { id, name: info?.label || id, weight: ids.length ? Number((100 / ids.length).toFixed(2)) : 0 }
  })
}, { deep: true })

async function submitDialog() {
  if (!dialog.form.name || !dialog.selectedDerivedIds.length) return
  dialog.saving = true
  try {
    const payload = {
      name: dialog.form.name,
      description: dialog.form.description,
      items: dialog.weightRows.map(r => ({ derivativeFactorId: r.id, weight: r.weight }))
    }
    if (dialog.isEdit) {
      await updateStyleFactor(factors.value.find(f => f.name === dialog.form.name)?.id, payload)
    } else {
      await createStyleFactor(payload)
    }
    dialog.visible = false
    ElMessage.success(dialog.isEdit ? '已更新' : '已创建')
    await load()
  } finally {
    dialog.saving = false
  }
}

onMounted(load)
</script>
