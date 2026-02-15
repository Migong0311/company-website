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

    <div class="page-container" v-if="post">
      <div class="post-card">
        <div class="post-header">
          <h2 class="post-title">{{ post.title }}</h2>
          <div class="post-meta">
            <span><i class="fas fa-user"></i> {{ post.authorName }}</span>
            <span><i class="fas fa-calendar"></i> {{ formatDate(post.createdAt) }}</span>
            <span><i class="fas fa-eye"></i> {{ post.viewCount }}</span>
          </div>
        </div>
        <div class="post-body" v-html="formattedContent"></div>
        <div class="post-actions">
          <router-link to="/qna" class="btn-outline">
            <i class="fas fa-list"></i> 목록
          </router-link>
          <div class="post-actions-right">
            <button class="btn-outline" @click="handleEdit">
              <i class="fas fa-edit"></i> 수정
            </button>
            <button class="btn-danger" @click="handleDelete">
              <i class="fas fa-trash"></i> 삭제
            </button>
          </div>
        </div>
      </div>

      <!-- 댓글 영역 -->
      <div class="comments-section">
        <h3 class="comments-title">
          <i class="fas fa-comments"></i> 댓글
          <span class="comments-count">{{ flatCommentCount }}</span>
        </h3>

        <div class="comment-list">
          <div v-if="comments.length === 0" class="comments-empty">
            아직 댓글이 없습니다. 첫 번째 댓글을 남겨보세요!
          </div>
          <template v-for="comment in comments" :key="comment.id">
            <CommentItem
              :comment="comment"
              :depth="0"
              @reply="openReply"
              @edit="openEditComment"
              @delete="handleDeleteComment"
            />
          </template>
        </div>

        <!-- 댓글 작성 -->
        <div class="comment-form-card">
          <h4 class="comment-form-title">댓글 작성</h4>
          <div class="comment-form-row">
            <input v-model="commentForm.authorName" type="text" placeholder="이름" class="input-field input-sm" />
            <input v-model="commentForm.password" type="password" placeholder="비밀번호" class="input-field input-sm" />
          </div>
          <textarea v-model="commentForm.content" placeholder="댓글을 입력하세요." class="input-field textarea-field" rows="3"></textarea>
          <div class="comment-form-actions">
            <button class="btn-primary btn-sm" @click="submitComment()">
              <i class="fas fa-paper-plane"></i> 등록
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { storeToRefs } from 'pinia'
import { useQnaStore } from '@/stores/qna'
import { useAuthStore } from '@/stores/auth'
import Swal from 'sweetalert2'
import CommentItem from '@/components/qna/CommentItem.vue'

const route = useRoute()
const router = useRouter()
const qnaStore = useQnaStore()
const authStore = useAuthStore()
const { currentPost: post, comments } = storeToRefs(qnaStore)

const commentForm = ref({
  authorName: '',
  password: '',
  content: ''
})

const formattedContent = computed(() => {
  if (!post.value?.content) return ''
  return post.value.content.replace(/\n/g, '<br>')
})

const flatCommentCount = computed(() => {
  let count = 0
  function countAll(list) {
    list.forEach(c => {
      count++
      if (c.children?.length) countAll(c.children)
    })
  }
  countAll(comments.value)
  return count
})

onMounted(async () => {
  await authStore.checkLogin()
  await qnaStore.fetchPost(route.params.id)
  await qnaStore.fetchComments(route.params.id)
})

