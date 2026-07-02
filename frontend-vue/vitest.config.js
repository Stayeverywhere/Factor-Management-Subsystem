import { defineConfig } from 'vitest/config'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  test: {
    globals: true,
    environment: 'jsdom',  // 改为 jsdom（测试 Vue 组件需要）
    include: ['test/**/*.test.js'],
    exclude: [],
    setupFiles: ['./test/setup.js'],
    coverage: {
      provider: 'v8',
      reporter: ['text', 'lcov', 'html'],
      reportsDirectory: './test/coverage',
      include: ['src/**/*.js', 'src/**/*.vue'],
      exclude: ['src/main.js'],
    },
  },
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),  // 使用 path.resolve
      '@pages': path.resolve(__dirname, './src/pages'),
      '@components': path.resolve(__dirname, './src/components'),
    },
  },
})
