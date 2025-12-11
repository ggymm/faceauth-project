const baseTitle = '管理平台'

export function setupRouterGuard(router) {
  createPageTitleGuard(router)
  createPageLoadingGuard(router)
}

export function createPageTitleGuard(router) {
  router.afterEach((to) => {
    const pageTitle = to.meta['title']
    if (pageTitle) {
      document.title = `${pageTitle} | ${baseTitle}`
    } else {
      document.title = baseTitle
    }
  })
}

export function createPageLoadingGuard(router) {
  router.beforeEach(() => {
    window['$loadingBar'].start()
  })

  router.afterEach(() => {
    setTimeout(() => {
      window['$loadingBar'].finish()
    }, 200)
  })

  router.onError(() => {
    window['$loadingBar'].error()
  })
}
