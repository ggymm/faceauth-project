import vue from '@vitejs/plugin-vue'

import Unocss from 'unocss/vite'

import unplugin from './unplugin'

export function createVitePlugins() {
  return [vue(), Unocss(), ...unplugin]
}
