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
      <!-- 카테고리 필터 + 검색 + 관리 -->
      <div class="board-header">
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
        <div class="board-header-right">
          <div class="search-box">
            <input
              v-model="searchKeyword"
              type="text"
              placeholder="제목/설명 검색"
              class="search-input"
              @keydown.enter="handleSearch"
            />
            <button class="search-btn" @click="handleSearch">
              <i class="fas fa-search"></i>
            </button>
          </div>
          <template v-if="isAdmin">
            <button class="btn-outline btn-sm" @click="openCategoryModal">
              <i class="fas fa-folder-plus"></i> 카테고리
            </button>
            <router-link to="/references/write" class="btn-primary btn-sm">
              <i class="fas fa-upload"></i> 업로드
            </router-link>
          </template>
        </div>
      </div>

      <!-- 총 건수 -->
      <p class="board-count">총 <strong>{{ totalElements }}</strong>건</p>

      <!-- 자료 카드 그리드 — 포스터 스타일 -->
      <div v-if="references.length === 0" class="empty-state">
        <i class="fas fa-folder-open"></i>
        <p>등록된 자료가 없습니다.</p>
      </div>

      <div v-else class="ref-grid">
        <div v-for="item in references" :key="item.id" class="ref-item" @click="goToDetail(item.id)">
          <div class="ref-poster">
            <img
              v-if="item.thumbnailPath"
              :src="refStore.getThumbnailUrl(item.id)"
              :alt="item.title"
              class="ref-poster-img"
              @error="handleThumbError"
            />
            <div v-else class="ref-poster-placeholder">
              <i :class="getFileIcon(item.fileName)"></i>
              <span class="placeholder-ext">{{ getFileExt(item.fileName) }}</span>
            </div>
            <!-- 관리자 삭제 버튼 -->
            <button v-if="isAdmin" class="ref-delete-btn" @click.stop="handleDelete(item.id)" title="삭제">
              <i class="fas fa-trash"></i>
            </button>
          </div>
          <p class="ref-title">{{ item.title }}</p>
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

    <!-- 맨위로 버튼 -->
    <button v-show="showScrollTop" class="scroll-top-btn" @click="scrollToTop" title="맨위로">
      <i class="fas fa-arrow-up"></i>
    </button>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { storeToRefs } from 'pinia'
import { useReferenceStore } from '@/stores/reference'
import { useAuthStore } from '@/stores/auth'
import Swal from 'sweetalert2'

const router = useRouter()

const refStore = useReferenceStore()
const authStore = useAuthStore()
const { categories, references, totalPages, totalElements, currentPage } = storeToRefs(refStore)
const { isLoggedIn: isAdmin } = storeToRefs(authStore)

const selectedCategory = ref(null)
const searchKeyword = ref('')
const isSearching = ref(false)
const showScrollTop = ref(false)

onMounted(async () => {
  await authStore.checkLogin()
  await refStore.fetchCategories()
  await refStore.fetchReferences()
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})

function handleScroll() {
  showScrollTop.value = window.scrollY > 300
}

