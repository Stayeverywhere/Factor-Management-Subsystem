<template>
  <div class="login-screen">
    <div class="login-card">
      <div class="login-hero">
        <div class="brand-mark large">F</div>
        <div>
          <div class="login-title">因子管理与投顾管理平台</div>
          <div class="login-subtitle">专业 · 克制 · 金融感 · 数据化</div>
        </div>
      </div>
      <div class="login-copy">
        <div class="login-copy-title">请选择身份并登录</div>
        <div class="login-copy-subtitle">三个模拟账号将进入不同工作台：超级管理员、业务经理、客户</div>
      </div>
      <el-space wrap class="quick-accounts">
        <el-button v-for="item in demoAccounts" :key="item.userType" :type="form.userType===item.userType ? 'primary' : 'default'" @click="fillDemo(item)">
          {{ item.label }}
        </el-button>
      </el-space>
      <el-form :model="form" class="login-form" label-position="top">
        <el-form-item label="账号"><el-input v-model="form.username" placeholder="请输入账号" /></el-form-item>
        <el-form-item label="密码"><el-input v-model="form.password" type="password" placeholder="请输入密码" show-password /></el-form-item>
        <el-form-item label="身份">
          <el-select v-model="form.userType" style="width: 100%">
            <el-option label="系统超级管理员" value="SYSTEM_ADMIN" />
            <el-option label="业务经理" value="TRADER" />
            <el-option label="客户" value="CUSTOMER" />
          </el-select>
        </el-form-item>
        <el-button type="primary" class="primary-btn full" :loading="loading" @click="handleLogin">登录进入系统</el-button>
        <div class="login-hint">{{ message }}</div>
      </el-form>
      <div class="demo-hint">
        <div>超级管理员：admin / admin123</div>
        <div>业务经理：trader / trader123</div>
        <div>客户：customer / customer123</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '../api'

const router = useRouter()
const loading = ref(false)
const message = ref('请选择账号后登录')
const form = ref({ username: 'admin', password: 'admin123', userType: 'SYSTEM_ADMIN' })
const demoAccounts = [
  { label: '超级管理员账号', userType: 'SYSTEM_ADMIN', username: 'admin', password: 'admin123' },
  { label: '业务经理账号', userType: 'TRADER', username: 'trader', password: 'trader123' },
  { label: '客户账号', userType: 'CUSTOMER', username: 'customer', password: 'customer123' }
]
function fillDemo(item) { form.value = { ...item } }
async function handleLogin() {
  loading.value = true
  try {
    const res = await login(form.value)
    localStorage.setItem('session', JSON.stringify(res))
    ElMessage.success(`登录成功：${res.account.displayName}`)
    router.push(`/workspace/${res.account.userType.toLowerCase()}`)
  } catch (e) {
    message.value = e?.response?.data?.message || e.message || '登录失败'
    ElMessage.error(message.value)
  } finally {
    loading.value = false
  }
}
</script>
