<template>
  <div class="page-container" v-if="diary">
    <div class="detail-header">
      <el-button text @click="$router.back()">← 返回</el-button>
      <div class="detail-actions">
        <el-button v-if="canEdit" @click="$router.push(`/diary/${diary.id}/edit`)">编辑</el-button>
        <el-button v-if="canEdit" type="danger" plain @click="handleDelete">删除</el-button>
      </div>
    </div>
    <div class="diary-detail xixi-card">
      <div class="detail-meta">
        <span class="mood-tag" v-if="diary.mood">{{ moodEmoji[diary.mood] }} {{ moodLabel[diary.mood] }}</span>
        <span class="detail-date">{{ diary.diaryDate }}</span>
      </div>
      <h1 class="detail-title">{{ diary.title }}</h1>
      <div class="detail-author">
        <el-avatar :size="32" :src="diary.authorAvatarUrl">{{ diary.authorNickname?.charAt(0) }}</el-avatar>
        <span>{{ diary.authorNickname }}</span>
      </div>
      <div class="detail-content" v-html="diary.content"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getDiary, deleteDiary } from '@/api/diary'
import { ElMessageBox, ElMessage } from 'element-plus'

const route = useRoute(); const router = useRouter(); const userStore = useUserStore()
const diary = ref(null)
const moodEmoji = { happy: '😊', excited: '🎉', peaceful: '😌', tired: '😴' }
const moodLabel = { happy: '开心', excited: '兴奋', peaceful: '平静', tired: '疲惫' }
const canEdit = computed(() => { if (!diary.value) return false; return userStore.isAdmin || diary.value.authorId === userStore.userInfo?.userId })
onMounted(async () => { const res = await getDiary(route.params.id); diary.value = res.data })
async function handleDelete() {
  await ElMessageBox.confirm('确定删除这篇日记吗？', '提示', { type: 'warning' })
  await deleteDiary(diary.value.id); ElMessage.success('已删除'); router.push('/diary')
}
</script>

<style scoped>
.detail-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.detail-actions { display: flex; gap: 8px; }
.diary-detail { padding: 32px; }
.detail-meta { display: flex; gap: 12px; margin-bottom: 12px; }
.mood-tag { background: var(--color-primary-light); color: var(--color-primary-dark); padding: 4px 12px; border-radius: 20px; font-size: 13px; }
.detail-date { color: var(--color-text-light); font-size: 14px; line-height: 26px; }
.detail-title { font-size: 26px; font-weight: 700; margin-bottom: 16px; }
.detail-author { display: flex; align-items: center; gap: 8px; font-size: 14px; color: var(--color-text-light); margin-bottom: 24px; padding-bottom: 24px; border-bottom: 1px solid var(--color-border); }
.detail-content { line-height: 1.8; font-size: 15px; }
</style>
