<template>
  <div class="page-wrapper">
    <div class="page-hero">
      <div class="page-hero-overlay"></div>
      <div class="page-hero-content">
        <p class="page-hero-subtitle">Resources</p>
        <h1 class="page-hero-title">자료실</h1>
        <div class="page-hero-bar"></div>
      </div>
    </div>

    <div class="page-container" v-if="reference">
      <div class="detail-card">
        <!-- 상단 메타 -->
        <div class="detail-header">
          <h2 class="detail-title">{{ reference.title }}</h2>
          <div class="detail-meta">
            <span><i class="fas fa-folder"></i> {{ reference.categoryName || '미분류' }}</span>
            <span><i class="fas fa-download"></i> 다운로드 {{ reference.downloadCount }}회</span>
            <span><i class="fas fa-calendar"></i> {{ formatDate(reference.createdAt) }}</span>
          </div>
        </div>

        <!-- 썸네일 이미지 -->
        <div v-if="reference.thumbnailPath" class="detail-thumbnail">
          <img :src="refStore.getThumbnailUrl(reference.id)" :alt="reference.title" />
        </div>

        <!-- 설명 -->
        <div v-if="reference.description" class="detail-description">
          <p>{{ reference.description }}</p>
        </div>

        <!-- 갤러리 이미지 -->
        <div v-if="reference.images && reference.images.length > 0" class="detail-gallery">
          <h3 class="section-title"><i class="fas fa-images"></i> 관련 이미지</h3>
          <div class="gallery-grid">
            <div
              v-for="img in reference.images"
              :key="img.id"
              class="gallery-item"
              @click="openImageViewer(img)"
            >
              <img :src="refStore.getImageUrl(img.id)" :alt="img.fileName" />
            </div>
          </div>
        </div>

        <!-- 첨부파일 목록 -->
        <div class="detail-files">
          <h3 class="section-title"><i class="fas fa-paperclip"></i> 첨부파일 ({{ fileCount }}개)</h3>
          <div class="file-list">
            <!-- 메인 파일 -->
            <a :href="refStore.getDownloadUrl(reference.id)" download class="file-item">
              <i :class="getFileIcon(reference.fileName)"></i>
              <span class="file-name">{{ reference.fileName }}</span>
              <i class="fas fa-download file-dl-icon"></i>
            </a>
            <!-- 추가 파일 -->
            <a
              v-for="f in reference.files"
              :key="f.id"
              :href="refStore.getFileDownloadUrl(f.id)"
              download
              class="file-item"
            >
              <i :class="getFileIcon(f.fileName)"></i>
              <span class="file-name">{{ f.fileName }}</span>
              <i class="fas fa-download file-dl-icon"></i>
            </a>
          </div>
        </div>

        <!-- 액션 버튼 -->
        <div class="detail-actions">
          <router-link to="/references" class="btn-outline">
            <i class="fas fa-list"></i> 목록
          </router-link>
          <button v-if="isAdmin" class="btn-danger" @click="handleDelete">
            <i class="fas fa-trash"></i> 삭제
          </button>
        </div>
      </div>
    </div>

    <div class="page-container" v-else>
      <div class="loading-state">
        <i class="fas fa-spinner fa-spin"></i>
        <p>자료를 불러오는 중...</p>
      </div>
    </div>

    <!-- 이미지 뷰어 오버레이 -->
    <div v-if="viewerImage" class="image-viewer" @click="closeImageViewer">
      <button class="viewer-close" @click="closeImageViewer">
        <i class="fas fa-times"></i>
      </button>
      <img :src="refStore.getImageUrl(viewerImage.id)" :alt="viewerImage.fileName" @click.stop />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { storeToRefs } from 'pinia'
import { useReferenceStore } from '@/stores/reference'
import { useAuthStore } from '@/stores/auth'
import Swal from 'sweetalert2'

const route = useRoute()
const router = useRouter()
const refStore = useReferenceStore()
const authStore = useAuthStore()
const { isLoggedIn: isAdmin } = storeToRefs(authStore)

const reference = ref(null)
const viewerImage = ref(null)

const fileCount = computed(() => {
  if (!reference.value) return 0
  return 1 + (reference.value.files ? reference.value.files.length : 0)
})

onMounted(async () => {
  await authStore.checkLogin()
  try {
    reference.value = await refStore.fetchReference(route.params.id)
  } catch {
    Swal.fire({ icon: 'error', title: '오류', text: '자료를 찾을 수 없습니다.' })
    router.push('/references')
  }
})

function formatDate(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return `${d.getFullYear()}.${String(d.getMonth() + 1).padStart(2, '0')}.${String(d.getDate()).padStart(2, '0')}`
}

function getFileIcon(fileName) {
  if (!fileName) return 'fas fa-file'
  const ext = fileName.split('.').pop().toLowerCase()
  const icons = {
    pdf: 'fas fa-file-pdf',
    doc: 'fas fa-file-word', docx: 'fas fa-file-word',
    xls: 'fas fa-file-excel', xlsx: 'fas fa-file-excel',
    ppt: 'fas fa-file-powerpoint', pptx: 'fas fa-file-powerpoint',
    zip: 'fas fa-file-archive', rar: 'fas fa-file-archive',
    jpg: 'fas fa-file-image', jpeg: 'fas fa-file-image', png: 'fas fa-file-image', gif: 'fas fa-file-image',
  }
  return icons[ext] || 'fas fa-file'
}

