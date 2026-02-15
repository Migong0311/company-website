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
      <div class="board-header" v-if="authStore.isLoggedIn">
        <div class="admin-actions">
          <button class="btn-outline btn-sm" @click="openCategoryModal">
            <i class="fas fa-folder-plus"></i> 카테고리 관리
          </button>
          <button class="btn-primary btn-sm" @click="openUploadModal">
            <i class="fas fa-upload"></i> 자료 업로드
          </button>
        </div>
      </div>

      <!-- 자료 목록 -->
      <div class="ref-grid">
        <div v-if="references.length === 0" class="empty-state">
          <i class="fas fa-folder-open"></i>
          <p>등록된 자료가 없습니다.</p>
        </div>
        <div v-for="item in references" :key="item.id" class="ref-card">
          <div class="ref-card-icon">
            <i class="fas fa-file-pdf"></i>
          </div>
          <div class="ref-card-content">
            <span class="ref-category-tag" v-if="item.categoryName">{{ item.categoryName }}</span>
            <h3 class="ref-card-title">{{ item.title }}</h3>
            <p class="ref-card-desc" v-if="item.description">{{ item.description }}</p>
            <div class="ref-card-meta">
              <span><i class="fas fa-calendar"></i> {{ formatDate(item.createdAt) }}</span>
              <span><i class="fas fa-download"></i> {{ item.downloadCount }}회</span>
            </div>
          </div>
          <div class="ref-card-actions">
            <a :href="getDownloadUrl(item.id)" class="btn-download" download>
              <i class="fas fa-download"></i> 다운로드
            </a>
            <button v-if="authStore.isLoggedIn" class="btn-delete-sm" @click="handleDelete(item.id)">
              <i class="fas fa-trash"></i>
            </button>
          </div>
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

function getDownloadUrl(id) {
  return refStore.getDownloadUrl(id)
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return `${d.getFullYear()}.${String(d.getMonth() + 1).padStart(2, '0')}.${String(d.getDate()).padStart(2, '0')}`
}

async function openCategoryModal() {
  const catList = categories.value.map(c => `<div class="swal-cat-row" style="display:flex;align-items:center;gap:8px;margin-bottom:8px;">
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

async function openUploadModal() {
  if (categories.value.length === 0) {
    Swal.fire({ icon: 'warning', title: '카테고리 필요', text: '먼저 카테고리를 추가해주세요.' })
    return
  }

  const catOptions = categories.value.map(c => `<option value="${c.id}">${c.name}</option>`).join('')

  const { value: formValues } = await Swal.fire({
    title: '자료 업로드',
    html: `
      <select id="swal-ref-cat" class="swal2-select" style="width:100%;padding:10px;border:1px solid #d9d9d9;border-radius:4px;margin-bottom:12px;">
        ${catOptions}
      </select>
      <input id="swal-ref-title" class="swal2-input" placeholder="자료 제목">
      <textarea id="swal-ref-desc" class="swal2-textarea" placeholder="설명 (선택)"></textarea>
      <input id="swal-ref-file" type="file" class="swal2-file" style="margin-top:8px;">
      <input id="swal-ref-thumb" type="file" class="swal2-file" accept="image/*" style="margin-top:8px;">
      <small style="color:#999;display:block;text-align:left;margin-top:4px;">썸네일 이미지 (선택)</small>
    `,
    showCancelButton: true,
    confirmButtonText: '업로드',
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
    await refStore.fetchReferences()
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
  text-decoration: none;
}

.btn-outline:hover {
  border-color: var(--primary);
  color: var(--primary);
}

.btn-sm {
  padding: 8px 16px;
  font-size: 0.85rem;
}

/* 자료 그리드 */
.ref-grid {
  display: flex;
  flex-direction: column;
  gap: 12px;
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
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 20px 24px;
  background: var(--white);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-sm);
  transition: var(--transition);
}

.ref-card:hover {
  box-shadow: var(--shadow-md);
  transform: translateY(-2px);
}

.ref-card-icon {
  width: 52px;
  height: 52px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--primary), var(--primary-light));
  border-radius: var(--radius-sm);
  flex-shrink: 0;
}

.ref-card-icon i {
  font-size: 1.4rem;
  color: var(--white);
}

.ref-card-content {
  flex: 1;
  min-width: 0;
}

.ref-category-tag {
  display: inline-block;
  font-size: 0.75rem;
  padding: 2px 10px;
  background: var(--bg-section);
  color: var(--primary);
  border-radius: 10px;
  margin-bottom: 6px;
}

.ref-card-title {
  font-size: 1rem;
  font-weight: 600;
  color: var(--text-dark);
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.ref-card-desc {
  font-size: 0.85rem;
  color: var(--text-muted);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 6px;
}

.ref-card-meta {
  display: flex;
  gap: 16px;
  font-size: 0.8rem;
  color: var(--text-muted);
}

.ref-card-meta i {
  margin-right: 4px;
}

.ref-card-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.btn-download {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 18px;
  background: var(--accent);
  color: var(--white);
  border: none;
  border-radius: var(--radius-sm);
  font-size: 0.85rem;
  font-weight: 500;
  cursor: pointer;
  transition: var(--transition);
  text-decoration: none;
}

.btn-download:hover {
  background: var(--accent-light);
}

.btn-delete-sm {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--white);
  border: 1px solid #e74c3c;
  border-radius: var(--radius-sm);
  color: #e74c3c;
  cursor: pointer;
  transition: var(--transition);
}

.btn-delete-sm:hover {
  background: #e74c3c;
  color: var(--white);
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
  .ref-card { flex-direction: column; align-items: flex-start; gap: 12px; }
  .ref-card-actions { width: 100%; justify-content: flex-end; }
  .ref-card-icon { width: 40px; height: 40px; }
  .ref-card-icon i { font-size: 1.1rem; }
  .admin-actions { flex-direction: column; width: 100%; }
  .admin-actions .btn-outline,
  .admin-actions .btn-primary { width: 100%; justify-content: center; }
}
</style>
