<template>
  <div class="page-container">
    <div class="page-header"><h1>📝 成长日记</h1><el-button type="primary" @click="$router.push('/diary/create')">写日记</el-button></div>
    <div class="filter-bar">
      <el-date-picker v-model="filterMonth" type="month" value-format="YYYY-MM" placeholder="按月份筛选" clearable @change="loadDiaries" style="width:180px" />
    </div>
    <div class="diary-list" v-if="diaries.length">
      <div v-for="diary in diaries" :key="diary.id" class="diary-card xixi-card" @click="$router.push(`/diary/${diary.id}`)">
        <div class="diary-header">
          <span class="diary-mood" v-if="diary.mood">{{ moodEmoji[diary.mood] }}</span>
          <h3 class="diary-title">{{ diary.title }}</h3>
          <span class="diary-date">{{ diary.diaryDate }}</span>
        </div>
        <p class="diary-preview">{{ stripHtml(diary.content) }}</p>
        <div class="diary-footer">
          <el-avatar :size="24" :src="diary.authorAvatarUrl">{{ diary.authorNickname?.charAt(0) }}</el-avatar>
          <span class="diary-author">{{ diary.authorNickname }}</span>
        </div>
      </div>
    </div>
    <el-empty v-else description="还没有日记，快来记录第一篇吧 📝" />
    <div class="pagination" v-if="total > pageSize"><el-pagination v-model:current-page="currentPage" :page-size="pageSize" :total="total" layout="prev, pager, next" @current-change="loadDiaries" /></div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { listDiaries } from '@/api/diary'

const diaries = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = 10
const filterMonth = ref('')
const moodEmoji = { happy: '😊', excited: '🎉', peaceful: '😌', tired: '😴' }
function stripHtml(html) { const d = document.createElement('div'); d.innerHTML = html; const t = d.textContent || ''; return t.length > 120 ? t.substring(0, 120) + '...' : t }
async function loadDiaries() { const res = await listDiaries(currentPage.value, pageSize, filterMonth.value || undefined); diaries.value = res.data.list; total.value = res.data.total }
onMounted(loadDiaries)
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-header h1 { font-size: 22px; }
.filter-bar { margin-bottom: 20px; }
.diary-list { display: flex; flex-direction: column; gap: 16px; }
.diary-card { cursor: pointer; transition: transform 0.2s; }
.diary-card:hover { transform: translateY(-2px); }
.diary-header { display: flex; align-items: center; gap: 10px; margin-bottom: 10px; }
.diary-mood { font-size: 20px; }
.diary-title { flex: 1; font-size: 17px; font-weight: 600; }
.diary-date { font-size: 13px; color: var(--color-text-light); }
.diary-preview { font-size: 14px; color: var(--color-text-light); line-height: 1.6; margin-bottom: 12px; }
.diary-footer { display: flex; align-items: center; gap: 8px; }
.diary-author { font-size: 13px; color: var(--color-text-light); }
.pagination { display: flex; justify-content: center; margin-top: 24px; }
</style>
