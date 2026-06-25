import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  { path: '/login', component: () => import('@/layouts/AuthLayout.vue'),
    children: [{ path: '', name: 'Login', component: () => import('@/views/auth/Login.vue') }] },
  { path: '/register', component: () => import('@/layouts/AuthLayout.vue'),
    children: [{ path: '', name: 'Register', component: () => import('@/views/auth/Register.vue') }] },
  { path: '/role-select', name: 'RoleSelect', component: () => import('@/views/auth/RoleSelect.vue'), meta: { requiresAuth: true } },
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      { path: '', name: 'Home', component: () => import('@/views/home/Home.vue') },
      { path: 'photos', name: 'Photos', component: () => import('@/views/media/PhotoWall.vue') },
      { path: 'videos', name: 'Videos', component: () => import('@/views/media/VideoList.vue') },
      { path: 'diary', name: 'DiaryList', component: () => import('@/views/diary/Diary.vue') },
      { path: 'diary/create', name: 'DiaryCreate', component: () => import('@/views/diary/DiaryEdit.vue') },
      { path: 'diary/:id', name: 'DiaryDetail', component: () => import('@/views/diary/DiaryDetail.vue') },
      { path: 'diary/:id/edit', name: 'DiaryEdit', component: () => import('@/views/diary/DiaryEdit.vue') },
      { path: 'profile', name: 'Profile', component: () => import('@/views/profile/Profile.vue') },
      { path: 'admin', name: 'Admin', component: () => import('@/views/admin/AdminPanel.vue'), meta: { requiresAdmin: true } }
    ]
  },
  { path: '/:pathMatch(.*)*', redirect: '/' }
]

const router = createRouter({ history: createWebHistory(), routes, scrollBehavior: () => ({ top: 0 }) })

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  document.title = to.meta.title ? `${to.meta.title} - 汐汐的小窝 🌸` : '汐汐的小窝 🌸'
  if (to.meta.requiresAuth && !userStore.isLoggedIn) { next('/login'); return }
  if (to.meta.requiresAdmin && !userStore.isAdmin) { next('/'); return }
  if (userStore.isLoggedIn && userStore.isFirstLogin && to.name !== 'RoleSelect' && to.name !== 'Login') { next('/role-select'); return }
  if (userStore.isLoggedIn && (to.name === 'Login' || to.name === 'Register')) { next('/'); return }
  next()
})

export default router
