import { createRouter, createWebHashHistory } from 'vue-router'

import { setupRouterGuard } from './guard'
import { basicRoutes } from './routes/index.js'

import { usePermissionStore } from '@/store/index.js'

export const router = createRouter({
  history: createWebHashHistory('/'),
  routes: basicRoutes,
  scrollBehavior: () => ({ left: 0, top: 0 })
})

export async function setupRouter(app) {
  await addDynamicRoutes()
  setupRouterGuard(router)
  app.use(router)
}

export async function addDynamicRoutes() {
  const permissionStore = usePermissionStore()
  const accessRoutes = permissionStore.generateRoutes()
  accessRoutes.forEach((route) => {
    !router.hasRoute(route.name) && router.addRoute(route)
  })
}
