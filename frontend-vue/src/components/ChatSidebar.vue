<template>
  <div class="chat-sidebar" :class="{ open: visible }">
    <!-- 遮罩层 -->
    <div class="chat-overlay" @click="$emit('close')" />

    <!-- 侧边面板 -->
    <div class="chat-panel">
      <!-- 头部 -->
      <div class="chat-header">
        <div class="header-left">
          <span class="header-icon">🤖</span>
          <span class="header-title">智能助手</span>
        </div>
        <button class="header-close" @click="$emit('close')">✕</button>
      </div>

      <!-- 会话列表 -->
      <div class="chat-conversations" v-if="showConversations && !currentConvId">
        <div class="conv-list">
          <div class="conv-item" v-for="c in conversations" :key="c.id" @click="switchConversation(c.id)">
            <span class="conv-name">{{ c.name || '新对话' }}</span>
            <button class="conv-del" @click.stop="deleteConv(c.id)">✕</button>
          </div>
        </div>
        <button class="conv-new" @click="newConversation">+ 新建对话</button>
      </div>

      <!-- 消息列表 -->
      <div class="chat-messages" ref="msgListRef" v-show="currentConvId !== null">
        <div v-for="(msg, i) in messages" :key="i" class="message-row" :class="msg.role">
          <div class="msg-avatar">{{ msg.role === 'user' ? '👤' : '🤖' }}</div>
          <div class="msg-bubble">
            <div class="msg-text" v-if="msg.content">{{ msg.content }}</div>
            <div class="msg-loading" v-if="msg.loading">
              <span class="dot-pulse"></span>
            </div>
          </div>
        </div>
        <div class="chat-hint" v-if="!messages.length">
          <p>您好！我是智能助手，可以帮您：</p>
          <ul>
            <li>📊 查询基金因子数据</li>
            <li>📈 分析多因子模型</li>
            <li>💡 解答量化投资问题</li>
          </ul>
        </div>
      </div>

      <!-- 输入区 -->
      <div class="chat-input-area">
        <input
          v-model="inputText"
          class="chat-input"
          placeholder="输入消息..."
          @keydown.enter="sendMessage"
          :disabled="sending"
        />
        <button class="chat-send" :disabled="!inputText.trim() || sending" @click="sendMessage">
          <span v-if="!sending">发送</span>
          <span v-else class="sending-dots">● ● ●</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, nextTick } from 'vue'
import { aiChat, aiConversations, aiDeleteConversation } from '../api'

const props = defineProps({ visible: Boolean })
const emit = defineEmits(['close'])

// 状态
const messages = ref([])
const inputText = ref('')
const sending = ref(false)
const conversations = ref([])
const showConversations = ref(false)
const currentConvId = ref(null)
const msgListRef = ref(null)

// 滚动到底部
function scrollBottom() {
  nextTick(() => {
    const el = msgListRef.value
    if (el) el.scrollTop = el.scrollHeight
  })
}

// 加载会话列表
async function loadConversations() {
  try {
    const res = await aiConversations('frontend-user', 50)
    if (res.success) {
      const data = res.data || {}
      conversations.value = data.data || []
    }
  } catch (e) { /* 静默 */ }
}

// 新建对话
function newConversation() {
  currentConvId.value = ''
  messages.value = []
  showConversations.value = false
}

// 切换会话
async function switchConversation(id) {
  currentConvId.value = id
  showConversations.value = false
  messages.value = []
  // 尝试加载历史消息（后端可扩展）
}

// 删除会话
async function deleteConv(id) {
  try {
    await aiDeleteConversation(id, 'frontend-user')
    conversations.value = conversations.value.filter(c => c.id !== id)
    if (currentConvId.value === id) {
      currentConvId.value = null
      messages.value = []
    }
  } catch (e) { /* 静默 */ }
}

// 发送消息
async function sendMessage() {
  const text = inputText.value.trim()
  if (!text || sending.value) return

  inputText.value = ''
  messages.value.push({ role: 'user', content: text })
  scrollBottom()

  // 加载中占位
  const loadingIdx = messages.value.length
  messages.value.push({ role: 'assistant', content: '', loading: true })
  scrollBottom()
  sending.value = true

  try {
    const res = await aiChat({
      query: text,
      user: 'frontend-user',
      conversation_id: currentConvId.value || '',
      inputs: {},
      response_mode: 'blocking',
    })

    messages.value[loadingIdx].loading = false

    if (res.success && res.data) {
      const data = res.data
      messages.value[loadingIdx].content = data.answer || '(无回复)'
      // 首次对话后保存 conversation_id
      if (!currentConvId.value && data.conversation_id) {
        currentConvId.value = data.conversation_id
        loadConversations()
      }
    } else {
      messages.value[loadingIdx].content = '请求失败：' + (res.error || '服务暂不可用')
    }
  } catch (e) {
    messages.value[loadingIdx].loading = false
    messages.value[loadingIdx].content = '网络错误，请稍后重试'
  }
  sending.value = false
  scrollBottom()
}

