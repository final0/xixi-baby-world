<template>
  <div class="page-container">
    <div class="page-header"><h1>{{ isEdit ? '编辑日记' : '写日记 📝' }}</h1></div>
    <div class="edit-form xixi-card">
      <el-form :model="form" label-position="top">
        <el-form-item label="标题" required><el-input v-model="form.title" placeholder="给今天的日记起个标题..." size="large" /></el-form-item>
        <div class="form-row">
          <el-form-item label="日期" required style="flex:1"><el-date-picker v-model="form.diaryDate" type="date" value-format="YYYY-MM-DD" style="width:100%" size="large" /></el-form-item>
          <el-form-item label="今天的心情" style="flex:1">
            <el-select v-model="form.mood" placeholder="选择心情" clearable size="large" style="width:100%">
              <el-option label="😊 开心" value="happy" /><el-option label="🎉 兴奋" value="excited" />
              <el-option label="😌 平静" value="peaceful" /><el-option label="😴 疲惫" value="tired" />
            </el-select>
          </el-form-item>
        </div>
        <el-form-item label="正文" required><div ref="editorRef" class="editor-container"></div></el-form-item>
        <div class="form-actions">
          <el-button @click="$router.back()">取消</el-button>
          <el-button type="primary" :loading="loading" @click="handleSubmit">{{ isEdit ? '保存修改' : '发布日记' }}</el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { createDiary, updateDiary, getDiary } from '@/api/diary'
import { ElMessage } from 'element-plus'

const route = useRoute(); const router = useRouter()
const editorRef = ref(null); const loading = ref(false)
let quillInstance = null
const isEdit = computed(() => !!route.params.id && route.path.includes('edit'))
const form = ref({ title: '', diaryDate: new Date().toISOString().split('T')[0], mood: '' })

onMounted(async () => {
  const Quill = (await import('quill')).default
  await import('quill/dist/quill.snow.css')
  quillInstance = new Quill(editorRef.value, {
    theme: 'snow', placeholder: '记录汐汐的美好时光...',
    modules: { toolbar: [[{ header: [2, 3, false] }],['bold','italic','underline'],[{ color: [] }],[{ list: 'ordered' },{ list: 'bullet' }],['blockquote'],['clean']] }
  })
  if (isEdit.value) {
    const res = await getDiary(route.params.id)
    const d = res.data; form.value = { title: d.title, diaryDate: d.diaryDate, mood: d.mood || '' }
    quillInstance.root.innerHTML = d.content
  }
})

async function handleSubmit() {
  if (!form.value.title) return ElMessage.warning('请输入标题')
  if (!form.value.diaryDate) return ElMessage.warning('请选择日期')
  const content = quillInstance?.root.innerHTML || ''
  if (!content || content === '<p><br></p>') return ElMessage.warning('请输入日记内容')
  loading.value = true
  try {
    const payload = { ...form.value, content }
    if (isEdit.value) { await updateDiary(route.params.id, payload); ElMessage.success('修改成功'); router.push(`/diary/${route.params.id}`) }
    else { const res = await createDiary(payload); ElMessage.success('日记发布成功 📝'); router.push(`/diary/${res.data.id}`) }
  } finally { loading.value = false }
}
</script>

<style scoped>
.page-header { margin-bottom: 20px; }
.page-header h1 { font-size: 22px; }
.edit-form { padding: 32px; }
.form-row { display: flex; gap: 20px; }
.editor-container { width: 100%; min-height: 300px; }
.form-actions { display: flex; justify-content: flex-end; gap: 12px; margin-top: 8px; }
</style>
