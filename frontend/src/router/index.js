import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/HomeView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/qna',
      name: 'qna-list',
      component: () => import('@/views/QnaListView.vue'),
    },
    {
      path: '/qna/write',
      name: 'qna-write',
      component: () => import('@/views/QnaWriteView.vue'),
    },
    {
      path: '/qna/:id',
      name: 'qna-detail',
      component: () => import('@/views/QnaDetailView.vue'),
    },
    {
      path: '/qna/:id/edit',
      name: 'qna-edit',
      component: () => import('@/views/QnaWriteView.vue'),
    },
    {
      path: '/references',
      name: 'references',
      component: () => import('@/views/ReferenceListView.vue'),
    },
    {
      path: '/references/write',
      name: 'reference-write',
      component: () => import('@/views/ReferenceWriteView.vue'),
    },
    {
      path: '/references/:id',
      name: 'reference-detail',
      component: () => import('@/views/ReferenceDetailView.vue'),
    },
  ],
  scrollBehavior(to, from, savedPosition) {
    if (to.hash) {
      return { el: to.hash, behavior: 'smooth' }
    }
    if (savedPosition) {
      return savedPosition
    }
    return { top: 0 }
  },
})

export default router
