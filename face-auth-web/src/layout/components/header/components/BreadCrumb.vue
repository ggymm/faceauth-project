<template>
  <n-breadcrumb>
    <n-breadcrumb-item
      v-for="item in route.matched.filter((t) => !!t.meta['title'])"
      :key="item.path"
      @click="handleBreadClick(item.path)"
    >
      <component :is="getIcon(item.meta)" />
      {{ item.meta['title'] }}
    </n-breadcrumb-item>
  </n-breadcrumb>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router'

import { renderIcon } from '@/utils'

const router = useRouter()
const route = useRoute()

function handleBreadClick(path) {
  if (path === route.path) return
  router.push(path)
}

function getIcon(meta) {
  if (meta?.icon) return renderIcon(meta.icon, { size: 18 })
  return null
}
</script>
