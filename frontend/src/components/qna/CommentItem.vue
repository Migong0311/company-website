<template>
  <div class="comment-item" :style="{ paddingLeft: depth * 32 + 'px' }">
    <div class="comment-box" :class="{ 'is-admin': comment.isAdmin, 'is-reply': depth > 0 }">
      <div v-if="depth > 0" class="reply-indicator">
        <i class="fas fa-reply fa-flip-horizontal"></i>
      </div>
      <div class="comment-content">
        <div class="comment-header">
          <div class="comment-author">
            <span v-if="comment.isAdmin" class="admin-badge"><i class="fas fa-user-shield"></i> 관리자</span>
            <span v-else class="author-name">{{ comment.authorName }}</span>
          </div>
          <span class="comment-date">{{ formatDate(comment.createdAt) }}</span>
        </div>
        <p class="comment-text">{{ comment.content }}</p>
        <div class="comment-actions">
          <button class="comment-btn" @click="$emit('reply', comment.id)">
            <i class="fas fa-reply"></i> 답글
          </button>
          <template v-if="!comment.isAdmin || isCurrentUserAdmin">
            <button class="comment-btn" @click="$emit('edit', comment.id)">
              <i class="fas fa-edit"></i> 수정
            </button>
            <button class="comment-btn comment-btn-delete" @click="$emit('delete', comment.id)">
              <i class="fas fa-trash"></i> 삭제
            </button>
          </template>
        </div>
      </div>
    </div>
    <template v-if="comment.children?.length">
      <CommentItem
        v-for="child in comment.children"
        :key="child.id"
        :comment="child"
        :depth="depth + 1"
        :isCurrentUserAdmin="isCurrentUserAdmin"
        @reply="(id) => $emit('reply', id)"
        @edit="(id) => $emit('edit', id)"
        @delete="(id) => $emit('delete', id)"
      />
    </template>
  </div>
</template>

<script setup>
defineProps({
  comment: { type: Object, required: true },
  depth: { type: Number, default: 0 },
  isCurrentUserAdmin: { type: Boolean, default: false }
})

defineEmits(['reply', 'edit', 'delete'])

function formatDate(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return `${d.getFullYear()}.${String(d.getMonth() + 1).padStart(2, '0')}.${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}
</script>

<style scoped>
.comment-item {
  border-bottom: 1px solid var(--border);
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-box {
  display: flex;
  gap: 12px;
  padding: 16px 24px;
}

.comment-box.is-admin {
  background: rgba(26, 58, 92, 0.03);
}

.comment-box.is-reply {
  background: var(--bg-light);
}

.reply-indicator {
  color: var(--text-muted);
  font-size: 0.85rem;
  padding-top: 2px;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.comment-author {
  display: flex;
  align-items: center;
  gap: 8px;
}

.author-name {
  font-size: 0.9rem;
  font-weight: 600;
  color: var(--text-dark);
}

.admin-badge {
  font-size: 0.7rem;
  padding: 2px 8px;
  background: var(--primary);
  color: var(--white);
  border-radius: 10px;
}

.comment-date {
  font-size: 0.8rem;
  color: var(--text-muted);
}

.comment-text {
  font-size: 0.9rem;
  line-height: 1.6;
  color: var(--text-body);
  white-space: pre-wrap;
}

.comment-actions {
  display: flex;
  gap: 12px;
  margin-top: 10px;
}

.comment-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 0;
  background: none;
  border: none;
  font-size: 0.8rem;
  color: var(--text-muted);
  cursor: pointer;
  transition: var(--transition);
}

.comment-btn:hover {
  color: var(--primary);
}

.comment-btn-delete:hover {
  color: #e74c3c;
}

@media (max-width: 768px) {
  .comment-box { padding: 12px 16px; }
}
</style>