function formatDate(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return `${d.getFullYear()}.${String(d.getMonth() + 1).padStart(2, '0')}.${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}

async function handleEdit() {
  const { value: password } = await Swal.fire({
    icon: 'question',
    title: '비밀번호 확인',
    text: '게시글 작성 시 입력한 비밀번호를 입력하세요.',
    input: 'password',
    inputPlaceholder: '비밀번호',
    showCancelButton: true,
    confirmButtonText: '확인',
    cancelButtonText: '취소',
    confirmButtonColor: '#1a3a5c'
  })
  if (!password) return

  try {
    const valid = await qnaStore.checkPostPassword(route.params.id, password)
    if (valid) {
      router.push({ path: `/qna/${route.params.id}/edit`, query: { pw: password } })
    } else {
      Swal.fire({ icon: 'error', title: '인증 실패', text: '비밀번호가 일치하지 않습니다.' })
    }
  } catch {
    Swal.fire({ icon: 'error', title: '오류', text: '비밀번호 확인에 실패했습니다.' })
  }
}

async function handleDelete() {
  const isAdmin = authStore.isLoggedIn

  let password = ''
  if (!isAdmin) {
    const result = await Swal.fire({
      icon: 'warning',
      title: '게시글 삭제',
      text: '비밀번호를 입력하세요.',
      input: 'password',
      inputPlaceholder: '비밀번호',
      showCancelButton: true,
      confirmButtonText: '삭제',
      cancelButtonText: '취소',
      confirmButtonColor: '#e74c3c'
    })
    if (!result.value) return
    password = result.value
  } else {
    const result = await Swal.fire({
      icon: 'warning',
      title: '게시글 삭제',
      text: '관리자 권한으로 삭제하시겠습니까?',
      showCancelButton: true,
      confirmButtonText: '삭제',
      cancelButtonText: '취소',
      confirmButtonColor: '#e74c3c'
    })
    if (!result.isConfirmed) return
  }

  try {
    await qnaStore.deletePost(route.params.id, password)
    Swal.fire({ icon: 'success', title: '삭제 완료', text: '게시글이 삭제되었습니다.' })
    router.push('/qna')
  } catch {
    Swal.fire({ icon: 'error', title: '삭제 실패', text: '비밀번호가 일치하지 않습니다.' })
  }
}

async function submitComment(parentId = null) {
  const form = commentForm.value
  if (!form.authorName || !form.password || !form.content) {
    Swal.fire({ icon: 'warning', title: '입력 필요', text: '이름, 비밀번호, 내용을 모두 입력해주세요.' })
    return
  }

  try {
    await qnaStore.createComment(route.params.id, {
      authorName: form.authorName,
      password: form.password,
      content: form.content,
      parentId
    })
    commentForm.value = { authorName: '', password: '', content: '' }
    await qnaStore.fetchComments(route.params.id)
    Swal.fire({ icon: 'success', title: '등록 완료', text: '댓글이 등록되었습니다.', timer: 1500, showConfirmButton: false })
  } catch {
    Swal.fire({ icon: 'error', title: '등록 실패', text: '댓글 등록에 실패했습니다.' })
  }
}

async function openReply(parentId) {
  const { value: formValues } = await Swal.fire({
    title: '답글 작성',
    html:
      '<input id="swal-name" class="swal2-input" placeholder="이름">' +
      '<input id="swal-pw" class="swal2-input" type="password" placeholder="비밀번호">' +
      '<textarea id="swal-content" class="swal2-textarea" placeholder="내용"></textarea>',
    focusConfirm: false,
    showCancelButton: true,
    confirmButtonText: '등록',
    cancelButtonText: '취소',
    confirmButtonColor: '#1a3a5c',
    preConfirm: () => {
      const name = document.getElementById('swal-name').value
      const pw = document.getElementById('swal-pw').value
      const content = document.getElementById('swal-content').value
      if (!name || !pw || !content) {
        Swal.showValidationMessage('모든 항목을 입력해주세요.')
        return false
      }
      return { authorName: name, password: pw, content }
    }
  })

  if (!formValues) return

  try {
    await qnaStore.createComment(route.params.id, { ...formValues, parentId })
    await qnaStore.fetchComments(route.params.id)
    Swal.fire({ icon: 'success', title: '등록 완료', timer: 1500, showConfirmButton: false })
  } catch {
    Swal.fire({ icon: 'error', title: '등록 실패', text: '답글 등록에 실패했습니다.' })
  }
}

async function openEditComment(commentId) {
  const { value: password } = await Swal.fire({
    icon: 'question',
    title: '비밀번호 확인',
    input: 'password',
    inputPlaceholder: '비밀번호',
    showCancelButton: true,
    confirmButtonText: '확인',
    cancelButtonText: '취소',
    confirmButtonColor: '#1a3a5c'
  })
  if (!password) return

  try {
    const valid = await qnaStore.checkCommentPassword(commentId, password)
    if (!valid) {
      Swal.fire({ icon: 'error', title: '인증 실패', text: '비밀번호가 일치하지 않습니다.' })
      return
    }

    const { value: content } = await Swal.fire({
      title: '댓글 수정',
      input: 'textarea',
      inputPlaceholder: '수정할 내용을 입력하세요.',
      showCancelButton: true,
      confirmButtonText: '수정',
      cancelButtonText: '취소',
      confirmButtonColor: '#1a3a5c'
    })
    if (!content) return

    await qnaStore.updateComment(commentId, password, content)
    await qnaStore.fetchComments(route.params.id)
    Swal.fire({ icon: 'success', title: '수정 완료', timer: 1500, showConfirmButton: false })
  } catch {
    Swal.fire({ icon: 'error', title: '수정 실패', text: '댓글 수정에 실패했습니다.' })
  }
}

async function handleDeleteComment(commentId) {
  const isAdmin = authStore.isLoggedIn

  let password = ''
  if (!isAdmin) {
    const result = await Swal.fire({
      icon: 'warning',
      title: '댓글 삭제',
      input: 'password',
      inputPlaceholder: '비밀번호',
      showCancelButton: true,
      confirmButtonText: '삭제',
      cancelButtonText: '취소',
      confirmButtonColor: '#e74c3c'
    })
    if (!result.value) return
    password = result.value
  } else {
    const result = await Swal.fire({
      icon: 'warning',
      title: '댓글 삭제',
      text: '관리자 권한으로 삭제하시겠습니까?',
      showCancelButton: true,
      confirmButtonText: '삭제',
      cancelButtonText: '취소',
      confirmButtonColor: '#e74c3c'
    })
    if (!result.isConfirmed) return
  }

  try {
    await qnaStore.deleteComment(commentId, password)
    await qnaStore.fetchComments(route.params.id)
    Swal.fire({ icon: 'success', title: '삭제 완료', timer: 1500, showConfirmButton: false })
  } catch {
    Swal.fire({ icon: 'error', title: '삭제 실패', text: '비밀번호가 일치하지 않습니다.' })
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

.post-card {
  background: var(--white);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
}

.post-header {
  padding: 32px 32px 20px;
  border-bottom: 1px solid var(--border);
}

.post-title {
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--text-dark);
  margin-bottom: 12px;
}

.post-meta {
  display: flex;
  gap: 20px;
  font-size: 0.85rem;
  color: var(--text-muted);
}

.post-meta i {
  margin-right: 4px;
}

.post-body {
  padding: 32px;
  min-height: 200px;
  font-size: 0.95rem;
  line-height: 1.8;
  color: var(--text-body);
}

.post-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 32px;
  border-top: 1px solid var(--border);
  background: var(--bg-light);
}

.post-actions-right {
  display: flex;
  gap: 8px;
}

.btn-outline {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 20px;
  background: var(--white);
  color: var(--text-body);
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  font-size: 0.85rem;
  cursor: pointer;
  transition: var(--transition);
  text-decoration: none;
}

.btn-outline:hover {
  border-color: var(--primary);
  color: var(--primary);
}

.btn-danger {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 20px;
  background: var(--white);
  color: #e74c3c;
  border: 1px solid #e74c3c;
  border-radius: var(--radius-sm);
  font-size: 0.85rem;
  cursor: pointer;
  transition: var(--transition);
}

.btn-danger:hover {
  background: #e74c3c;
  color: var(--white);
}

/* 댓글 영역 */
.comments-section {
  margin-top: 32px;
}

.comments-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: var(--text-dark);
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.comments-count {
  background: var(--primary);
  color: var(--white);
  font-size: 0.75rem;
  padding: 2px 10px;
  border-radius: 20px;
}

.comment-list {
  background: var(--white);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
}

.comments-empty {
  padding: 40px;
  text-align: center;
  color: var(--text-muted);
  font-size: 0.9rem;
}

.comment-form-card {
  margin-top: 24px;
  background: var(--white);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-sm);
  padding: 24px;
}

.comment-form-title {
  font-size: 1rem;
  font-weight: 600;
  color: var(--text-dark);
  margin-bottom: 16px;
}

.comment-form-row {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
}

.input-field {
  width: 100%;
  padding: 10px 14px;
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  font-size: 0.9rem;
  font-family: inherit;
  transition: var(--transition);
}

.input-field:focus {
  outline: none;
  border-color: var(--primary);
  box-shadow: 0 0 0 3px rgba(26, 58, 92, 0.1);
}

.input-sm {
  max-width: 200px;
}

.textarea-field {
  resize: vertical;
  min-height: 80px;
}

.comment-form-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
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

.btn-sm {
  padding: 8px 18px;
  font-size: 0.85rem;
}

@media (max-width: 768px) {
  .page-hero { height: 200px; }
  .page-hero-title { font-size: 1.6rem; }
  .post-header, .post-body, .post-actions { padding: 20px; }
  .post-title { font-size: 1.2rem; }
  .post-meta { flex-wrap: wrap; gap: 12px; }
  .post-actions { flex-direction: column; gap: 12px; }
  .post-actions-right { width: 100%; justify-content: flex-end; }
  .comment-form-row { flex-direction: column; }
  .input-sm { max-width: 100%; }
}
</style>
