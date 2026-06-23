<template>
  <el-dialog v-model="visible" :title="title" width="760px" @close="$emit('close')">
    <el-steps :active="step" finish-status="success" align-center>
      <el-step :title="stepOneTitle" />
      <el-step title="设置权重" />
    </el-steps>
    <div v-if="step === 0" class="dialog-body">
      <el-transfer v-model="selectedIds" filterable :data="options" :titles="['可选项','已选项']" />
    </div>
    <div v-else class="dialog-body">
      <el-table :data="weights" border>
        <el-table-column prop="name" :label="itemLabel" />
        <el-table-column label="权重">
          <template #default="{ row }"><el-input-number v-model="row.weight" :min="0" :max="100" :step="1" /></template>
        </el-table-column>
      </el-table>
      <div class="weight-summary">权重总和：{{ weightSum }}%</div>
    </div>
    <template #footer>
      <el-button v-if="step > 0" @click="step--">上一步</el-button>
      <el-button v-if="step === 0" type="primary" @click="step++">下一步</el-button>
      <el-button v-else type="primary" @click="$emit('submit', weights)">创建</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
const props = defineProps({ modelValue: Boolean, title: String, stepOneTitle: String, itemLabel: String, options: Array, initialSelected: Array })
const emit = defineEmits(['update:modelValue', 'submit', 'close'])
const visible = computed({ get: () => props.modelValue, set: v => emit('update:modelValue', v) })
const step = ref(0)
const selectedIds = ref([])
const weights = computed(() => selectedIds.value.map(id => ({ id, name: props.options?.find(i => i.key === id)?.label || id, weight: 100 / Math.max(1, selectedIds.value.length) })))
const weightSum = computed(() => weights.value.reduce((s, i) => s + Number(i.weight || 0), 0).toFixed(0))
watch(() => props.modelValue, v => { if (v) { step.value = 0; selectedIds.value = props.initialSelected || [] } })
</script>
