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

    <div class="page-container">
      <!-- 카테고리 필터 -->
      <div class="category-filter">
        <button
          class="filter-btn"
          :class="{ active: selectedCategory === null }"
          @click="filterByCategory(null)"
        >
          전체
        </button>
        <button
          v-for="cat in categories"
          :key="cat.id"
          class="filter-btn"
          :class="{ active: selectedCategory === cat.id }"
          @click="filterByCategory(cat.id)"
        >
          {{ cat.name }}
        </button>
      </div>

      <!-- 관리자 기능 -->
      <div class="board-header" v-if="isAdmin">
        <div class="admin-actions">
          <button class="btn-outline btn-sm" @click="openCategoryModal">
            <i class="fas fa-folder-plus"></i> 카테고리 관리
          </button>
          <button class="btn-primary btn-sm" @click="openUploadModal">
            <i class="fas fa-upload"></i> 자료 업로드
          </button>
        </div>
      </div>

      <!-- 자료 카드 그리드 -->
      <div v-if="references.length === 0" class="empty-state">
        <i class="fas fa-folder-open"></i>
        <p>등록된 자료가 없습니다.</p>
      </div>

      <div v-else class="ref-grid">
        <div v-for="item in references" :key="item.id" class="ref-card" @click="openDetail(item)">
          <div class="ref-thumb">
            <img
              v-if="item.thumbnailPath"
              :src="refStore.getThumbnailUrl(item.id)"
              :alt="item.title"
              class="ref-thumb-img"
              @error="handleThumbError"
            />
            <div v-else class="ref-thumb-placeholder">
              <i :class="getFileIcon(item.fileName)"></i>
            </div>
            <span class="ref-category-tag" v-if="item.categoryName">{{ item.categoryName }}</span>
          </div>
          <div class="ref-card-body">
            <h3 class="ref-card-title">{{ item.title }}</h3>
            <p class="ref-card-desc" v-if="item.description">{{ item.description }}</p>
            <div class="ref-card-meta">
              <span><i class="fas fa-file"></i> {{ item.fileName }}</span>
              <span><i class="fas fa-download"></i> {{ item.downloadCount }}회</span>
            </div>
            <div class="ref-card-date">
              <i class="fas fa-calendar"></i> {{ formatDate(item.createdAt) }}
            </div>
          </div>
          <!-- 관리자 삭제 버튼 -->
          <button v-if="isAdmin" class="ref-card-delete" @click.stop="handleDelete(item.id)" title="삭제">
            <i class="fas fa-trash"></i>
          </button>
        </div>
      </div>

      <!-- 페이지네이션 -->
      <div class="pagination" v-if="totalPages > 1">
        <button class="page-btn" :disabled="currentPage === 0" @click="changePage(currentPage - 1)">
          <i class="fas fa-chevron-left"></i>
        </button>
        <button
          v-for="page in totalPages"
          :key="page"
          class="page-btn"
          :class="{ active: currentPage === page - 1 }"
          @click="changePage(page - 1)"
        >
          {{ page }}
        </button>
        <button class="page-btn" :disabled="currentPage === totalPages - 1" @click="changePage(currentPage + 1)">
          <i class="fas fa-chevron-right"></i>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import { useReferenceStore } from '@/stores/reference'
import { useAuthStore } from '@/stores/auth'
import Swal from 'sweetalert2'

const refStore = useReferenceStore()
const authStore = useAuthStore()
const { categories, references, totalPages, currentPage } = storeToRefs(refStore)
const { isLoggedIn: isAdmin } = storeToRefs(authStore)

const selectedCategory = ref(null)

onMounted(async () => {
  await authStore.checkLogin()
  await refStore.fetchCategories()
  await refStore.fetchReferences()
})

function filterByCategory(categoryId) {
  selectedCategory.value = categoryId
  if (categoryId) {
    refStore.fetchByCategory(categoryId)
  } else {
    refStore.fetchReferences()
  }
}

function changePage(page) {
  if (selectedCategory.value) {
    refStore.fetchByCategory(selectedCategory.value, page)
  } else {
    refStore.fetchReferences(page)
  }
}

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

function handleThumbError(e) {
  e.target.style.display = 'none'
  e.target.parentElement.classList.add('thumb-fallback')
}

