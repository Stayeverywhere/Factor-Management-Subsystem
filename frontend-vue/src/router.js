import { createRouter, createWebHistory } from 'vue-router'
import LoginPage from './pages/LoginPage.vue'
import WorkspacePage from './pages/WorkspacePage.vue'
import EmptyPage from './pages/EmptyPage.vue'
import FactorOverviewPage from './pages/FactorOverviewPage.vue'

const customerChildren = [
  { path: '', redirect: '/workspace/customer/factor-overview' },
  { path: 'factor-overview', component: FactorOverviewPage, props: { title: '因子查询' } },
  { path: 'derived-factor', component: EmptyPage, props: { title: '衍生因子管理' } },
  { path: 'style-factor', component: EmptyPage, props: { title: '风格因子管理' } },
  { path: 'multi-factor', component: EmptyPage, props: { title: '多因子分析' } }
]

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: LoginPage },
  {
    path: '/workspace/:role',
    component: WorkspacePage,
    props: true,
    children: [
      ...customerChildren,
      { path: 'home', redirect: '/workspace/customer/factor-overview' },
      { path: ':pathMatch(.*)*', redirect: '/workspace/customer/factor-overview' }
    ]
  }
]

export default createRouter({
  history: createWebHistory(),
  routes
})
