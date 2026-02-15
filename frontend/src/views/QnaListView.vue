<template>
  <div class="page-wrapper">
    <div class="page-hero">
      <div class="page-hero-overlay"></div>
      <div class="page-hero-content">
        <p class="page-hero-subtitle">Community</p>
        <h1 class="page-hero-title">Q&A 게시판</h1>
        <div class="page-hero-bar"></div>
      </div>
    </div>

    <div class="page-container">
      <div class="board-header">
        <p class="board-count">총 <strong>{{ totalElements }}</strong>건</p>
        <div class="board-header-right">
          <div class="search-box">
            <input
              v-model="searchKeyword"
              type="text"
              placeholder="제목/내용 검색"
              class="search-input"
              @keydown.enter="handleSearch"
            />
            <button class="search-btn" @click="handleSearch">
              <i class="fas fa-search"></i>
            </button>
          </div>
          <router-link to="/qna/write" class="btn-primary">
            <i class="fas fa-pen"></i> 글쓰기
          </router-link>
        </div>
      </div>

      <div class="board-table-wrap">
        <table class="board-table">
          <thead>
            <tr>
              <th class="col-no">번호</th>
              <th class="col-title">제목</th>
              <th class="col-author">작성자</th>
              <th class="col-date">작성일</th>
              <th class="col-views">조회</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="posts.length === 0">
              <td colspan="5" class="empty-row">등록된 게시글이 없습니다.</td>
            </tr>
            <tr
              v-for="post in posts"
              :key="post.id"
              class="clickable-row"
              :class="{ 'notice-row': post.isNotice }"
              @click="goToDetail(post.id)"
            >
              <td class="col-no">
                <span v-if="post.isNotice" class="notice-badge">공지</span>
                <span v-else>{{ post.id }}</span>
              </td>
              <td class="col-title">
                <span v-if="post.isNotice" class="notice-title">{{ post.title }}</span>
                <span v-else>{{ post.title }}</span>
                <span v-if="post.commentCount > 0" class="comment-count">[{{ post.commentCount }}]</span>
              </td>
              <td class="col-author">{{ post.authorName }}</td>
              <td class="col-date">{{ formatDate(post.createdAt) }}</td>
              <td class="col-views">{{ post.viewCount }}</td>
            </tr>
          </tbody>
        </table>
      </div>

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
import { useQnaStore } from '@/stores/qna'

const PAGE_SIZE = 15

const router = useRouter()
const qnaStore = useQnaStore()
const { posts, totalPages, totalElements, currentPage } = storeToRefs(qnaStore)

const searchKeyword = ref('')
const isSearching = ref(false)
const showScrollTop = ref(false)

onMounted(() => {
  qnaStore.fetchPosts(0, PAGE_SIZE)
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

function goToDetail(id) {
  router.push(`/qna/${id}`)
}

function changePage(page) {
  if (isSearching.value && searchKeyword.value) {
    qnaStore.searchPosts(searchKeyword.value, page, PAGE_SIZE)
  } else {
    qnaStore.fetchPosts(page, PAGE_SIZE)
  }
}

function handleSearch() {
  if (searchKeyword.value.trim()) {
    isSearching.value = true
    qnaStore.searchPosts(searchKeyword.value.trim(), 0, PAGE_SIZE)
  } else {
    isSearching.value = false
    qnaStore.fetchPosts(0, PAGE_SIZE)
  }
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return `${d.getFullYear()}.${String(d.getMonth() + 1).padStart(2, '0')}.${String(d.getDate()).padStart(2, '0')}`
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

.board-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 12px;
}

.board-header-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.board-count {
  font-size: 0.95rem;
  color: var(--text-body);
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
  width: 200px;
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

.btn-primary {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 24px;
  background: var(--primary);
  color: var(--white);
  border: none;
  border-radius: var(--radius-sm);
  font-size: 0.9rem;
  font-weight: 500;
  text-decoration: none;
  cursor: pointer;
  transition: var(--transition);
}

.btn-primary:hover {
  background: var(--primary-light);
  transform: translateY(-1px);
}

.board-table-wrap {
  background: var(--white);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
}

.board-table {
  width: 100%;
  border-collapse: collapse;
}

.board-table thead {
  background: var(--primary);
  color: var(--white);
}

.board-table th {
  padding: 14px 16px;
  font-size: 0.9rem;
  font-weight: 500;
  text-align: center;
  white-space: nowrap;
}

.board-table td {
  padding: 14px 16px;
  font-size: 0.9rem;
  color: var(--text-body);
  text-align: center;
  border-bottom: 1px solid var(--border);
}

.col-no { width: 90px; }
.col-title { text-align: left !important; }
.col-author { width: 100px; }
.col-date { width: 110px; }
.col-views { width: 70px; }

.board-table th.col-title { text-align: left; }

.clickable-row {
  cursor: pointer;
  transition: var(--transition);
}

.clickable-row:hover {
  background: var(--bg-section);
}

.clickable-row:hover .col-title {
  color: var(--primary);
}

/* 공지 스타일 */
.notice-row {
  background: #fffde7;
}

.notice-row:hover {
  background: #fff9c4;
}

.notice-badge {
  display: inline-block;
  padding: 3px 12px;
  background: #f9a825;
  color: #fff;
  border-radius: 4px;
  font-size: 0.75rem;
  font-weight: 600;
  white-space: nowrap;
  letter-spacing: 1px;
}

.notice-title {
  font-weight: 700;
  color: #e65100;
}

.notice-row:hover .col-title {
  color: #bf360c;
}

.comment-count {
  color: var(--primary);
  font-size: 0.82rem;
  margin-left: 4px;
}

.empty-row {
  padding: 60px 16px !important;
  text-align: center !important;
  color: var(--text-muted);
}

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

/* 맨위로 버튼 */
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

@media (max-width: 768px) {
  .page-hero { height: 200px; }
  .page-hero-title { font-size: 1.6rem; }
  .col-no, .col-views { display: none; }
  .col-author { width: 80px; }
  .board-table th, .board-table td { padding: 12px 10px; font-size: 0.85rem; }
  .board-header { flex-direction: column; align-items: stretch; }
  .board-header-right { justify-content: space-between; }
  .search-input { width: 140px; }
}
</style>