/* 자료 상세 모달 */
function openDetail(item) {
  const thumbHtml = item.thumbnailPath
    ? `<div style="margin-bottom:16px;"><img src="${refStore.getThumbnailUrl(item.id)}" style="max-width:100%;max-height:300px;border-radius:8px;object-fit:contain;" /></div>`
    : ''

  Swal.fire({
    title: item.title,
    html: `
      ${thumbHtml}
      ${item.description ? `<p style="text-align:left;color:#555;margin-bottom:12px;">${item.description}</p>` : ''}
      <div style="text-align:left;font-size:0.85rem;color:#888;">
        <p><i class="fas fa-folder" style="width:18px;"></i> ${item.categoryName || '미분류'}</p>
        <p><i class="fas fa-file" style="width:18px;"></i> ${item.fileName}</p>
        <p><i class="fas fa-download" style="width:18px;"></i> 다운로드 ${item.downloadCount}회</p>
        <p><i class="fas fa-calendar" style="width:18px;"></i> ${formatDate(item.createdAt)}</p>
      </div>
    `,
    showCancelButton: true,
    confirmButtonText: '<i class="fas fa-download"></i> 다운로드',
    cancelButtonText: '닫기',
    confirmButtonColor: '#e8a020',
  }).then((result) => {
    if (result.isConfirmed) {
      const link = document.createElement('a')
      link.href = refStore.getDownloadUrl(item.id)
      link.download = ''
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
    }
  })
}

/* 카테고리 관리 모달 */
async function openCategoryModal() {
  const catList = categories.value.map(c => `<div style="display:flex;align-items:center;gap:8px;margin-bottom:8px;">
    <span style="flex:1">${c.name} (순서: ${c.sortOrder})</span>
    <button class="swal-cat-del" data-id="${c.id}" style="background:#e74c3c;color:#fff;border:none;border-radius:4px;padding:4px 10px;cursor:pointer;font-size:0.8rem;">삭제</button>
  </div>`).join('')

  const { value: formValues } = await Swal.fire({
    title: '카테고리 관리',
    html: `
      <div style="text-align:left;margin-bottom:16px;max-height:200px;overflow-y:auto;">${catList || '<p style="color:#999">등록된 카테고리가 없습니다.</p>'}</div>
      <hr style="margin:16px 0">
      <p style="font-weight:600;text-align:left;margin-bottom:8px;">새 카테고리 추가</p>
      <input id="swal-cat-name" class="swal2-input" placeholder="카테고리명">
      <input id="swal-cat-order" class="swal2-input" type="number" placeholder="정렬 순서 (숫자)" value="0">
    `,
    showCancelButton: true,
    confirmButtonText: '추가',
    cancelButtonText: '닫기',
    confirmButtonColor: '#1a3a5c',
    didOpen: () => {
      document.querySelectorAll('.swal-cat-del').forEach(btn => {
        btn.addEventListener('click', async () => {
          const catId = btn.getAttribute('data-id')
          try {
            await refStore.deleteCategory(catId)
            await refStore.fetchCategories()
            Swal.close()
            openCategoryModal()
          } catch {
            Swal.showValidationMessage('카테고리 삭제에 실패했습니다.')
          }
        })
      })
    },
    preConfirm: () => {
      const name = document.getElementById('swal-cat-name').value
      const sortOrder = parseInt(document.getElementById('swal-cat-order').value) || 0
      if (!name) {
        Swal.showValidationMessage('카테고리명을 입력해주세요.')
        return false
      }
      return { name, sortOrder }
    }
  })

  if (!formValues) return

  try {
    await refStore.createCategory(formValues)
    await refStore.fetchCategories()
    Swal.fire({ icon: 'success', title: '추가 완료', timer: 1500, showConfirmButton: false })
  } catch {
    Swal.fire({ icon: 'error', title: '오류', text: '카테고리 추가에 실패했습니다.' })
  }
}

