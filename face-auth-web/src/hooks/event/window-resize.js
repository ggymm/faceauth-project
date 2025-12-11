import { tryOnMounted, tryOnUnmounted, useDebounceFn } from '@vueuse/core'

export function useWindowResize(fn, wait = 150) {
  let handler = () => {
    fn()
  }

  handler = useDebounceFn(handler, wait)

  const start = () => {
    window.addEventListener('resize', handler)
  }

  const stop = () => {
    window.removeEventListener('resize', handler)
  }

  tryOnMounted(() => {
    start()
  })

  tryOnUnmounted(() => {
    stop()
  })

  return [start, stop]
}
