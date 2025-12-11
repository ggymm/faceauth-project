import { defineConfig, loadEnv } from 'vite'

import { getSrcPath } from './vite/utils'
import { createVitePlugins } from './vite/plugin'

export default defineConfig(({ mode }) => {
  loadEnv(mode, process.cwd())

  return {
    server: {
      port: 3000,
      host: '0.0.0.0'
    },
    resolve: {
      alias: {
        '@': getSrcPath()
      }
    },
    plugins: createVitePlugins(),
    build: {
      outDir: 'dist',
      target: 'chrome86',
      reportCompressedSize: false,
      chunkSizeWarningLimit: 1024
    }
  }
})
