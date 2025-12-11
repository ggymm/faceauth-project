import { resolve } from 'path'

import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'

import { NaiveUiResolver } from 'unplugin-vue-components/resolvers'
import { createSvgIconsPlugin } from 'vite-plugin-svg-icons'

import Icons from 'unplugin-icons/vite'
import IconsResolver from 'unplugin-icons/resolver'
import { FileSystemIconLoader } from 'unplugin-icons/loaders'

import { getSrcPath } from '../utils'

const iconPath = resolve(getSrcPath(), 'assets/svg')

export default [
  AutoImport({
    imports: ['vue', 'vue-router'],
    dts: false
  }),
  Icons({
    compiler: 'vue3',
    customCollections: {
      custom: FileSystemIconLoader(iconPath)
    },
    scale: 1,
    defaultClass: 'inline-block'
  }),
  Components({
    resolvers: [NaiveUiResolver(), IconsResolver({ componentPrefix: 'icon' })],
    dts: false
  }),
  createSvgIconsPlugin({
    iconDirs: [iconPath],
    symbolId: 'icon-[dir]-[name]',
    inject: 'body-last',
    customDomId: '__CUSTOM_SVG_ICON__'
  })
]
