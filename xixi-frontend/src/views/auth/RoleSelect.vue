<template>
  <div class="role-page">
    <div class="role-card">
      <div class="role-header">
        <div style="font-size:48px;margin-bottom:12px">👨‍👩‍👧</div>
        <h1>你是汐汐的谁？</h1>
        <p>选择你的角色，获得专属小短句～</p>
      </div>
      <div class="role-grid">
        <div v-for="r in roles" :key="r.value" :class="['role-item', selected === r.value && 'active']" @click="selected = r.value">
          <span class="role-emoji">{{ r.emoji }}</span>
          <span class="role-label">{{ r.label }}</span>
        </div>
      </div>
      <div v-if="selected" class="role-motto">💕 {{ mottoMap[selected] }}</div>
      <el-button type="primary" size="large" :disabled="!selected" :loading="loading" @click="handleConfirm" style="width:100%;margin-top:24px">确认，进入汐汐的小窝！🌸</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { selectRole } from '@/api/user'

const router = useRouter()
const userStore = useUserStore()
const selected = ref('')
const loading = ref(false)
const roles = [
  { value: 'dad', emoji: '👨', label: '爸爸' },{ value: 'mom', emoji: '👩', label: '妈妈' },
  { value: 'grandpa', emoji: '👴', label: '爷爷' },{ value: 'grandma', emoji: '👵', label: '奶奶' },
  { value: 'outerpa', emoji: '🧓', label: '外公' },{ value: 'outerma', emoji: '👵', label: '外婆' },
  { value: 'other', emoji: '❤️', label: '其他亲属' },
]
const mottoMap = { dad:'爸爸最爱汐汐啦～',mom:'妈妈陪着汐汐一起长大 💕',grandpa:'爷爷每天都想着汐汐呢～',grandma:'奶奶的心肝宝贝！',outerpa:'外公最开心的事就是看见汐汐笑',outerma:'外婆的汐汐真棒！',other:'我们都爱汐汐 🌸' }
async function handleConfirm() {
  loading.value = true
  try { await selectRole(selected.value); await userStore.refreshProfile(); router.push('/') }
  finally { loading.value = false }
}
</script>

<style scoped>
.role-page { min-height: 100vh; background: linear-gradient(135deg, #fff0f7, #fce4ec); display: flex; align-items: center; justify-content: center; padding: 24px; }
.role-card { background: #fff; border-radius: 24px; box-shadow: 0 8px 40px rgba(249, 168, 201, 0.3); padding: 48px 40px; width: 100%; max-width: 480px; }
.role-header { text-align: center; margin-bottom: 32px; }
.role-header h1 { font-size: 22px; color: var(--color-primary-dark); margin-bottom: 8px; }
.role-header p { color: var(--color-text-light); font-size: 14px; }
.role-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 12px; }
.role-item { display: flex; flex-direction: column; align-items: center; gap: 6px; padding: 16px 8px; border: 2px solid var(--color-border); border-radius: 16px; cursor: pointer; transition: all 0.2s; }
.role-item:hover { border-color: var(--color-primary); background: var(--color-primary-light); }
.role-item.active { border-color: var(--color-primary-dark); background: var(--color-primary-light); }
.role-emoji { font-size: 28px; }
.role-label { font-size: 13px; color: var(--color-text); }
.role-motto { margin-top: 20px; padding: 14px; background: var(--color-primary-light); border-radius: 12px; text-align: center; color: var(--color-primary-dark); font-size: 14px; }
</style>
