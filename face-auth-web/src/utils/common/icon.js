import { h } from 'vue'

import { NIcon } from 'naive-ui'

import SvgIcon from '@/components/icon/SvgIcon.vue'

export function renderIcon(icon, props = { size: 12 }) {
  return () => h(NIcon, props, { default: () => h(SvgIcon, { icon }) })
}
