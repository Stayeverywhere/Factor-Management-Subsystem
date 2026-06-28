import { createRouter, createWebHistory } from 'vue-router'
import LoginPage from './pages/LoginPage.vue'
import WorkspacePage from './pages/WorkspacePage.vue'
import EmptyPage from './pages/EmptyPage.vue'
import FactorOverviewPage from './pages/FactorOverviewPage.vue'
import DerivativeFactorPage from './pages/DerivativeFactorPage.vue'
import StyleFactorPage from './pages/StyleFactorPage.vue'

const customerChildren = [
  { path: '', redirect: '/workspace/customer/factor-overview' },
  { path: 'factor-overview', component: FactorOverviewPage, props: { title: '因子查询' } },
  { path: 'derived-factor', component: DerivativeFactorPage },
  { path: 'style-factor', component: StyleFactorPage },
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
