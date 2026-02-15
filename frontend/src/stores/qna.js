import { defineStore } from 'pinia'
import { ref } from 'vue'
import api from '@/api'

export const useQnaStore = defineStore('qna', () => {
  const posts = ref([])
  const currentPost = ref(null)
  const comments = ref([])
  const totalPages = ref(0)
  const totalElements = ref(0)
  const currentPage = ref(0)

  async function fetchPosts(page = 0, size = 10) {
    const { data } = await api.get('/qna', { params: { page, size } })
    posts.value = data.content
    totalPages.value = data.totalPages
    totalElements.value = data.totalElements
    currentPage.value = data.number
  }

  async function searchPosts(keyword, page = 0, size = 15) {
    const { data } = await api.get('/qna/search', { params: { keyword, page, size } })
    posts.value = data.content
    totalPages.value = data.totalPages
    totalElements.value = data.totalElements
    currentPage.value = data.number
  }

  async function fetchPost(id) {
    const { data } = await api.get(`/qna/${id}`)
    currentPost.value = data
    return data
  }

  async function createPost(payload) {
    const { data } = await api.post('/qna', payload)
    return data
  }

  async function checkPostPassword(id, password) {
    const { data } = await api.post(`/qna/${id}/check-password`, { password })
    return data.valid
  }

  async function updatePost(id, payload) {
    const { data } = await api.put(`/qna/${id}`, payload)
    return data
  }

  async function deletePost(id, password) {
    await api.delete(`/qna/${id}`, { data: { password } })
  }

  async function fetchComments(postId) {
    const { data } = await api.get(`/qna/${postId}/comments`)
    comments.value = data
    return data
  }

  async function createComment(postId, payload) {
    const { data } = await api.post(`/qna/${postId}/comments`, payload)
    return data
  }

  async function checkCommentPassword(commentId, password) {
    const { data } = await api.post(`/qna/comments/${commentId}/check-password`, { password })
    return data.valid
  }

  async function updateComment(commentId, password, content) {
    const { data } = await api.put(`/qna/comments/${commentId}`, { password, content })
    return data
  }

  async function deleteComment(commentId, password) {
    await api.delete(`/qna/comments/${commentId}`, { data: { password } })
  }

  return {
    posts, currentPost, comments, totalPages, totalElements, currentPage,
    fetchPosts, searchPosts, fetchPost, createPost, checkPostPassword, updatePost, deletePost,
    fetchComments, createComment, checkCommentPassword, updateComment, deleteComment
  }
})
