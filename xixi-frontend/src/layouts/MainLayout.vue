<template>
  <el-container class="main-layout">
    <el-header class="top-header" height="64px">
      <div class="header-inner">
        <div class="logo" @click="$router.push('/')">
          🌸 汐汐的小窝
        </div>
        <el-menu mode="horizontal" :default-active="activeMenu" class="nav-menu"
          :ellipsis="false" @select="handleMenuSelect">
          <el-menu-item index="/">🏠 首页</el-menu-item>
          <el-menu-item index="/photos">📷 照片墙</el-menu-item>
          <el-menu-item index="/videos">🎬 视频集</el-menu-item>
          <el-menu-item index="/diary">📝 成长日记</el-menu-item>
          <el-menu-item v-if="userStore.isAdmin" index="/admin">⚙️ 后台管理</el-menu-item>
        </el-menu>
        <div class="header-right">
          <el-dropdown trigger="click">
            <div class="user-avatar">
              <el-avatar :size="36" :src="userStore.userInfo?.avatarUrl">
                {{ userStore.userInfo?.nickname?.charAt(0) || '✨' }}
              </el-avatar>
              <span class="nickname">{{ userStore.userInfo?.nickname }}</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="$router.push('/profile')">👤 个人信息</el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">🚪 退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </el-header>
    <el-main class="main-content">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </el-main>
    <FloatingAI />
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import FloatingAI from '@/components/FloatingAI.vue'
import { ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => '/' + route.path.split('/')[1])

function handleMenuSelect(index) { router.push(index) }

async function handleLogout() {
  await ElMessageBox.confirm('确定要退出登录吗？', '退出登录', {
    confirmButtonText: '退出', cancelButtonText: '取消', type: 'warning'
  })
  await userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.main-layout { min-height: 100vh; background: var(--color-bg); }
.top-header {
  background: #fff;
  box-shadow: 0 2px 12px rgba(249, 168, 201, 0.15);
  position: sticky; top: 0; z-index: 100; padding: 0;
}
.header-inner {
  max-width: 1200px; margin: 0 auto; height: 100%;
  display: flex; align-items: center; padding: 0 16px; gap: 24px;
}
.logo { font-size: 20px; font-weight: 700; color: var(--color-primary-dark); cursor: pointer; white-space: nowrap; letter-spacing: 1px; }
.nav-menu { flex: 1; border-bottom: none !important; --el-menu-active-color: var(--color-primary-dark); --el-menu-hover-text-color: var(--color-primary-dark); }
.header-right { margin-left: auto; }
.user-avatar { display: flex; align-items: center; gap: 8px; cursor: pointer; padding: 4px 8px; border-radius: 20px; transition: background 0.2s; }
.user-avatar:hover { background: var(--color-primary-light); }
.nickname { font-size: 14px; color: var(--color-text); }
.main-content { padding: 0; min-height: calc(100vh - 64px); }
</style>
