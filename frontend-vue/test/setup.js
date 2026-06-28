/**
 * Vitest 全局初始化
 * 所有测试文件运行前自动执行
 */
import { config } from '@vue/test-utils'
import ElementPlus from 'element-plus'

// 全局注册 Element Plus 组件（供组件测试使用）
config.global.plugins = [ElementPlus]

// 全局模拟 ResizeObserver（ECharts 依赖）
global.ResizeObserver = class {
  observe() {}
  unobserve() {}
  disconnect() {}
}

// 全局模拟 window.URL.createObjectURL
if (typeof window !== 'undefined' && !window.URL?.createObjectURL) {
  window.URL = { createObjectURL: () => '', revokeObjectURL: () => {} }
}
