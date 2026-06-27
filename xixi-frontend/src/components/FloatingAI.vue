<template>
  <div class="ai-fab" @click="toggleDrawer" title="汐汐小助手">
    <span class="fab-icon">🤖</span>
    <span class="fab-dot" v-if="unread"></span>
  </div>
  <el-drawer v-model="drawerVisible" direction="rtl" :size="360" :with-header="false" class="ai-drawer">
    <div class="chat-container">
      <div class="chat-header">
        <span>🤖 汐汐小助手</span>
        <el-button size="small" text @click="clearChat">清除记录</el-button>
        <el-button size="small" text @click="drawerVisible = false">✕</el-button>
      </div>
      <div class="chat-messages" ref="messagesRef">
        <div v-for="(msg, i) in messages" :key="i" :class="['chat-msg', msg.role]">
          <div class="msg-avatar">{{ msg.role === 'user' ? '👤' : '🤖' }}</div>
          <div class="msg-bubble" v-html="renderContent(msg.content)"></div>
        </div>
        <div v-if="loading" class="chat-msg assistant">
          <div class="msg-avatar">🤖</div>
          <div class="msg-bubble loading-bubble">
            <span class="dot"></span><span class="dot"></span><span class="dot"></span>
          </div>
        </div>
      </div>
      <div class="chat-input">
        <el-input v-model="inputText" placeholder="问我任何事情吧～ 💕"
          @keydown.enter.prevent="sendMessage" :disabled="loading" clearable />
        <el-button type="primary" @click="sendMessage" :loading="loading" :disabled="!inputText.trim()">发送</el-button>
      </div>
    </div>
  </el-drawer>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { marked } from 'marked'
import { chat, clearSession } from '@/api/ai'

const drawerVisible = ref(false)
const inputText = ref('')
const loading = ref(false)
const unread = ref(false)
const messages = ref([])
const messagesRef = ref(null)

const WELCOME = '我是汐汐小助手，你要干啊？😄'

let sessionId = localStorage.getItem('xixi_ai_session')
if (!sessionId) {
  sessionId = Math.random().toString(36).substring(2) + Date.now().toString(36)
  localStorage.setItem('xixi_ai_session', sessionId)
}

function toggleDrawer() {
  drawerVisible.value = !drawerVisible.value
  unread.value = false
  if (drawerVisible.value && messages.value.length === 0) {
    messages.value.push({ role: 'assistant', content: WELCOME })
  }
}

function renderContent(content) {
  return marked.parse(content || '')
}

async function sendMessage() {
  const text = inputText.value.trim()
  if (!text || loading.value) return
  inputText.value = ''
  messages.value.push({ role: 'user', content: text })
  loading.value = true
  await scrollBottom()
  try {
    const res = await chat(text, sessionId)
    messages.value.push({ role: 'assistant', content: res.data.reply })
  } catch (e) {
    messages.value.push({ role: 'assistant', content: '抱歉，我现在有点忙，稍后再试吧 🙈' })
  } finally {
    loading.value = false
    await scrollBottom()
  }
}

async function clearChat() {
  await clearSession(sessionId)
  sessionId = Math.random().toString(36).substring(2) + Date.now().toString(36)
  localStorage.setItem('xixi_ai_session', sessionId)
  messages.value = [{ role: 'assistant', content: WELCOME }]
}

async function scrollBottom() {
  await nextTick()
  if (messagesRef.value) messagesRef.value.scrollTop = messagesRef.value.scrollHeight
}
</script>

<style scoped>
.ai-fab {
  position: fixed; right: 24px; bottom: 80px;
  width: 56px; height: 56px;
  background: linear-gradient(135deg, var(--color-primary), var(--color-primary-dark));
  border-radius: 50%; display: flex; align-items: center; justify-content: center;
  cursor: pointer; box-shadow: 0 4px 16px rgba(233, 30, 122, 0.4);
  z-index: 9999; transition: transform 0.2s, box-shadow 0.2s;
}
.ai-fab:hover { transform: scale(1.1); box-shadow: 0 6px 20px rgba(233, 30, 122, 0.5); }
.fab-icon { font-size: 24px; }
.fab-dot { position: absolute; top: 4px; right: 4px; width: 10px; height: 10px; background: #ff4949; border-radius: 50%; border: 2px solid #fff; }
.chat-container { display: flex; flex-direction: column; height: 100vh; }
.chat-header { display: flex; align-items: center; padding: 16px; border-bottom: 1px solid var(--color-border); font-weight: 600; color: var(--color-primary-dark); gap: 8px; }
.chat-header span { flex: 1; }
.chat-messages { flex: 1; overflow-y: auto; padding: 16px; display: flex; flex-direction: column; gap: 12px; }
.chat-msg { display: flex; gap: 8px; align-items: flex-start; }
.chat-msg.user { flex-direction: row-reverse; }
.msg-avatar { font-size: 20px; flex-shrink: 0; margin-top: 2px; }
.msg-bubble { max-width: 75%; padding: 10px 14px; border-radius: 16px; font-size: 14px; line-height: 1.6; word-break: break-word; }
.chat-msg.assistant .msg-bubble { background: #fff; border: 1px solid var(--color-border); border-radius: 4px 16px 16px 16px; }
.chat-msg.user .msg-bubble { background: var(--color-primary-light); border-radius: 16px 4px 16px 16px; color: var(--color-primary-dark); }
.loading-bubble { display: flex; gap: 4px; align-items: center; padding: 14px; }
.dot { width: 6px; height: 6px; background: var(--color-primary); border-radius: 50%; animation: bounce 1.2s infinite; }
.dot:nth-child(2) { animation-delay: 0.2s; }
.dot:nth-child(3) { animation-delay: 0.4s; }
@keyframes bounce { 0%, 80%, 100% { transform: scale(0.8); opacity: 0.5; } 40% { transform: scale(1.2); opacity: 1; } }
.chat-input { padding: 12px 16px; border-top: 1px solid var(--color-border); display: flex; gap: 8px; }
</style>
