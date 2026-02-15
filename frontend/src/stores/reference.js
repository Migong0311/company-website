import { defineStore } from 'pinia'
import { ref } from 'vue'
import api from '@/api'

export const useReferenceStore = defineStore('reference', () => {
  const categories = ref([])
  const references = ref([])
  const totalPages = ref(0)
  const totalElements = ref(0)
  const currentPage = ref(0)

  async function fetchCategories() {
    const { data } = await api.get('/reference-categories')
    categories.value = data
    return data
  }

  async function createCategory(payload) {
    const { data } = await api.post('/reference-categories', payload)
    return data
  }

  async function updateCategory(id, payload) {
    const { data } = await api.put(`/reference-categories/${id}`, payload)
    return data
  }

  async function deleteCategory(id) {
    await api.delete(`/reference-categories/${id}`)
  }

  async function fetchReferences(page = 0, size = 10) {
    const { data } = await api.get('/references', { params: { page, size } })
    references.value = data.content
    totalPages.value = data.totalPages
    totalElements.value = data.totalElements
    currentPage.value = data.number
  }

  async function fetchByCategory(categoryId, page = 0, size = 10) {
    const { data } = await api.get(`/references/category/${categoryId}`, { params: { page, size } })
    references.value = data.content
    totalPages.value = data.totalPages
    totalElements.value = data.totalElements
    currentPage.value = data.number
  }

  async function fetchReference(id) {
    const { data } = await api.get(`/references/${id}`)
    return data
  }

  async function createReference(formData) {
    const { data } = await api.post('/references', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    return data
  }

  async function deleteReference(id) {
    await api.delete(`/references/${id}`)
  }

  function getDownloadUrl(id) {
    return `/api/references/${id}/download`
  }

  return {
    categories, references, totalPages, totalElements, currentPage,
    fetchCategories, createCategory, updateCategory, deleteCategory,
    fetchReferences, fetchByCategory, fetchReference, createReference, deleteReference,
    getDownloadUrl
  }
})
