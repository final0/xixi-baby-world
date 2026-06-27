<template>
  <div class="home-page page-container">
    <div class="welcome-banner" v-if="info.baby">
      <div class="baby-avatar">
        <el-avatar :size="80" :src="info.baby.avatarUrl">🌸</el-avatar>
      </div>
      <div class="baby-info">
        <h1>{{ info.baby.name }} 的成长空间 🌸</h1>
        <div class="baby-stats">
          <span>🎂 生日：{{ info.baby.birthday }}</span>
          <span>👶 年龄：{{ info.baby.age }}</span>
          <span v-if="info.baby.birthWeight">⚖️ 出生体重：{{ info.baby.birthWeight }}kg</span>
          <span v-if="info.baby.birthHeight">📏 出生身高：{{ info.baby.birthHeight }}cm</span>
        </div>
        <p v-if="info.baby.intro" class="baby-intro">{{ info.baby.intro }}</p>
      </div>
      <div class="user-welcome">
        <el-avatar :size="48" :src="userStore.userInfo?.avatarUrl">{{ userStore.userInfo?.nickname?.charAt(0) }}</el-avatar>
        <div>
          <div class="user-greeting">欢迎回来，{{ userStore.userInfo?.nickname }}！</div>
          <div class="user-motto">{{ userStore.userInfo?.roleMotto }}</div>
        </div>
      </div>
    </div>

    <div class="motto-section" v-if="info.mottos?.length">
      <div class="motto-scroll">
        <TransitionGroup name="motto">
          <span class="motto-item" :key="currentMotto">
            💕 {{ info.mottos[currentMotto]?.content }}
            <small v-if="info.mottos[currentMotto]?.author"> —— {{ info.mottos[currentMotto].author }}</small>
          </span>
        </TransitionGroup>
      </div>
    </div>

    <div class="section" v-if="info.featuredPhotos?.length">
      <div class="section-header">
        <h2>📷 精选回忆</h2>
        <router-link to="/photos" class="section-more">查看全部 →</router-link>
      </div>
      <div class="photo-grid">
        <div v-for="photo in info.featuredPhotos" :key="photo.id" class="photo-item">
          <el-image :src="photo.thumbnailUrl || photo.url" fit="cover"
            :preview-src-list="[photo.url]" lazy />
          <div v-if="photo.title" class="photo-title">{{ photo.title }}</div>
        </div>
      </div>
    </div>

    <div class="section">
      <h2>✨ 快捷入口</h2>
      <div class="quick-links">
        <router-link to="/photos" class="quick-card"><span>📷</span><span>照片墙</span></router-link>
        <router-link to="/videos" class="quick-card"><span>🎬</span><span>视频集</span></router-link>
        <router-link to="/diary" class="quick-card"><span>📝</span><span>成长日记</span></router-link>
        <router-link to="/profile" class="quick-card"><span>👤</span><span>个人中心</span></router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { getHomeInfo } from '@/api/home'

const userStore = useUserStore()
const info = ref({ baby: null, featuredPhotos: [], mottos: [] })
const currentMotto = ref(0)
let mottoTimer = null

onMounted(async () => {
  const res = await getHomeInfo()
  info.value = res.data
  if (info.value.mottos?.length > 1) {
    mottoTimer = setInterval(() => {
      currentMotto.value = (currentMotto.value + 1) % info.value.mottos.length
    }, 4000)
  }
})
onUnmounted(() => { if (mottoTimer) clearInterval(mottoTimer) })
</script>

<style scoped>
.home-page { padding-bottom: 40px; }
.welcome-banner { background: linear-gradient(135deg, #fff0f7, #fce4ec); border-radius: var(--radius-card); padding: 32px; margin-bottom: 24px; display: flex; align-items: center; gap: 24px; flex-wrap: wrap; }
.baby-info { flex: 1; }
.baby-info h1 { font-size: 22px; color: var(--color-primary-dark); margin-bottom: 10px; }
.baby-stats { display: flex; flex-wrap: wrap; gap: 12px; font-size: 14px; color: var(--color-text-light); margin-bottom: 8px; }
.baby-intro { font-size: 14px; color: var(--color-text-light); }
.user-welcome { display: flex; align-items: center; gap: 12px; margin-left: auto; }
.user-greeting { font-size: 15px; font-weight: 600; color: var(--color-primary-dark); }
.user-motto { font-size: 13px; color: var(--color-text-light); margin-top: 2px; }
.motto-section { background: #fff; border-radius: var(--radius-card); padding: 16px 24px; margin-bottom: 24px; box-shadow: var(--shadow-card); min-height: 52px; display: flex; align-items: center; }
.motto-scroll { width: 100%; }
.motto-item { font-size: 15px; color: var(--color-primary-dark); display: block; }
.motto-item small { color: var(--color-text-light); font-size: 12px; }
.motto-enter-active, .motto-leave-active { transition: all 0.5s ease; }
.motto-enter-from { opacity: 0; transform: translateY(10px); }
.motto-leave-to { opacity: 0; transform: translateY(-10px); }
.section { margin-bottom: 32px; }
.section-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; }
.section-header h2, .section h2 { font-size: 18px; color: var(--color-text); margin-bottom: 16px; }
.section-more { color: var(--color-primary-dark); font-size: 14px; text-decoration: none; }
.photo-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(160px, 1fr)); gap: 12px; }
.photo-item { border-radius: 12px; overflow: hidden; box-shadow: var(--shadow-card); position: relative; aspect-ratio: 1; }
.photo-item .el-image { width: 100%; height: 100%; }
.photo-title { position: absolute; bottom: 0; left: 0; right: 0; background: linear-gradient(transparent, rgba(0,0,0,0.5)); color: #fff; font-size: 12px; padding: 8px; }
.quick-links { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; }
.quick-card { display: flex; flex-direction: column; align-items: center; gap: 8px; padding: 24px 16px; background: #fff; border-radius: var(--radius-card); box-shadow: var(--shadow-card); text-decoration: none; color: var(--color-text); transition: all 0.2s; font-size: 13px; }
.quick-card span:first-child { font-size: 32px; }
.quick-card:hover { transform: translateY(-4px); box-shadow: 0 8px 24px rgba(249,168,201,0.3); color: var(--color-primary-dark); }
</style>
