<template>
  <div class="page-container">
    <div class="page-header"><h1>📷 照片墙</h1><el-button type="primary" @click="uploadVisible = true">上传照片</el-button></div>
    <div class="photo-masonry" v-if="photos.length">
      <div v-for="photo in photos" :key="photo.id" class="photo-card">
        <el-image :src="photo.thumbnailUrl || photo.url" fit="cover" :preview-src-list="photos.map(p => p.url)" lazy />
        <div class="photo-info">
          <span class="photo-title">{{ photo.title || '无标题' }}</span>
          <span class="photo-date">{{ photo.takenAt || formatDate(photo.createdAt) }}</span>
          <span class="photo-uploader">by {{ photo.uploaderNickname }}</span>
        </div>
        <div class="photo-actions" v-if="canDelete(photo)">
          <el-button size="small" text type="danger" @click="handleDelete(photo.id)">删除</el-button>
          <el-button v-if="isAdmin" size="small" text @click="handleFeatured(photo)">{{ photo.isFeatured ? '取消精选' : '设为精选' }}</el-button>
        </div>
      </div>
    </div>
    <el-empty v-else description="还没有照片，快来上传第一张吧 📷" />
    <div class="pagination" v-if="total > pageSize">
      <el-pagination v-model:current-page="currentPage" :page-size="pageSize" :total="total" layout="prev, pager, next" @current-change="loadPhotos" />
    </div>
    <el-dialog v-model="uploadVisible" title="上传照片" width="480px">
      <el-form :model="uploadForm" label-width="80px">
        <el-form-item label="照片">
          <el-upload :auto-upload="false" :on-change="handleFileChange" accept="image/*" :limit="1" list-type="picture-card"><el-icon><Plus /></el-icon></el-upload>
        </el-form-item>
        <el-form-item label="标题"><el-input v-model="uploadForm.title" placeholder="给照片起个名字" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="uploadForm.description" type="textarea" placeholder="记录这个瞬间..." /></el-form-item>
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
import { listPhotos, uploadPhoto, deletePhoto, setFeatured } from '@/api/photo'
import { ElMessage, ElMessageBox } from 'element-plus'

const userStore = useUserStore()
const photos = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = 20
const uploadVisible = ref(false)
const uploading = ref(false)
const uploadFile = ref(null)
const uploadForm = ref({ title: '', description: '', takenAt: '' })
const isAdmin = computed(() => userStore.isAdmin)
function canDelete(photo) { return isAdmin.value || photo.uploaderId === userStore.userInfo?.userId }
function formatDate(dt) { return dt ? dt.substring(0, 10) : '' }
async function loadPhotos() { const res = await listPhotos(currentPage.value, pageSize); photos.value = res.data.list; total.value = res.data.total }
function handleFileChange(file) { uploadFile.value = file.raw }
async function handleUpload() {
  if (!uploadFile.value) return ElMessage.warning('请选择照片')
  const fd = new FormData(); fd.append('file', uploadFile.value)
  if (uploadForm.value.title) fd.append('title', uploadForm.value.title)
  if (uploadForm.value.description) fd.append('description', uploadForm.value.description)
  if (uploadForm.value.takenAt) fd.append('takenAt', uploadForm.value.takenAt)
  uploading.value = true
  try { await uploadPhoto(fd); ElMessage.success('上传成功 📷'); uploadVisible.value = false; uploadForm.value = { title: '', description: '', takenAt: '' }; uploadFile.value = null; loadPhotos() }
  finally { uploading.value = false }
}
async function handleDelete(id) { await ElMessageBox.confirm('确定删除这张照片吗？', '提示', { type: 'warning' }); await deletePhoto(id); ElMessage.success('已删除'); loadPhotos() }
async function handleFeatured(photo) { await setFeatured(photo.id, !photo.isFeatured, 0); ElMessage.success(photo.isFeatured ? '已取消精选' : '已设为精选 ⭐'); loadPhotos() }
onMounted(loadPhotos)
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.page-header h1 { font-size: 22px; }
.photo-masonry { columns: 4; column-gap: 16px; margin-bottom: 24px; }
@media (max-width: 900px) { .photo-masonry { columns: 3; } }
@media (max-width: 600px) { .photo-masonry { columns: 2; } }
.photo-card { break-inside: avoid; background: #fff; border-radius: 12px; box-shadow: var(--shadow-card); overflow: hidden; margin-bottom: 16px; }
.photo-card .el-image { width: 100%; display: block; }
.photo-info { padding: 10px 12px 4px; }
.photo-title { display: block; font-size: 13px; font-weight: 500; }
.photo-date, .photo-uploader { display: block; font-size: 11px; color: var(--color-text-light); margin-top: 2px; }
.photo-actions { padding: 0 8px 8px; display: flex; gap: 4px; }
.pagination { display: flex; justify-content: center; margin-top: 24px; }
</style>
