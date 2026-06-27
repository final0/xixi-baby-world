<template>
  <div class="auth-page">
    <div class="auth-card">
      <div class="auth-header">
        <div class="auth-logo">💕</div>
        <h1>加入汐汐的小家庭</h1>
        <p>一起记录美好时光</p>
      </div>
      <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="4-20位字母数字下划线" prefix-icon="User" size="large" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="用于接收验证码" prefix-icon="Message" size="large" />
        </el-form-item>
        <el-form-item label="邮箱验证码" prop="emailCode">
          <div class="code-row">
            <el-input v-model="form.emailCode" placeholder="请输入6位验证码" size="large" style="flex:1" />
            <el-button :disabled="countdown > 0" @click="handleSendCode" size="large" plain>
              {{ countdown > 0 ? `${countdown}s后重发` : '获取验证码' }}
            </el-button>
          </div>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="8-20位，含字母和数字"
            prefix-icon="Lock" show-password size="large" />
        </el-form-item>
        <el-button type="primary" size="large" :loading="loading" @click="handleRegister" style="width:100%;margin-top:8px">
          立即加入 🌸
        </el-button>
      </el-form>
      <div class="auth-footer">已有账号？<router-link to="/login">立即登录</router-link></div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { sendEmailCode, register } from '@/api/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const countdown = ref(0)

const form = ref({ username: '', email: '', emailCode: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名' }, { min: 4, max: 20, message: '4-20位字符' }, { pattern: /^[a-zA-Z0-9_]+$/, message: '仅字母数字下划线' }],
  email: [{ required: true, message: '请输入邮箱' }, { type: 'email', message: '邮箱格式不正确' }],
  emailCode: [{ required: true, message: '请输入验证码' }],
  password: [{ required: true, message: '请输入密码' }, { min: 8, max: 20, message: '8-20位字符' }, { pattern: /^(?=.*[A-Za-z])(?=.*\d).+$/, message: '须含字母和数字' }],
}

async function handleSendCode() {
  if (!form.value.email) return ElMessage.warning('请先输入邮箱')
  await sendEmailCode(form.value.email)
  ElMessage.success('验证码已发送，请查收邮件 💌')
  countdown.value = 60
  const timer = setInterval(() => { countdown.value--; if (countdown.value <= 0) clearInterval(timer) }, 1000)
}

async function handleRegister() {
  await formRef.value.validate()
  loading.value = true
  try {
    await register(form.value)
    ElMessage.success('注册成功！欢迎加入汐汐的小窝 🌸')
    router.push('/login')
  } finally { loading.value = false }
}
</script>

<style scoped>
.auth-page { min-height: 100vh; background: linear-gradient(135deg,#fff0f7,#fce4ec); display: flex; align-items: center; justify-content: center; padding: 24px; }
.auth-card { background: #fff; border-radius: 24px; box-shadow: 0 8px 40px rgba(249,168,201,0.3); padding: 48px 40px; width: 100%; max-width: 440px; }
.auth-header { text-align: center; margin-bottom: 32px; }
.auth-logo { font-size: 48px; margin-bottom: 12px; }
.auth-header h1 { font-size: 22px; color: var(--color-primary-dark); margin-bottom: 6px; }
.auth-header p { color: var(--color-text-light); font-size: 14px; }
.code-row { display: flex; gap: 12px; }
.auth-footer { text-align: center; margin-top: 20px; color: var(--color-text-light); font-size: 14px; }
.auth-footer a { color: var(--color-primary-dark); font-weight: 600; }
</style>
