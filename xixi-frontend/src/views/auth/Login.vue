<template>
  <div class="auth-page">
    <div class="auth-card">
      <div class="auth-header">
        <div class="auth-logo">🌸</div>
        <h1>欢迎回来</h1>
        <p>快来看看汐汐今天怎么样啊～</p>
      </div>
      <el-form :model="form" :rules="rules" ref="formRef" label-position="top" @submit.prevent="handleLogin">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" prefix-icon="User" size="large" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" prefix-icon="Lock" show-password size="large" />
        </el-form-item>
        <el-form-item label="验证码" prop="captchaAnswer">
          <div class="captcha-row">
            <el-input v-model="form.captchaAnswer" placeholder="请输入计算结果" prefix-icon="Edit" size="large" style="flex:1" />
            <img :src="captchaImg" @click="refreshCaptcha" class="captcha-img" title="点击刷新" />
          </div>
        </el-form-item>
        <el-button type="primary" size="large" :loading="loading" @click="handleLogin" style="width:100%;margin-top:8px">
          进入汐汐的小窝 💕
        </el-button>
      </el-form>
      <div class="auth-footer">还没有账号？<router-link to="/register">立即加入</router-link></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getCaptcha, login } from '@/api/auth'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)
const captchaImg = ref('')
const captchaId = ref('')
const form = ref({ username: '', password: '', captchaAnswer: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  captchaAnswer: [{ required: true, message: '请输入验证码', trigger: 'blur' }],
}
async function refreshCaptcha() {
  const res = await getCaptcha()
  captchaId.value = res.data.captchaId
  captchaImg.value = res.data.image
}
async function handleLogin() {
  await formRef.value.validate()
  loading.value = true
  try {
    const res = await login({ username: form.value.username, password: form.value.password, captchaId: captchaId.value, captchaAnswer: form.value.captchaAnswer })
    userStore.setLoginData(res.data)
    if (res.data.firstLogin) { router.push('/role-select') } else { router.push('/') }
  } catch (e) { refreshCaptcha(); form.value.captchaAnswer = '' }
  finally { loading.value = false }
}
onMounted(refreshCaptcha)
</script>

<style scoped>
.auth-page { min-height: 100vh; background: linear-gradient(135deg, #fff0f7, #fce4ec); display: flex; align-items: center; justify-content: center; padding: 24px; }
.auth-card { background: #fff; border-radius: 24px; box-shadow: 0 8px 40px rgba(249, 168, 201, 0.3); padding: 48px 40px; width: 100%; max-width: 420px; }
.auth-header { text-align: center; margin-bottom: 32px; }
.auth-logo { font-size: 48px; margin-bottom: 12px; }
.auth-header h1 { font-size: 24px; color: var(--color-primary-dark); margin-bottom: 6px; }
.auth-header p { color: var(--color-text-light); font-size: 14px; }
.captcha-row { display: flex; gap: 12px; align-items: center; }
.captcha-img { height: 40px; border-radius: 8px; cursor: pointer; border: 1px solid var(--color-border); }
.auth-footer { text-align: center; margin-top: 20px; color: var(--color-text-light); font-size: 14px; }
.auth-footer a { color: var(--color-primary-dark); font-weight: 600; }
</style>
