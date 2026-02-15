<template>
  <div class="page-wrapper">
    <div class="page-hero">
      <div class="page-hero-overlay"></div>
      <div class="page-hero-content">
        <p class="page-hero-subtitle">Community</p>
        <h1 class="page-hero-title">{{ isEdit ? '게시글 수정' : '게시글 작성' }}</h1>
        <div class="page-hero-bar"></div>
      </div>
    </div>

    <div class="page-container">
      <div class="form-card">
        <div class="form-group">
          <label class="form-label">작성자</label>
          <input v-model="form.authorName" type="text" class="form-input" placeholder="이름을 입력하세요" :disabled="isAdmin" />
        </div>
        <div class="form-group" v-if="!isEdit && !isAdmin">
          <label class="form-label">비밀번호</label>
          <input v-model="form.password" type="password" class="form-input" placeholder="수정/삭제 시 필요합니다" />
        </div>
        <div v-if="isAdmin && !isEdit" class="admin-notice">
          <i class="fas fa-shield-alt"></i> 관리자 모드 — 비밀번호 없이 작성됩니다.
        </div>
        <div v-if="isAdmin" class="form-group notice-check-group">
          <label class="notice-check-label">
            <input v-model="form.isNotice" type="checkbox" class="notice-checkbox" />
            <span class="notice-check-text"><i class="fas fa-bullhorn"></i> 공지로 등록</span>
          </label>
        </div>
        <div class="form-group">
          <label class="form-label">제목</label>
          <input v-model="form.title" type="text" class="form-input" placeholder="제목을 입력하세요" />
        </div>
        <div class="form-group">
          <label class="form-label">내용</label>
          <textarea v-model="form.content" class="form-input form-textarea" placeholder="내용을 입력하세요" rows="12"></textarea>
        </div>

        <div class="form-actions">
          <router-link :to="isEdit ? `/qna/${route.params.id}` : '/qna'" class="btn-outline">
            <i class="fas fa-times"></i> 취소
          </router-link>
          <button class="btn-primary" @click="handleSubmit">
            <i class="fas fa-check"></i> {{ isEdit ? '수정' : '등록' }}
          </button>
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

const route = useRoute()
const router = useRouter()
const qnaStore = useQnaStore()
const authStore = useAuthStore()
const { isLoggedIn: isAdmin } = storeToRefs(authStore)

const isEdit = computed(() => !!route.params.id)
const isAdminEdit = computed(() => route.query.admin === '1')

const form = ref({
  authorName: '',
  password: '',
  title: '',
  content: '',
  isNotice: false
})

onMounted(async () => {
  await authStore.checkLogin()

  if (isAdmin.value && !isEdit.value) {
    form.value.authorName = '관리자'
    form.value.password = 'admin'
  }

  if (isEdit.value) {
    const post = await qnaStore.fetchPost(route.params.id)
    form.value.authorName = post.authorName
    form.value.title = post.title
    form.value.content = post.content
    form.value.password = isAdminEdit.value ? 'admin' : (route.query.pw || '')
    form.value.isNotice = post.isNotice || false
  }
})

async function handleSubmit() {
  if (!form.value.authorName || !form.value.title || !form.value.content) {
    Swal.fire({ icon: 'warning', title: '입력 필요', text: '작성자, 제목, 내용을 모두 입력해주세요.' })
    return
  }
  if (!isEdit.value && !isAdmin.value && !form.value.password) {
    Swal.fire({ icon: 'warning', title: '입력 필요', text: '비밀번호를 입력해주세요.' })
    return
  }

  try {
    if (isEdit.value) {
      await qnaStore.updatePost(route.params.id, form.value)
      Swal.fire({ icon: 'success', title: '수정 완료', text: '게시글이 수정되었습니다.', timer: 1500, showConfirmButton: false })
      router.push(`/qna/${route.params.id}`)
    } else {
      const data = await qnaStore.createPost(form.value)
      Swal.fire({ icon: 'success', title: '등록 완료', text: '게시글이 등록되었습니다.', timer: 1500, showConfirmButton: false })
      router.push(`/qna/${data.id}`)
    }
  } catch {
    Swal.fire({ icon: 'error', title: '오류', text: '저장에 실패했습니다. 다시 시도해주세요.' })
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
  max-width: 800px;
  margin: 0 auto;
  padding: 40px 24px 80px;
}

.form-card {
  background: var(--white);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-sm);
  padding: 40px;
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

.form-input {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  font-size: 0.95rem;
  font-family: inherit;
  transition: var(--transition);
  box-sizing: border-box;
}

.form-input:focus {
  outline: none;
  border-color: var(--primary);
  box-shadow: 0 0 0 3px rgba(26, 58, 92, 0.1);
}

.form-input:disabled {
  background: var(--bg-light);
  color: var(--primary);
  font-weight: 500;
}

.form-textarea {
  resize: vertical;
  min-height: 240px;
  line-height: 1.7;
}

.admin-notice {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: rgba(26, 58, 92, 0.06);
  border-radius: var(--radius-sm);
  color: var(--primary);
  font-size: 0.85rem;
  font-weight: 500;
  margin-bottom: 24px;
}

.notice-check-group {
  margin-bottom: 20px;
}

.notice-check-label {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  user-select: none;
}

.notice-checkbox {
  width: 18px;
  height: 18px;
  accent-color: #f9a825;
  cursor: pointer;
}

.notice-check-text {
  font-size: 0.9rem;
  font-weight: 600;
  color: #e65100;
}

.notice-check-text i {
  margin-right: 4px;
}

.form-actions {
  display: flex;
  justify-content: center;
  gap: 12px;
  padding-top: 16px;
}

.btn-outline {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 12px 32px;
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

.btn-primary {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 12px 32px;
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

@media (max-width: 768px) {
  .page-hero { height: 200px; }
  .page-hero-title { font-size: 1.6rem; }
  .form-card { padding: 24px; }
}
</style>
