import { createRouter, createWebHistory } from 'vue-router'
import LoginPage from './pages/LoginPage.vue'
import WorkspacePage from './pages/WorkspacePage.vue'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: LoginPage },
  { path: '/workspace/:role', component: WorkspacePage, props: true }
]

export default createRouter({
  history: createWebHistory(),
  routes
})
