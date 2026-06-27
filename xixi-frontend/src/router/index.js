import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  { path: '/login',       component: () => import('@/views/auth/Login.vue'),      meta: { guest: true } },
  { path: '/register',    component: () => import('@/views/auth/Register.vue'),   meta: { guest: true } },
  { path: '/role-select', component: () => import('@/views/auth/RoleSelect.vue'), meta: { auth: true } },
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    meta: { auth: true },
    children: [
      { path: '',               component: () => import('@/views/home/Home.vue') },
      { path: 'photos',         component: () => import('@/views/media/PhotoWall.vue') },
      { path: 'videos',         component: () => import('@/views/media/VideoList.vue') },
      { path: 'diary',          component: () => import('@/views/diary/DiaryList.vue') },
      { path: 'diary/create',   component: () => import('@/views/diary/DiaryEdit.vue') },
      { path: 'diary/:id',      component: () => import('@/views/diary/DiaryDetail.vue') },
      { path: 'diary/:id/edit', component: () => import('@/views/diary/DiaryEdit.vue') },
      { path: 'profile',        component: () => import('@/views/profile/Profile.vue') },
      { path: 'admin',          component: () => import('@/views/admin/AdminPanel.vue'), meta: { admin: true } },
    ]
  },
  { path: '/:pathMatch(.*)*', redirect: '/' }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ top: 0 })
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  userStore.initFromStorage()
  if (to.meta.auth && !userStore.isLoggedIn) return next('/login')
  if (to.meta.guest && userStore.isLoggedIn) return next('/')
  if (to.meta.admin && !userStore.isAdmin) return next('/')
  next()
})

export default router
