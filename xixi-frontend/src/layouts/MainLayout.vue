<template>
  <div class="main-layout">
    <header class="navbar">
      <div class="navbar-inner">
        <router-link to="/" class="navbar-logo"><span>🌸</span><span class="logo-text">汐汐的小窝</span></router-link>
        <nav class="navbar-menu">
          <router-link to="/" class="nav-item">🏠 首页</router-link>
          <router-link to="/photos" class="nav-item">📷 照片墙</router-link>
          <router-link to="/videos" class="nav-item">🎦 视频集</router-link>
          <router-link to="/diary" class="nav-item">📝 成长日记</router-link>
          <router-link v-if="userStore.isAdmin" to="/admin" class="nav-item admin">⚙️ 管理后台</router-link>
        </nav>
        <div class="navbar-user">
          <el-dropdown @command="handleCommand">
            <div class="user-avatar">
              <el-avatar :size="36" :src="userStore.userInfo?.avatarUrl" :icon="UserFilled" />
              <span class="user-name">{{ userStore.userInfo?.nickname || '家人' }}</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">👤 个人中心</el-dropdown-item>
                <el-dropdown-item command="logout" divided>🚶 退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </header>
    <nav class="bottom-nav">
      <router-link to="/" class="bottom-nav-item"><span class="icon">🏠</span><span>首页</span></router-link>
      <router-link to="/photos" class="bottom-nav-item"><span class="icon">📷</span><span>照片</span></router-link>
      <router-link to="/videos" class="bottom-nav-item"><span class="icon">🎦</span><span>视频</span></router-link>
      <router-link to="/diary" class="bottom-nav-item"><span class="icon">📝</span><span>日记</span></router-link>
      <router-link to="/profile" class="bottom-nav-item"><span class="icon">👤</span><span>我的</span></router-link>
    </nav>
    <main class="main-content">
      <router-view v-slot="{ Component, route }">
        <transition name="slide" mode="out-in">
          <component :is="Component" :key="route.path" />
        </transition>
      </router-view>
    </main>
    <FloatingAI />
  </div>
</template>
<script setup>
import { UserFilled } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { authApi } from '@/api'
import FloatingAI from '@/components/FloatingAI.vue'
const router = useRouter()
const userStore = useUserStore()
async function handleCommand(command) {
  if (command === 'profile') { router.push('/profile') }
  else if (command === 'logout') {
    await ElMessageBox.confirm('确定要退出登录吗？','提示',{confirmButtonText:'退出',cancelButtonText:'取消',type:'warning'})
    await authApi.logout().catch(() => {})
    userStore.logout()
    ElMessage.success('已安全退出 👋')
    router.push('/login')
  }
}
</script>
<style scoped>
.main-layout { min-height:100vh; padding-top:64px; }
.navbar { position:fixed; top:0; left:0; right:0; height:64px; background:rgba(255,255,255,0.95); backdrop-filter:blur(10px); border-bottom:1px solid var(--color-border); z-index:100; box-shadow:0 2px 12px rgba(249,168,201,0.15); }
.navbar-inner { max-width:1200px; margin:0 auto; height:100%; padding:0 24px; display:flex; align-items:center; gap:32px; }
.navbar-logo { display:flex; align-items:center; gap:8px; text-decoration:none; font-size:20px; font-weight:700; color:#E91E7A; flex-shrink:0; }
.navbar-logo span:first-child { font-size:24px; }
.navbar-menu { display:flex; align-items:center; gap:4px; flex:1; }
.nav-item { padding:6px 14px; border-radius:20px; text-decoration:none; font-size:14px; color:var(--color-text-sub); transition:all 0.2s; white-space:nowrap; }
.nav-item:hover, .nav-item.router-link-active { background:var(--color-primary-light); color:#E91E7A; }
.nav-item.admin { color:#e6a23c; }
.navbar-user { margin-left:auto; }
.user-avatar { display:flex; align-items:center; gap:8px; cursor:pointer; }
.user-name { font-size:14px; }
.bottom-nav { display:none; position:fixed; bottom:0; left:0; right:0; height:60px; background:white; border-top:1px solid var(--color-border); z-index:100; justify-content:space-around; align-items:center; }
.bottom-nav-item { display:flex; flex-direction:column; align-items:center; gap:2px; text-decoration:none; color:var(--color-text-sub); font-size:11px; flex:1; padding:8px 0; }
.bottom-nav-item .icon { font-size:20px; }
.bottom-nav-item.router-link-active { color:#E91E7A; }
.main-content { max-width:1200px; margin:0 auto; padding:24px; min-height:calc(100vh - 64px); }
@media(max-width:768px) { .navbar-menu{display:none} .bottom-nav{display:flex} .main-layout{padding-bottom:60px} .main-content{padding:16px} .logo-text{display:none} }
</style>