/* 자료 업로드 모달 (다중 파일 지원) */
async function openUploadModal() {
  if (categories.value.length === 0) {
    Swal.fire({ icon: 'warning', title: '카테고리 필요', text: '먼저 카테고리를 추가해주세요.' })
    return
  }

  const catOptions = categories.value.map(c => `<option value="${c.id}">${c.name}</option>`).join('')

  const { value: formValues } = await Swal.fire({
    title: '자료 업로드',
    width: 560,
    html: `
      <div style="text-align:left;">
        <label style="font-weight:600;font-size:0.85rem;display:block;margin-bottom:6px;">카테고리</label>
        <select id="swal-ref-cat" class="swal2-select" style="width:100%;padding:10px;border:1px solid #d9d9d9;border-radius:4px;margin-bottom:16px;font-size:0.9rem;">
          ${catOptions}
        </select>

        <label style="font-weight:600;font-size:0.85rem;display:block;margin-bottom:6px;">제목</label>
        <input id="swal-ref-title" class="swal2-input" placeholder="자료 제목" style="margin:0 0 16px 0;width:100%;box-sizing:border-box;">

        <label style="font-weight:600;font-size:0.85rem;display:block;margin-bottom:6px;">설명 <small style="color:#999">(선택)</small></label>
        <textarea id="swal-ref-desc" class="swal2-textarea" placeholder="자료에 대한 설명" style="margin:0 0 16px 0;width:100%;box-sizing:border-box;min-height:80px;"></textarea>

        <label style="font-weight:600;font-size:0.85rem;display:block;margin-bottom:6px;">첨부파일</label>
        <input id="swal-ref-file" type="file" style="margin-bottom:8px;font-size:0.85rem;">
        <p style="color:#999;font-size:0.78rem;margin-bottom:16px;">최대 10MB / PDF, 문서, 이미지, 압축파일 등</p>

        <label style="font-weight:600;font-size:0.85rem;display:block;margin-bottom:6px;">썸네일 이미지 <small style="color:#999">(선택)</small></label>
        <input id="swal-ref-thumb" type="file" accept="image/*" style="margin-bottom:4px;font-size:0.85rem;">
        <p style="color:#e8a020;font-size:0.78rem;"><i class="fas fa-info-circle"></i> 이미지 파일만 업로드 가능합니다 (JPG, PNG, GIF, WebP)</p>
      </div>
    `,
    showCancelButton: true,
    confirmButtonText: '<i class="fas fa-upload"></i> 업로드',
    cancelButtonText: '취소',
    confirmButtonColor: '#1a3a5c',
    preConfirm: () => {
      const categoryId = document.getElementById('swal-ref-cat').value
      const title = document.getElementById('swal-ref-title').value
      const description = document.getElementById('swal-ref-desc').value
      const file = document.getElementById('swal-ref-file').files[0]
      const thumbnail = document.getElementById('swal-ref-thumb').files[0]

      if (!title) {
        Swal.showValidationMessage('제목을 입력해주세요.')
        return false
      }
      if (!file) {
        Swal.showValidationMessage('파일을 선택해주세요.')
        return false
      }
      if (file.size > 10 * 1024 * 1024) {
        Swal.showValidationMessage('파일 크기는 10MB를 초과할 수 없습니다.')
        return false
      }
      if (thumbnail && !thumbnail.type.startsWith('image/')) {
        Swal.showValidationMessage('썸네일은 이미지 파일만 가능합니다.')
        return false
      }

      const formData = new FormData()
      formData.append('categoryId', categoryId)
      formData.append('title', title)
      if (description) formData.append('description', description)
      formData.append('file', file)
      if (thumbnail) formData.append('thumbnail', thumbnail)
      return formData
    }
  })

  if (!formValues) return

  try {
    await refStore.createReference(formValues)
    if (selectedCategory.value) {
      await refStore.fetchByCategory(selectedCategory.value)
    } else {
      await refStore.fetchReferences()
    }
    Swal.fire({ icon: 'success', title: '업로드 완료', text: '자료가 등록되었습니다.', timer: 1500, showConfirmButton: false })
  } catch {
    Swal.fire({ icon: 'error', title: '업로드 실패', text: '파일 업로드에 실패했습니다.' })
  }
}

async function handleDelete(id) {
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
    await refStore.deleteReference(id)
    if (selectedCategory.value) {
      await refStore.fetchByCategory(selectedCategory.value)
    } else {
      await refStore.fetchReferences()
    }
    Swal.fire({ icon: 'success', title: '삭제 완료', timer: 1500, showConfirmButton: false })
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

/* 카테고리 필터 */
.category-filter {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 24px;
}

.filter-btn {
  padding: 8px 20px;
  background: var(--white);
  border: 1px solid var(--border);
  border-radius: 20px;
  font-size: 0.85rem;
  color: var(--text-body);
  cursor: pointer;
  transition: var(--transition);
}

.filter-btn:hover {
  border-color: var(--primary);
  color: var(--primary);
}

.filter-btn.active {
  background: var(--primary);
  color: var(--white);
  border-color: var(--primary);
}

/* 관리자 헤더 */
.board-header {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 20px;
}

.admin-actions {
  display: flex;
  gap: 8px;
}

.btn-primary {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 10px 24px;
  background: var(--primary);
  color: var(--white);
  border: none;
  border-radius: var(--radius-sm);
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: var(--transition);
}

.btn-primary:hover {
  background: var(--primary-light);
}

.btn-outline {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 10px 24px;
  background: var(--white);
  color: var(--text-body);
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  font-size: 0.9rem;
  cursor: pointer;
  transition: var(--transition);
}

.btn-outline:hover {
  border-color: var(--primary);
  color: var(--primary);
}

.btn-sm {
  padding: 8px 16px;
  font-size: 0.85rem;
}

/* 자료 카드 그리드 */
.ref-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
  color: var(--text-muted);
}