function scrollToTop() {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

function filterByCategory(categoryId) {
  selectedCategory.value = categoryId
  searchKeyword.value = ''
  isSearching.value = false
  if (categoryId) {
    refStore.fetchByCategory(categoryId)
  } else {
    refStore.fetchReferences()
  }
}

function changePage(page) {
  if (isSearching.value && searchKeyword.value) {
    refStore.searchReferences(searchKeyword.value, page)
  } else if (selectedCategory.value) {
    refStore.fetchByCategory(selectedCategory.value, page)
  } else {
    refStore.fetchReferences(page)
  }
}

function handleSearch() {
  if (searchKeyword.value.trim()) {
    isSearching.value = true
    selectedCategory.value = null
    refStore.searchReferences(searchKeyword.value.trim(), 0)
  } else {
    isSearching.value = false
    refStore.fetchReferences(0)
  }
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

function getFileExt(fileName) {
  if (!fileName) return ''
  return fileName.split('.').pop().toUpperCase()
}

function handleThumbError(e) {
  e.target.style.display = 'none'
  e.target.parentElement.classList.add('thumb-fallback')
}

function goToDetail(id) {
  router.push(`/references/${id}`)
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

/* 헤더 */
.board-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
  flex-wrap: wrap;
  gap: 12px;
}

.board-header-right {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.board-count {
  font-size: 0.9rem;
  color: var(--text-body);
  margin-bottom: 20px;
}

/* 카테고리 */
.category-filter {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
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

/* 검색 */
.search-box {
  display: flex;
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  overflow: hidden;
  background: var(--white);
}

.search-input {
  padding: 8px 14px;
  border: none;
  font-size: 0.85rem;
  width: 180px;
  outline: none;
  font-family: inherit;
}

.search-btn {
  padding: 8px 14px;
  background: var(--primary);
  color: var(--white);
  border: none;
  cursor: pointer;
  transition: var(--transition);
}

.search-btn:hover {
  background: var(--primary-light);
}

/* 버튼 */
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
  text-decoration: none;
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

/* 포스터형 그리드 */
.ref-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 28px 24px;
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

.ref-item {
  cursor: pointer;
}

.ref-item:hover .ref-poster {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  transform: translateY(-4px);
}

.ref-item:hover .ref-title {
  color: var(--primary);
}

/* 포스터 이미지 영역 */
.ref-poster {
  position: relative;
  width: 100%;
  aspect-ratio: 3 / 4;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  overflow: hidden;
  background: #f8f9fa;
  transition: all 0.3s ease;
}

.ref-poster-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.ref-poster-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%);
}

.ref-poster-placeholder i {
  font-size: 3.5rem;
  color: var(--primary);
  opacity: 0.2;
}

.placeholder-ext {
  font-size: 0.8rem;
  font-weight: 700;
  color: var(--primary);
  opacity: 0.3;
  letter-spacing: 1px;
}

.thumb-fallback {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.thumb-fallback::after {
  content: '\f15b';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  font-size: 3.5rem;
  color: var(--primary);
  opacity: 0.2;
}

/* 제목 (이미지 바깥 아래) */
.ref-title {
  margin-top: 10px;
  font-size: 0.9rem;
  font-weight: 500;
  color: var(--text-dark);
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  transition: color 0.2s;
}

/* 관리자 삭제 */
.ref-delete-btn {
  position: absolute;
  top: 8px;
  right: 8px;
  width: 30px;
  height: 30px;
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

.ref-item:hover .ref-delete-btn {
  opacity: 1;
}

.ref-delete-btn:hover {
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

/* 맨위로 */
.scroll-top-btn {
  position: fixed;
  bottom: 30px;
  right: 30px;
  width: 48px;
  height: 48px;
  background: var(--primary);
  color: var(--white);
  border: none;
  border-radius: 50%;
  font-size: 1.1rem;
  cursor: pointer;
  box-shadow: var(--shadow-md);
  transition: var(--transition);
  z-index: 999;
}

.scroll-top-btn:hover {
  background: var(--primary-light);
  transform: translateY(-2px);
}

@media (max-width: 1024px) {
  .ref-grid { grid-template-columns: repeat(3, 1fr); gap: 20px; }
}

@media (max-width: 768px) {
  .page-hero { height: 200px; }
  .page-hero-title { font-size: 1.6rem; }
  .ref-grid { grid-template-columns: repeat(2, 1fr); gap: 16px; }
  .board-header { flex-direction: column; align-items: stretch; }
  .board-header-right { justify-content: flex-end; }
  .search-input { width: 140px; }
}

@media (max-width: 480px) {
  .ref-grid { grid-template-columns: repeat(2, 1fr); gap: 12px; }
  .ref-title { font-size: 0.82rem; }
}
</style>
