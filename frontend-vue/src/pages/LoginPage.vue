<template>
  <div class="login-screen">
    <div class="login-card">
      <div class="brand-header">
        <div class="brand-kicker">Factor Management</div>
        <div class="login-title">因子管理与投顾管理平台</div>
      </div>

      <el-form :model="form" class="login-form" label-position="top">
        <el-form-item label="身份">
          <el-select v-model="form.userType" style="width: 100%">
            <el-option label="系统超级管理员" value="SYSTEM_ADMIN" />
            <el-option label="业务经理" value="TRADER" />
            <el-option label="客户" value="CUSTOMER" />
          </el-select>
        </el-form-item>
        <el-form-item label="账号"><el-input v-model="form.username" placeholder="请输入账号" /></el-form-item>
        <el-form-item label="密码"><el-input v-model="form.password" type="password" placeholder="请输入密码" show-password /></el-form-item>
        <el-button type="primary" class="primary-btn full" :loading="loading" @click="handleLogin">登录</el-button>
        <div class="login-hint">{{ message }}</div>
      </el-form>
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
const message = ref('请选择身份后登录')
const form = ref({ username: 'admin', password: 'admin123', userType: 'SYSTEM_ADMIN' })

async function handleLogin() {
  loading.value = true
  try {
    const res = await login(form.value)
    localStorage.setItem('session', JSON.stringify(res))
    ElMessage.success(`登录成功：${res.account.displayName}`)
    router.push(`/workspace/${res.account.userType.toLowerCase()}/home`)
  } catch (e) {
    message.value = e?.response?.data?.message || e.message || '登录失败'
    ElMessage.error(message.value)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-screen {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(180deg, #f6f9ff 0%, #eef3fb 100%);
  padding: 24px;
}

.login-card {
  width: min(420px, 100%);
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(42, 77, 160, 0.08);
  border-radius: 24px;
  box-shadow: 0 18px 50px rgba(23, 39, 77, 0.12);
  padding: 40px 32px 32px;
  backdrop-filter: blur(10px);
}

.brand-header {
  text-align: center;
  margin-bottom: 28px;
}

.brand-kicker {
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.28em;
  text-transform: uppercase;
  color: #5f77b8;
  margin-bottom: 12px;
}

.login-title {
  font-size: 28px;
  line-height: 1.25;
  font-weight: 800;
  color: #1f2f56;
  letter-spacing: 0.04em;
  text-align: center;
}

.login-form {
  margin-top: 8px;
}

.login-form :deep(.el-form-item__label) {
  font-weight: 600;
  color: #42526b;
}

.login-hint {
  margin-top: 14px;
  text-align: center;
  color: #6a7487;
  font-size: 13px;
}
</style>