.empty-state i {
  font-size: 3rem;
  margin-bottom: 16px;
  opacity: 0.3;
}

.ref-card {
  position: relative;
  background: var(--white);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
  cursor: pointer;
  transition: var(--transition);
}

.ref-card:hover {
  box-shadow: var(--shadow-md);
  transform: translateY(-4px);
}

/* 썸네일 영역 */
.ref-thumb {
  position: relative;
  width: 100%;
  height: 180px;
  background: linear-gradient(135deg, #f0f4f8 0%, #e2e8f0 100%);
  overflow: hidden;
}

.ref-thumb-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.ref-thumb-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.ref-thumb-placeholder i {
  font-size: 3rem;
  color: var(--primary);
  opacity: 0.25;
}

.thumb-fallback {
  display: flex;
  align-items: center;
  justify-content: center;
}

.thumb-fallback::after {
  content: '\f15b';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  font-size: 3rem;
  color: var(--primary);
  opacity: 0.25;
}

.ref-category-tag {
  position: absolute;
  top: 10px;
  left: 10px;
  font-size: 0.72rem;
  padding: 3px 10px;
  background: rgba(26, 58, 92, 0.85);
  color: var(--white);
  border-radius: 12px;
  backdrop-filter: blur(4px);
}

/* 카드 바디 */
.ref-card-body {
  padding: 16px 18px 18px;
}

.ref-card-title {
  font-size: 0.95rem;
  font-weight: 600;
  color: var(--text-dark);
  margin-bottom: 6px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.4;
}

.ref-card-desc {
  font-size: 0.82rem;
  color: var(--text-muted);
  margin-bottom: 10px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.5;
}

.ref-card-meta {
  display: flex;
  gap: 14px;
  font-size: 0.78rem;
  color: var(--text-muted);
  margin-bottom: 6px;
}

.ref-card-meta i {
  margin-right: 3px;
}

.ref-card-meta span {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 160px;
}

.ref-card-date {
  font-size: 0.76rem;
  color: var(--text-muted);
}

.ref-card-date i {
  margin-right: 3px;
}

/* 관리자 삭제 버튼 */
.ref-card-delete {
  position: absolute;
  top: 10px;
  right: 10px;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(231, 76, 60, 0.9);
  border: none;
  border-radius: 50%;
  color: #fff;
  font-size: 0.8rem;
  cursor: pointer;
  opacity: 0;
  transition: var(--transition);
}

.ref-card:hover .ref-card-delete {
  opacity: 1;
}

.ref-card-delete:hover {
  background: #c0392b;
  transform: scale(1.1);
}

/* 페이지네이션 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 6px;
  margin-top: 32px;
}

.page-btn {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  background: var(--white);
  color: var(--text-body);
  font-size: 0.85rem;
  cursor: pointer;
  transition: var(--transition);
}

.page-btn:hover:not(:disabled) {
  border-color: var(--primary);
  color: var(--primary);
}

.page-btn.active {
  background: var(--primary);
  color: var(--white);
  border-color: var(--primary);
}

.page-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .page-hero { height: 200px; }
  .page-hero-title { font-size: 1.6rem; }
  .ref-grid { grid-template-columns: repeat(auto-fill, minmax(240px, 1fr)); gap: 14px; }
  .ref-thumb { height: 150px; }
  .admin-actions { flex-direction: column; width: 100%; }
  .admin-actions .btn-outline,
  .admin-actions .btn-primary { width: 100%; justify-content: center; }
}

@media (max-width: 480px) {
  .ref-grid { grid-template-columns: 1fr; }
}
</style>
