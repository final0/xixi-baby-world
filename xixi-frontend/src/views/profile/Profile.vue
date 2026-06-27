<template>
  <div class="page-container">
    <h1 class="page-title">👤 个人信息</h1>
    <div class="profile-grid">
      <div class="xixi-card">
        <h3 class="card-title">基本信息</h3>
        <div class="avatar-section">
          <el-avatar :size="80" :src="form.avatarUrl" @click="avatarInput.click()">
            {{ form.nickname?.charAt(0) }}
          </el-avatar>
          <div>
            <el-button size="small" @click="avatarInput.click()">更换头像</el-button>
            <input ref="avatarInput" type="file" accept="image/*" hidden @change="handleAvatarChange" />
            <p style="font-size:12px;color:var(--color-text-light);margin-top:4px">支持jpg/png，最大5MB</p>
          </div>
        </div>
        <el-form :model="form" label-width="80px" style="margin-top:20px">
          <el-form-item label="用户名"><el-input :value="form.username" disabled /></el-form-item>
          <el-form-item label="昵称"><el-input v-model="form.nickname" /></el-form-item>
          <el-form-item label="邮箱"><el-input :value="form.email" disabled /></el-form-item>
          <el-form-item label="联系方式"><el-input v-model="form.phone" /></el-form-item>
          <el-form-item label="角色">
            <el-select v-model="form.role" style="width:100%">
              <el-option v-for="r in roles" :key="r.value" :label="r.label" :value="r.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="专属短句"><el-input :value="mottoMap[form.role]" disabled /></el-form-item>
        </el-form>
        <div style="display:flex;justify-content:flex-end;margin-top:12px">
          <el-button type="primary" :loading="saving" @click="handleSave">保存修改</el-button>
        </div>
      </div>
      <div class="xixi-card">
        <h3 class="card-title">修改密码</h3>
        <el-form :model="pwdForm" label-width="90px" style="margin-top:12px">
          <el-form-item label="当前密码"><el-input v-model="pwdForm.oldPassword" type="password" show-password /></el-form-item>
          <el-form-item label="新密码"><el-input v-model="pwdForm.newPassword" type="password" show-password /></el-form-item>
        </el-form>
        <div style="display:flex;justify-content:flex-end;margin-top:12px">
          <el-button type="primary" :loading="pwdSaving" @click="handleChangePassword">修改密码</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { getProfile, updateProfile, uploadAvatar, changePassword } from '@/api/user'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const avatarInput = ref(null)
const saving = ref(false)
const pwdSaving = ref(false)
const form = ref({ username:'',nickname:'',email:'',phone:'',role:'',avatarUrl:'' })
const pwdForm = ref({ oldPassword:'', newPassword:'' })

const roles = [
  {value:'dad',label:'👨 爸爸'},{value:'mom',label:'👩 妈妈'},
  {value:'grandpa',label:'👴 爷爷'},{value:'grandma',label:'👵 奶奶'},
  {value:'outerpa',label:'🧓 外公'},{value:'outerma',label:'👵 外婆'},
  {value:'other',label:'❤️ 其他亲属'}
]
const mottoMap = {
  dad:'爸爸最爱汐汐啦～',mom:'妈妈陪着汐汐一起长大 💕',
  grandpa:'爷爷每天都想着汐汐呢～',grandma:'奶奶的心肝宝贝！',
  outerpa:'外公最开心的事就是看见汐汐笑',outerma:'外婆的汐汐真棒！',
  other:'我们都爱汐汐 🌸'
}

onMounted(async () => {
  const res = await getProfile()
  const d = res.data
  form.value = { username:d.username, nickname:d.nickname, email:d.email, phone:d.phone||'', role:d.role, avatarUrl:d.avatarUrl||'' }
})

async function handleAvatarChange(e) {
  const file = e.target.files[0]
  if (!file) return
  const fd = new FormData()
  fd.append('file', file)
  const res = await uploadAvatar(fd)
  form.value.avatarUrl = res.data.avatarUrl
  await userStore.refreshProfile()
  ElMessage.success('头像更新成功 🌸')
}

async function handleSave() {
  saving.value = true
  try {
    await updateProfile({ nickname: form.value.nickname, phone: form.value.phone, role: form.value.role })
    await userStore.refreshProfile()
    ElMessage.success('保存成功 💕')
  } finally { saving.value = false }
}

async function handleChangePassword() {
  if (!pwdForm.value.oldPassword || !pwdForm.value.newPassword) return ElMessage.warning('请填写完整')
  pwdSaving.value = true
  try {
    await changePassword(pwdForm.value)
    ElMessage.success('密码修改成功')
    pwdForm.value = { oldPassword:'', newPassword:'' }
  } finally { pwdSaving.value = false }
}
</script>

<style scoped>
.page-title { font-size: 22px; margin-bottom: 24px; }
.profile-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 24px; }
@media (max-width: 768px) { .profile-grid { grid-template-columns: 1fr; } }
.card-title { font-size: 16px; font-weight: 600; margin-bottom: 16px; padding-bottom: 12px; border-bottom: 1px solid var(--color-border); }
.avatar-section { display: flex; align-items: center; gap: 20px; }
</style>
