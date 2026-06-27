<template>
  <div class="page-container">
    <div class="page-header"><h1>🎬 视频集</h1><el-button type="primary" @click="uploadVisible = true">上传视频</el-button></div>
    <div class="video-grid" v-if="videos.length">
      <div v-for="video in videos" :key="video.id" class="video-card xixi-card">
        <div class="video-cover" @click="playVideo(video)">
          <el-image v-if="video.coverUrl" :src="video.coverUrl" fit="cover" />
          <div v-else class="cover-placeholder">🎬</div>
          <div class="play-btn">▶</div>
        </div>
        <div class="video-info">
          <div class="video-title">{{ video.title }}</div>
          <div class="video-meta"><span>{{ video.takenAt || formatDate(video.createdAt) }}</span><span>{{ video.uploaderNickname }}</span></div>
          <div class="video-actions" v-if="canDelete(video)"><el-button size="small" text type="danger" @click="handleDelete(video.id)">删除</el-button></div>
        </div>
      </div>
    </div>
    <el-empty v-else description="还没有视频，快来上传第一个吧 🎬" />
    <el-dialog v-model="playerVisible" :title="currentVideo?.title" width="760px" destroy-on-close>
      <video v-if="playerVisible && playUrl" :src="playUrl" controls style="width:100%;border-radius:8px" autoplay />
    </el-dialog>
    <el-dialog v-model="uploadVisible" title="上传视频" width="500px">
      <el-form :model="uploadForm" label-width="80px">
        <el-form-item label="视频"><el-upload :auto-upload="false" :on-change="f => videoFile = f.raw" accept="video/*" :limit="1"><el-button>选择视频文件</el-button></el-upload></el-form-item>
        <el-form-item label="封面图"><el-upload :auto-upload="false" :on-change="f => coverFile = f.raw" accept="image/*" :limit="1" list-type="picture-card"><el-icon><Plus /></el-icon></el-upload></el-form-item>
        <el-form-item label="标题" required><el-input v-model="uploadForm.title" placeholder="给视频起个名字" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="uploadForm.description" type="textarea" /></el-form-item>
        <el-form-item label="拍摄日期"><el-date-picker v-model="uploadForm.takenAt" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="uploadVisible = false">取消</el-button>
        <el-button type="primary" :loading="uploading" @click="handleUpload">上传</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { listVideos, uploadVideo, deleteVideo, getPlayUrl } from '@/api/video'
import { ElMessage, ElMessageBox } from 'element-plus'

const userStore = useUserStore()
const videos = ref([])
const uploadVisible = ref(false)
const uploading = ref(false)
const videoFile = ref(null)
const coverFile = ref(null)
const playerVisible = ref(false)
const currentVideo = ref(null)
const playUrl = ref('')
const uploadForm = ref({ title: '', description: '', takenAt: '' })
const isAdmin = computed(() => userStore.isAdmin)
function canDelete(v) { return isAdmin.value || v.uploaderId === userStore.userInfo?.userId }
function formatDate(dt) { return dt ? dt.substring(0, 10) : '' }
async function loadVideos() { const res = await listVideos(); videos.value = res.data.list }
async function playVideo(video) { currentVideo.value = video; const res = await getPlayUrl(video.id); playUrl.value = res.data.playUrl; playerVisible.value = true }
async function handleUpload() {
  if (!videoFile.value) return ElMessage.warning('请选择视频文件')
  if (!uploadForm.value.title) return ElMessage.warning('请输入标题')
  const fd = new FormData(); fd.append('file', videoFile.value)
  if (coverFile.value) fd.append('cover', coverFile.value)
  fd.append('title', uploadForm.value.title)
  if (uploadForm.value.description) fd.append('description', uploadForm.value.description)
  if (uploadForm.value.takenAt) fd.append('takenAt', uploadForm.value.takenAt)
  uploading.value = true
  try { await uploadVideo(fd); ElMessage.success('上传成功 🎬'); uploadVisible.value = false; uploadForm.value = { title: '', description: '', takenAt: '' }; videoFile.value = null; coverFile.value = null; loadVideos() }
  finally { uploading.value = false }
}
async function handleDelete(id) { await ElMessageBox.confirm('确定删除这个视频吗？', '提示', { type: 'warning' }); await deleteVideo(id); ElMessage.success('已删除'); loadVideos() }
onMounted(loadVideos)
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.page-header h1 { font-size: 22px; }
.video-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(260px, 1fr)); gap: 20px; }
.video-card { padding: 0; overflow: hidden; }
.video-cover { position: relative; aspect-ratio: 16/9; background: #f5f5f5; cursor: pointer; display: flex; align-items: center; justify-content: center; }
.video-cover .el-image { width: 100%; height: 100%; }
.cover-placeholder { font-size: 48px; }
.play-btn { position: absolute; inset: 0; display: flex; align-items: center; justify-content: center; background: rgba(0,0,0,0.2); color: #fff; font-size: 32px; opacity: 0; transition: opacity 0.2s; }
.video-cover:hover .play-btn { opacity: 1; }
.video-info { padding: 12px 16px 14px; }
.video-title { font-size: 15px; font-weight: 500; margin-bottom: 6px; }
.video-meta { display: flex; gap: 12px; font-size: 12px; color: var(--color-text-light); }
.video-actions { margin-top: 6px; }
</style>
