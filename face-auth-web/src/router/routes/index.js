const Layout = () => import('@/layout/index.vue')

export const basicRoutes = [
  {
    name: '404',
    path: '/404',
    component: () => import('@/views/error/404.vue'),
    isHidden: true
  },
  {
    name: 'Root',
    path: '/',
    component: Layout,
    redirect: '/manage',
    isHidden: true
  },
  {
    name: 'Manage',
    path: '/manage',
    component: Layout,
    redirect: '/manage/index',
    children: [
      {
        name: 'ManageIndex',
        path: 'index',
        component: () => import('@/views/manage/index.vue'),
        meta: {
          title: '数据管理',
          icon: 'home',
          order: 0
        }
      }
    ]
  },
  {
    name: 'Debug',
    path: '/debug',
    component: Layout,
    redirect: '/debug/index',
    children: [
      {
        name: 'DebugIndex',
        path: 'index',
        component: () => import('@/views/debug/index.vue'),
        meta: {
          title: '模型调试',
          icon: 'home',
          order: 0
        }
      }
    ]
  }
]

const asyncRoutes = []

export { asyncRoutes }
