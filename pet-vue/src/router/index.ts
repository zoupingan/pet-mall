import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import AdminLayout from '../layouts/AdminLayout.vue'
import { tokenManager } from '@/utils/token'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: LoginView,
      meta: { title: '登录' }
    },
    {
      path: '/register',
      name: 'register',
      component: RegisterView,
      meta: { title: '注册' }
    },
    {
      path: '/',
      component: AdminLayout,
      redirect: '/product/list',
      children: [
        {
          path: 'product/list',
          name: 'product-list',
          component: () => import('../views/product/ProductListView.vue'),
          meta: { title: '商品列表', requiresAuth: true }
        },
        {
          path: 'product/category',
          name: 'product-category',
          component: () => import('../views/product/CategoryView.vue'),
          meta: { title: '分类管理', requiresAuth: true }
        },
        {
          path: 'product/brand',
          name: 'product-brand',
          component: () => import('../views/product/BrandView.vue'),
          meta: { title: '品牌管理', requiresAuth: true }
        },
        {
          path: 'order',
          name: 'order',
          component: () => import('../views/order/OrderView.vue'),
          meta: { title: '订单管理', requiresAuth: true }
        },
        {
          path: 'content/banner',
          name: 'content-banner',
          component: () => import('../views/content/BannerView.vue'),
          meta: { title: '轮播图管理', requiresAuth: true }
        },
        {
          path: 'user',
          name: 'user',
          component: () => import('../views/user/UserView.vue'),
          meta: { title: '用户管理', requiresAuth: true }
        },
        {
          path: 'address',
          name: 'address',
          component: () => import('../views/address/AddressBookView.vue'),
          meta: { title: '地址管理', requiresAuth: true }
        },
        {
          path: 'profile',
          name: 'profile',
          component: () => import('../views/user/ProfileView.vue'),
          meta: { title: '个人中心', requiresAuth: true }
        }
      ]
    }
  ]
})

router.beforeEach((to, from, next) => {
  const isAuthenticated = tokenManager.hasToken()

  if (to.meta.requiresAuth && !isAuthenticated) {
    console.log('🔒 未登录，重定向到登录页')
    next('/login')
  } else if (to.path === '/login' && isAuthenticated) {
    console.log('✅ 已登录，重定向到首页')
    next('/')
  } else {
    next()
  }
})

export default router