// 打开时加载会话列表
watch(() => props.visible, (v) => {
  if (v) {
    currentConvId.value = null
    messages.value = []
    showConversations.value = true
    loadConversations()
  }
})
</script>

<style scoped>
.chat-sidebar {
  position: fixed; top: 0; right: 0; width: 100%; height: 100%;
  z-index: 2000; display: flex; justify-content: flex-end;
  pointer-events: none; visibility: hidden; transition: visibility 0.2s;
}
.chat-sidebar.open { pointer-events: auto; visibility: visible; }

.chat-overlay {
  position: absolute; inset: 0; background: rgba(0,0,0,0.25);
  opacity: 0; transition: opacity 0.25s;
}
.chat-sidebar.open .chat-overlay { opacity: 1; }

.chat-panel {
  position: relative; width: 420px; max-width: 100vw; height: 100%;
  background: #fff; display: flex; flex-direction: column;
  box-shadow: -4px 0 24px rgba(0,0,0,0.1);
  transform: translateX(100%); transition: transform 0.25s ease;
}
.chat-sidebar.open .chat-panel { transform: translateX(0); }

/* 头部 */
.chat-header {
  display: flex; align-items: center; justify-content: space-between;
  padding: 14px 16px; border-bottom: 1px solid #ebeef5;
}
.header-left { display: flex; align-items: center; gap: 8px; }
.header-icon { font-size: 22px; }
.header-title { font-size: 16px; font-weight: 600; color: #1f2f56; }
.header-close {
  background: none; border: none; font-size: 18px; color: #909399;
  cursor: pointer; padding: 4px 8px; border-radius: 4px;
}
.header-close:hover { background: #f5f7fa; }

/* 会话列表 */
.chat-conversations { flex: 1; overflow-y: auto; padding: 12px; }
.conv-list { display: flex; flex-direction: column; gap: 4px; }
.conv-item {
  display: flex; align-items: center; justify-content: space-between;
  padding: 10px 12px; border-radius: 6px; cursor: pointer;
}
.conv-item:hover { background: #f5f7fa; }
.conv-name { font-size: 14px; color: #303133; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.conv-del {
  background: none; border: none; font-size: 12px; color: #c0c4cc;
  cursor: pointer; visibility: hidden; padding: 2px 4px;
}
.conv-item:hover .conv-del { visibility: visible; }
.conv-del:hover { color: #f56c6c; }
.conv-new {
  width: 100%; margin-top: 12px; padding: 8px; border: 1px dashed #dcdfe6;
  border-radius: 6px; background: #fafafa; cursor: pointer; font-size: 14px; color: #606266;
}
.conv-new:hover { border-color: #409eff; color: #409eff; }

/* 消息区 */
.chat-messages {
  flex: 1; overflow-y: auto; padding: 16px;
  display: flex; flex-direction: column; gap: 16px;
}
.chat-hint {
  margin: auto; text-align: center; color: #909399; font-size: 13px; line-height: 1.8;
}
.chat-hint ul { padding: 0; list-style: none; }
.chat-hint li { padding: 2px 0; }
.message-row { display: flex; gap: 10px; max-width: 90%; }
.message-row.user { align-self: flex-end; flex-direction: row-reverse; }
.message-row.assistant { align-self: flex-start; }
.msg-avatar {
  width: 32px; height: 32px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-size: 16px; flex-shrink: 0;
  background: #f0f2f5;
}
.message-row.user .msg-avatar { background: #ecf5ff; }
.msg-bubble {
  padding: 10px 14px; border-radius: 12px; font-size: 14px; line-height: 1.6; color: #303133;
}
.message-row.user .msg-bubble {
  background: #409eff; color: #fff; border-bottom-right-radius: 4px;
}
.message-row.assistant .msg-bubble {
  background: #f0f2f5; border-bottom-left-radius: 4px;
}
.msg-loading { padding: 4px 0; }
.dot-pulse {
  display: inline-block; width: 6px; height: 6px; border-radius: 50%;
  background: #409eff; animation: pulse 1.2s infinite;
}
@keyframes pulse { 0%,100% { opacity: 0.3; } 50% { opacity: 1; } }

/* 输入区 */
.chat-input-area {
  display: flex; gap: 8px; padding: 12px 16px;
  border-top: 1px solid #ebeef5; background: #fafafa;
}
.chat-input {
  flex: 1; height: 40px; padding: 0 14px; border: 1px solid #dcdfe6;
  border-radius: 20px; font-size: 14px; outline: none; background: #fff;
}
.chat-input:focus { border-color: #409eff; }
.chat-send {
  height: 40px; padding: 0 20px; border: none; border-radius: 20px;
  background: #409eff; color: #fff; font-size: 14px; cursor: pointer;
}
.chat-send:disabled { background: #c0c4cc; cursor: not-allowed; }
.sending-dots { letter-spacing: 2px; animation: pulse 1s infinite; }
</style>
