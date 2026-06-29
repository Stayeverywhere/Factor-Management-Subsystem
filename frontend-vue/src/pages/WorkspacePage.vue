<template>
  <div v-if="session" class="workspace-shell" :class="themeClass">
    <header class="workspace-header">
      <div class="workspace-brand">
        <div class="brand-mark">F</div>
        <div>
          <div class="brand-title">因子管理平台</div>
          <div class="brand-subtitle">Factor Management Subsystem</div>
        </div>
      </div>

      <nav class="workspace-nav">
        <RouterLink
          v-for="item in navItems"
          :key="item.key"
          :to="item.path"
          class="nav-item"
          :class="{ active: route.path === item.path }"
        >
          {{ item.label }}
        </RouterLink>
      </nav>

      <div class="workspace-account">
        <el-button class="theme-toggle" circle plain @click="toggleTheme">{{ isDark ? '☾' : '☀' }}</el-button>
        <el-button class="ai-btn" circle @click="chatVisible = true" title="智能助手">
          🤖
        </el-button>
        <el-dropdown>
          <span class="account-chip">{{ session.account.displayName }}</span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item>当前身份：{{ session.account.userType }}</el-dropdown-item>
              <el-dropdown-item divided @click="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </header>

    <main class="workspace-main">
      <router-view v-slot="{ Component }">
        <transition name="fade-slide" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>

    <!-- 智能助手侧边栏 -->
    <ChatSidebar :visible="chatVisible" @close="chatVisible = false" />
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import ChatSidebar from '../components/ChatSidebar.vue'

const router = useRouter()
const route = useRoute()
const session = ref(JSON.parse(localStorage.getItem('session') || 'null'))
const isDark = ref(localStorage.getItem('theme') === 'dark')
const themeClass = computed(() => (isDark.value ? 'theme-dark' : 'theme-light'))
const chatVisible = ref(false)

const navItems = [
  { key: 'factor-overview', label: '因子查询', path: '/workspace/customer/factor-overview' },
  { key: 'derived-factor', label: '衍生因子管理', path: '/workspace/customer/derived-factor' },
  { key: 'style-factor', label: '风格因子管理', path: '/workspace/customer/style-factor' },
  { key: 'multi-factor', label: '多因子分析', path: '/workspace/customer/multi-factor' }
]

if (!session.value) router.replace('/login')

function toggleTheme() { isDark.value = !isDark.value; localStorage.setItem('theme', isDark.value ? 'dark' : 'light') }
function logout() { localStorage.removeItem('session'); router.replace('/login') }

watch(isDark, () => document.documentElement.dataset.theme = isDark.value ? 'dark' : 'light', { immediate: true })
</script>

<style scoped>
.ai-btn {
  font-size: 18px !important; border: none !important;
  background: transparent !important;
}
.ai-btn:hover { background: #f0f2f5 !important; }
</style>
