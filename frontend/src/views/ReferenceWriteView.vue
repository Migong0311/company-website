<template>
  <div class="page-wrapper">
    <div class="page-hero">
      <div class="page-hero-overlay"></div>
      <div class="page-hero-content">
        <p class="page-hero-subtitle">Resources</p>
        <h1 class="page-hero-title">자료 업로드</h1>
        <div class="page-hero-bar"></div>
      </div>
    </div>

    <div class="page-container">
      <div class="form-card">
        <div class="form-group">
          <label class="form-label">카테고리 <span class="required">*</span></label>
          <select v-model="form.categoryId" class="form-select">
            <option value="" disabled>카테고리를 선택하세요</option>
            <option v-for="cat in categories" :key="cat.id" :value="cat.id">{{ cat.name }}</option>
          </select>
        </div>

        <div class="form-group">
          <label class="form-label">제목 <span class="required">*</span></label>
          <input v-model="form.title" type="text" class="form-input" placeholder="자료 제목을 입력하세요" />
        </div>

        <div class="form-group">
          <label class="form-label">설명 <span class="optional">(선택)</span></label>
          <textarea v-model="form.description" class="form-textarea" placeholder="자료에 대한 설명을 입력하세요" rows="4"></textarea>
        </div>

        <div class="form-group">
          <label class="form-label">첨부파일 <span class="required">*</span> <span class="hint">50MB 이하</span></label>
          <input type="file" ref="fileInput" class="form-file" @change="handleFileChange" />
          <p v-if="fileError" class="field-error">{{ fileError }}</p>
        </div>

        <div class="form-group">
          <label class="form-label">썸네일 이미지 <span class="optional">(선택, 목록 대표 이미지)</span></label>
          <input type="file" ref="thumbInput" accept="image/*" class="form-file" @change="handleThumbChange" />
          <p v-if="thumbError" class="field-error">{{ thumbError }}</p>
        </div>

        <div class="form-group">
          <label class="form-label">갤러리 이미지 <span class="optional">(선택, 최대 3장, 각 50MB 이하)</span></label>
          <div class="image-inputs">
            <div v-for="i in 3" :key="i" class="image-input-row">
              <span class="image-label">이미지 {{ i }}</span>
              <input type="file" :ref="el => imageRefs[i-1] = el" accept="image/*" class="form-file" @change="handleImageChange(i-1, $event)" />
            </div>
          </div>
          <p v-if="imageError" class="field-error">{{ imageError }}</p>
          <p class="field-hint"><i class="fas fa-info-circle"></i> 상세 페이지에서 표시될 관련 사진 (JPG, PNG, GIF, WebP)</p>
        </div>

        <div class="form-actions">
          <router-link to="/references" class="btn-outline">
            <i class="fas fa-arrow-left"></i> 취소
          </router-link>
          <button class="btn-primary" @click="handleSubmit" :disabled="isSubmitting">
            <i class="fas fa-upload"></i> {{ isSubmitting ? '업로드 중...' : '업로드' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { storeToRefs } from 'pinia'
import { useReferenceStore } from '@/stores/reference'
import { useAuthStore } from '@/stores/auth'
import Swal from 'sweetalert2'

const MAX_FILE_SIZE = 50 * 1024 * 1024 // 50MB

const router = useRouter()
const refStore = useReferenceStore()
const authStore = useAuthStore()
const { categories } = storeToRefs(refStore)

const form = ref({
  categoryId: '',
  title: '',
  description: ''
})

const fileInput = ref(null)
const thumbInput = ref(null)
const imageRefs = ref([])

const fileError = ref('')
const thumbError = ref('')
const imageError = ref('')
const isSubmitting = ref(false)

onMounted(async () => {
  await authStore.checkLogin()
  if (!authStore.isLoggedIn) {
    router.push('/references')
    return
  }
  await refStore.fetchCategories()
  if (categories.value.length > 0) {
    form.value.categoryId = categories.value[0].id
  }
})

function validateFileSize(file, label) {
  if (file && file.size > MAX_FILE_SIZE) {
    return `${label} "${file.name}"의 용량이 50MB를 초과합니다. 50MB 이하의 파일만 첨부 가능합니다.`
  }
  return ''
}

function handleFileChange(e) {
  const file = e.target.files[0]
  if (!file) { fileError.value = ''; return }
  const err = validateFileSize(file, '첨부파일')
  fileError.value = err
  if (err) { e.target.value = ''; Swal.fire({ icon: 'warning', title: '용량 초과', text: err }) }
}

function handleThumbChange(e) {
  const file = e.target.files[0]
  if (!file) { thumbError.value = ''; return }
  if (!file.type.startsWith('image/')) {
    thumbError.value = '이미지 파일만 가능합니다.'
    e.target.value = ''
    return
  }
  const err = validateFileSize(file, '썸네일')
  thumbError.value = err
  if (err) { e.target.value = ''; Swal.fire({ icon: 'warning', title: '용량 초과', text: err }) }
}

function handleImageChange(idx, e) {
  const file = e.target.files[0]
  if (!file) { imageError.value = ''; return }
  if (!file.type.startsWith('image/')) {
    imageError.value = `이미지 ${idx + 1}번은 이미지 파일만 가능합니다.`
    e.target.value = ''
    return
  }
  const err = validateFileSize(file, `갤러리 이미지`)
  imageError.value = err
  if (err) { e.target.value = ''; Swal.fire({ icon: 'warning', title: '용량 초과', text: err }) }
}

async function handleSubmit() {
  // 유효성 검사
  if (!form.value.categoryId) {
    Swal.fire({ icon: 'warning', title: '입력 필요', text: '카테고리를 선택해주세요.' })
    return
  }
  if (!form.value.title.trim()) {
    Swal.fire({ icon: 'warning', title: '입력 필요', text: '제목을 입력해주세요.' })
    return
  }

  const file = fileInput.value?.files[0]
  if (!file) {
    Swal.fire({ icon: 'warning', title: '입력 필요', text: '첨부파일을 선택해주세요.' })
    return
  }
  if (file.size > MAX_FILE_SIZE) {
    Swal.fire({ icon: 'warning', title: '용량 초과', text: '첨부파일이 50MB를 초과합니다.' })
    return
  }

  const thumbnail = thumbInput.value?.files[0]
  if (thumbnail && !thumbnail.type.startsWith('image/')) {
    Swal.fire({ icon: 'warning', title: '형식 오류', text: '썸네일은 이미지 파일만 가능합니다.' })
    return
  }
  if (thumbnail && thumbnail.size > MAX_FILE_SIZE) {
    Swal.fire({ icon: 'warning', title: '용량 초과', text: '썸네일이 50MB를 초과합니다.' })
    return
  }

  // 갤러리 이미지 수집
  const images = []
  for (let i = 0; i < 3; i++) {
    const imgFile = imageRefs.value[i]?.files[0]
    if (imgFile) {
      if (!imgFile.type.startsWith('image/')) {
        Swal.fire({ icon: 'warning', title: '형식 오류', text: `이미지 ${i+1}번은 이미지 파일만 가능합니다.` })
        return
      }
      if (imgFile.size > MAX_FILE_SIZE) {
        Swal.fire({ icon: 'warning', title: '용량 초과', text: `갤러리 이미지 "${imgFile.name}"이 50MB를 초과합니다.` })
        return
      }
      images.push(imgFile)
    }
  }

  // FormData 구성
  const formData = new FormData()
  formData.append('categoryId', form.value.categoryId)
  formData.append('title', form.value.title.trim())
  if (form.value.description.trim()) formData.append('description', form.value.description.trim())
  formData.append('file', file)
  if (thumbnail) formData.append('thumbnail', thumbnail)
  for (const img of images) {
    formData.append('images', img)
  }

  isSubmitting.value = true
  try {
    await refStore.createReference(formData)
    Swal.fire({ icon: 'success', title: '업로드 완료', text: '자료가 등록되었습니다.', timer: 1500, showConfirmButton: false })
    router.push('/references')
  } catch {
    Swal.fire({ icon: 'error', title: '업로드 실패', text: '파일 업로드에 실패했습니다.' })
  } finally {
    isSubmitting.value = false
  }
}
</script>

<style scoped>
.page-wrapper {
  min-height: 100vh;
  background: var(--bg-light);
}

.page-hero {
  position: relative;
  height: 260px;
  background: linear-gradient(135deg, var(--primary) 0%, var(--primary-dark) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.page-hero-overlay {
  position: absolute;
  inset: 0;
  background: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.05'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
}

.page-hero-content {
  position: relative;
  text-align: center;
  color: var(--white);
}

.page-hero-subtitle {
  font-size: 0.9rem;
  letter-spacing: 3px;
  text-transform: uppercase;
  opacity: 0.8;
  margin-bottom: 12px;
}

.page-hero-title {
  font-size: 2.2rem;
  font-weight: 700;
}

.page-hero-bar {
  width: 50px;
  height: 3px;
  background: var(--accent);
  margin: 16px auto 0;
  border-radius: 2px;
}

.page-container {
  max-width: 720px;
  margin: 0 auto;
  padding: 40px 24px 80px;
}

.form-card {
  background: var(--white);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-sm);
  padding: 36px 32px;
}

.form-group {
  margin-bottom: 24px;
}

.form-label {
  display: block;
  font-size: 0.9rem;
  font-weight: 600;
  color: var(--text-dark);
  margin-bottom: 8px;
}

.required {
  color: #e74c3c;
}

.optional {
  color: var(--text-muted);
  font-weight: 400;
  font-size: 0.82rem;
}

.hint {
  color: var(--accent);
  font-weight: 400;
  font-size: 0.82rem;
}

.form-input,
.form-select,
.form-textarea {
  width: 100%;
  padding: 10px 14px;
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  font-size: 0.9rem;
  font-family: inherit;
  outline: none;
  transition: border-color 0.2s;
  box-sizing: border-box;
}

.form-input:focus,
.form-select:focus,
.form-textarea:focus {
  border-color: var(--primary);
}

.form-textarea {
  resize: vertical;
  min-height: 100px;
}

.form-file {
  display: block;
  font-size: 0.85rem;
  color: var(--text-body);
}

.field-error {
  margin-top: 6px;
  font-size: 0.82rem;
  color: #e74c3c;
}

.field-hint {
  margin-top: 8px;
  font-size: 0.78rem;
  color: var(--text-muted);
}

.field-hint i {
  color: var(--accent);
}

.image-inputs {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.image-input-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.image-label {
  min-width: 60px;
  font-size: 0.85rem;
  font-weight: 500;
  color: var(--text-body);
}

.form-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid var(--border);
}

.btn-primary {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 32px;
  background: var(--primary);
  color: var(--white);
  border: none;
  border-radius: var(--radius-sm);
  font-size: 0.95rem;
  font-weight: 500;
  cursor: pointer;
  transition: var(--transition);
}

.btn-primary:hover:not(:disabled) {
  background: var(--primary-light);
  transform: translateY(-1px);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-outline {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  background: var(--white);
  color: var(--text-body);
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  font-size: 0.9rem;
  font-weight: 500;
  text-decoration: none;
  cursor: pointer;
  transition: var(--transition);
}

.btn-outline:hover {
  border-color: var(--primary);
  color: var(--primary);
}

@media (max-width: 768px) {
  .page-hero { height: 200px; }
  .page-hero-title { font-size: 1.6rem; }
  .form-card { padding: 24px 20px; }
  .image-input-row { flex-direction: column; align-items: flex-start; gap: 4px; }
}
</style>
