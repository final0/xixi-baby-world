<template>
  <div class="page-container">
    <h1 class="page-title">⚙️ 管理后台</h1>
    <el-tabs v-model="activeTab" type="border-card">
      <el-tab-pane label="🌸 宝宝信息" name="baby">
        <el-form :model="babyForm" label-width="100px" style="max-width:500px">
          <el-form-item label="宝宝名字"><el-input v-model="babyForm.name" /></el-form-item>
          <el-form-item label="生日"><el-date-picker v-model="babyForm.birthday" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
          <el-form-item label="出生体重(kg)"><el-input-number v-model="babyForm.birthWeight" :precision="2" :step="0.1" /></el-form-item>
          <el-form-item label="出生身高(cm)"><el-input-number v-model="babyForm.birthHeight" :precision="1" :step="0.5" /></el-form-item>
          <el-form-item label="简介"><el-input v-model="babyForm.intro" type="textarea" :rows="2" /></el-form-item>
          <el-form-item><el-button type="primary" :loading="savingBaby" @click="saveBaby">保存</el-button></el-form-item>
        </el-form>
      </el-tab-pane>
      <el-tab-pane label="👥 用户管理" name="users">
        <el-table :data="users" stripe>
          <el-table-column prop="username" label="用户名" />
          <el-table-column prop="nickname" label="昵称" />
          <el-table-column prop="email" label="邮箱" />
          <el-table-column prop="role" label="角色" />
          <el-table-column label="管理员"><template #default="{ row }"><el-tag :type="row.isAdmin ? 'danger' : 'info'">{{ row.isAdmin ? '是' : '否' }}</el-tag></template></el-table-column>
          <el-table-column label="状态"><template #default="{ row }"><el-tag :type="row.status ? 'success' : 'danger'">{{ row.status ? '正常' : '禁用' }}</el-tag></template></el-table-column>
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button size="small" :type="row.status ? 'danger' : 'success'" plain @click="toggleUserStatus(row)">{{ row.status ? '禁用' : '启用' }}</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="💕 小短句" name="mottos">
        <div style="margin-bottom:16px">
          <el-input v-model="newMotto.content" placeholder="输入短句内容" style="width:300px;margin-right:12px" />
          <el-input v-model="newMotto.author" placeholder="来源/作者（可选）" style="width:150px;margin-right:12px" />
          <el-button type="primary" @click="handleAddMotto">添加</el-button>
        </div>
        <el-table :data="mottos" stripe>
          <el-table-column prop="content" label="内容" />
          <el-table-column prop="author" label="来源" width="100" />
          <el-table-column label="状态" width="80"><template #default="{ row }"><el-tag :type="row.isActive ? 'success' : 'info'">{{ row.isActive ? '启用' : '停用' }}</el-tag></template></el-table-column>
          <el-table-column label="操作" width="140">
            <template #default="{ row }">
              <el-button size="small" text @click="toggleMotto(row)">{{ row.isActive ? '停用' : '启用' }}</el-button>
              <el-button size="small" text type="danger" @click="handleDeleteMotto(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="⚙️ 系统配置" name="config">
        <el-form label-width="120px" style="max-width:540px">
          <el-divider>SMTP 邮件配置</el-divider>
          <el-form-item v-for="key in smtpKeys" :key="key" :label="configLabels[key]">
            <div style="display:flex;gap:8px">
              <el-input v-model="configs[key]" :type="key==='smtp_password'?'password':'text'" show-password />
              <el-button @click="saveConfig(key, configs[key])">保存</el-button>
            </div>
          </el-form-item>
          <el-form-item label="测试邮件">
            <div style="display:flex;gap:8px"><el-input v-model="testEmailTo" placeholder="收件邮箱" /><el-button type="primary" @click="handleTestEmail">发送测试</el-button></div>
          </el-form-item>
          <el-divider>AI 配置</el-divider>
          <el-form-item v-for="key in aiKeys" :key="key" :label="configLabels[key]">
            <div style="display:flex;gap:8px">
              <el-input v-model="configs[key]" :type="key==='ai_api_key'?'password':'text'" show-password />
              <el-button @click="saveConfig(key, configs[key])">保存</el-button>
            </div>
          </el-form-item>
        </el-form>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { updateBaby, getConfigs, updateConfig, testEmail, listUsers, updateUserStatus, listMottos, addMotto, deleteMotto, updateMottoStatus } from '@/api/admin'
import { getHomeInfo } from '@/api/home'
import { ElMessage, ElMessageBox } from 'element-plus'

const activeTab = ref('baby')
const savingBaby = ref(false)
const babyForm = ref({ name:'汐汐', birthday:'', birthWeight:null, birthHeight:null, intro:'' })
const users = ref([]); const mottos = ref([])
const newMotto = ref({ content:'', author:'' })
const configs = ref({}); const testEmailTo = ref('')
const smtpKeys = ['smtp_host','smtp_port','smtp_username','smtp_password','smtp_ssl']
const aiKeys = ['ai_api_key','ai_base_url','ai_model']
const configLabels = { smtp_host:'SMTP 服务器',smtp_port:'SMTP 端口',smtp_username:'发件邮箱',smtp_password:'授权码',smtp_ssl:'启用SSL',ai_api_key:'API Key',ai_base_url:'API 地址',ai_model:'模型名称' }

onMounted(async () => {
  const homeRes = await getHomeInfo(); if (homeRes.data.baby) Object.assign(babyForm.value, homeRes.data.baby)
  const userRes = await listUsers(); users.value = userRes.data.list
  const mottoRes = await listMottos(); mottos.value = mottoRes.data
  const cfgRes = await getConfigs(); configs.value = cfgRes.data
})
async function saveBaby() { savingBaby.value = true; try { await updateBaby(babyForm.value); ElMessage.success('保存成功') } finally { savingBaby.value = false } }
async function toggleUserStatus(row) { await updateUserStatus(row.id, row.status ? 0 : 1); row.status = row.status ? 0 : 1; ElMessage.success('状态更新成功') }
async function handleAddMotto() { if (!newMotto.value.content) return ElMessage.warning('请输入内容'); const res = await addMotto(newMotto.value.content, newMotto.value.author); mottos.value.unshift(res.data); newMotto.value = { content:'', author:'' }; ElMessage.success('添加成功') }
async function handleDeleteMotto(id) { await ElMessageBox.confirm('确定删除这条短句吗？','',{type:'warning'}); await deleteMotto(id); mottos.value = mottos.value.filter(m => m.id !== id); ElMessage.success('已删除') }
async function toggleMotto(row) { await updateMottoStatus(row.id, row.isActive ? 0 : 1); row.isActive = row.isActive ? 0 : 1 }
async function saveConfig(key, value) { await updateConfig(key, value); ElMessage.success(`${configLabels[key]} 已保存`) }
async function handleTestEmail() { if (!testEmailTo.value) return ElMessage.warning('请输入收件邮箱'); await testEmail(testEmailTo.value); ElMessage.success('测试邮件发送成功 📧') }
</script>

<style scoped>
.page-title { font-size: 22px; margin-bottom: 24px; }
</style>
