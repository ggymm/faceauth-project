<template>
  <n-icon size="20" cursor-pointer @click="appStore['switchCollapsed']">
    <svg-icon icon="format-indent-increase" v-if="appStore.collapsed" />
    <svg-icon icon="format-indent-decrease" v-if="!appStore.collapsed" />
  </n-icon>
</template>

<script setup>
import { useAppStore } from '@/store'

import SvgIcon from '@/components/icon/SvgIcon.vue'

const appStore = useAppStore()
</script>
