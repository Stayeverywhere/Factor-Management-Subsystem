import axios from 'axios'

const http = axios.create({
  baseURL: '/api',
  timeout: 30000
})

export async function login(payload) {
  const { data } = await http.post('/auth/login', payload)
  return data.data
}

export async function getDashboard(role) {
  const { data } = await http.get('/dashboard', { params: { role } })
  return data.data
}

export async function getRoles() {
  const { data } = await http.get('/admin/roles')
  return data.data
}

export async function getTraderAccounts(traderId) {
  const { data } = await http.get('/trader/currency/accounts', { params: { traderId } })
  return data.data
}

export async function getFactorCategories() {
  const { data } = await http.get('/factors/categories')
  return data.data
}

export async function getFunds(params) {
  const { data } = await http.get('/factors/funds', { params })
  return data.data
}

export async function getBaseFactors(params) {
  const { data } = await http.get('/factors/base', { params })
  return data.data
}

export async function getBaseFactorValues(params) {
  const { data } = await http.get('/factors/base/value', { params })
  return data.data
}

export async function getDerivativeFactors() {
  const { data } = await http.get('/factors/derived')
  return data.data
}

export async function getDerivativeFactorValues(params) {
  const { data } = await http.get('/factors/derived/value', { params })
  return data.data
}

export async function getStyleFactors() {
  const { data } = await http.get('/factors/style')
  return data.data
}

export async function getStyleFactorValues(params) {
  const { data } = await http.get('/factors/style/value', { params })
  return data.data
}

export async function createDerivativeFactor(payload) {
  const { data } = await http.post('/factors/derived', payload)
  return data.data
}

export async function createStyleFactor(payload) {
  const { data } = await http.post('/factors/style', payload)
  return data.data
}

// ── 衍生因子管理 ──
export async function updateDerivativeFactor(id, payload) {
  const { data } = await http.put(`/factors/derived/${id}`, payload)
  return data.data
}

export async function deleteDerivativeFactor(id) {
  const { data } = await http.delete(`/factors/derived/${id}`)
  return data.data
}

export async function pinDerivativeFactor(id, pinned) {
  const { data } = await http.patch(`/factors/derived/${id}/pin`, { pinned })
  return data.data
}

// ── 风格因子管理 ──
export async function updateStyleFactor(id, payload) {
  const { data } = await http.put(`/factors/style/${id}`, payload)
  return data.data
}

export async function deleteStyleFactor(id) {
  const { data } = await http.delete(`/factors/style/${id}`)
  return data.data
}

export async function pinStyleFactor(id, pinned) {
  const { data } = await http.patch(`/factors/style/${id}/pin`, { pinned })
  return data.data
}