function openImageViewer(img) {
  viewerImage.value = img
}

function closeImageViewer() {
  viewerImage.value = null
}

async function handleDelete() {
  const result = await Swal.fire({
    icon: 'warning',
    title: '자료 삭제',
    text: '이 자료를 삭제하시겠습니까?',
    showCancelButton: true,
    confirmButtonText: '삭제',
    cancelButtonText: '취소',
    confirmButtonColor: '#e74c3c'
  })
  if (!result.isConfirmed) return

  try {
    await refStore.deleteReference(route.params.id)
    Swal.fire({ icon: 'success', title: '삭제 완료', timer: 1500, showConfirmButton: false })
    router.push('/references')
  } catch {
    Swal.fire({ icon: 'error', title: '삭제 실패', text: '자료 삭제에 실패했습니다.' })
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
  max-width: var(--max-width);
  margin: 0 auto;
  padding: 40px 24px 80px;
}

/* 상세 카드 */
.detail-card {
  background: var(--white);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
}

.detail-header {
  padding: 32px 32px 24px;
  border-bottom: 1px solid var(--border);
}

.detail-title {
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--text-dark);
  margin-bottom: 12px;
}

.detail-meta {
  display: flex;
  gap: 20px;
  font-size: 0.85rem;
  color: var(--text-muted);
  flex-wrap: wrap;
}

.detail-meta span {
  display: flex;
  align-items: center;
  gap: 6px;
}

.detail-meta i {
  width: 16px;
  text-align: center;
}

/* 썸네일 */
.detail-thumbnail {
  padding: 24px 32px;
  text-align: center;
}

.detail-thumbnail img {
  max-width: 100%;
  max-height: 500px;
  object-fit: contain;
  border-radius: var(--radius-sm);
  border: 1px solid var(--border);
}

/* 설명 */
.detail-description {
  padding: 0 32px 24px;
}

.detail-description p {
  font-size: 0.95rem;
  line-height: 1.8;
  color: var(--text-body);
  white-space: pre-wrap;
}

/* 갤러리 이미지 */
.detail-gallery {
  padding: 0 32px 24px;
}

.section-title {
  font-size: 1rem;
  font-weight: 600;
  color: var(--text-dark);
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-title i {
  color: var(--primary);
}

.gallery-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 12px;
}

.gallery-item {
  border-radius: var(--radius-sm);
  overflow: hidden;
  border: 1px solid var(--border);
  cursor: pointer;
  transition: all 0.3s ease;
  aspect-ratio: 4 / 3;
}

.gallery-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
}

.gallery-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 첨부파일 */
.detail-files {
  padding: 24px 32px;
  border-top: 1px solid var(--border);
}

.file-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.file-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  background: var(--bg-light);
  border-radius: var(--radius-sm);
  text-decoration: none;
  color: var(--text-body);
  font-size: 0.9rem;
  transition: background 0.2s;
}

.file-item:hover {
  background: #e9ecef;
}

.file-item i:first-child {
  color: var(--primary);
  font-size: 1.1rem;
  width: 20px;
  text-align: center;
}

.file-name {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.file-dl-icon {
  color: var(--accent);
}

/* 액션 */
.detail-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 32px;
  border-top: 1px solid var(--border);
}

.btn-outline {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 24px;
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

.btn-danger {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 24px;
  background: #e74c3c;
  color: var(--white);
  border: none;
  border-radius: var(--radius-sm);
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: var(--transition);
}

.btn-danger:hover {
  background: #c0392b;
}

/* 로딩 */
.loading-state {
  text-align: center;
  padding: 80px 20px;
  color: var(--text-muted);
}

.loading-state i {
  font-size: 2rem;
  margin-bottom: 16px;
}

/* 이미지 뷰어 */
.image-viewer {
  position: fixed;
  inset: 0;
  z-index: 9999;
  background: rgba(0, 0, 0, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.image-viewer img {
  max-width: 90vw;
  max-height: 90vh;
  object-fit: contain;
  cursor: default;
  border-radius: 4px;
}

.viewer-close {
  position: absolute;
  top: 20px;
  right: 20px;
  width: 44px;
  height: 44px;
  background: rgba(255, 255, 255, 0.15);
  border: none;
  border-radius: 50%;
  color: #fff;
  font-size: 1.2rem;
  cursor: pointer;
  transition: background 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.viewer-close:hover {
  background: rgba(255, 255, 255, 0.3);
}

@media (max-width: 768px) {
  .page-hero { height: 200px; }
  .page-hero-title { font-size: 1.6rem; }
  .detail-header { padding: 24px 20px 20px; }
  .detail-title { font-size: 1.2rem; }
  .detail-thumbnail { padding: 16px 20px; }
  .detail-description { padding: 0 20px 20px; }
  .detail-gallery { padding: 0 20px 20px; }
  .gallery-grid { grid-template-columns: repeat(2, 1fr); }
  .detail-files { padding: 20px; }
  .detail-actions { padding: 20px; }
}
</style>
