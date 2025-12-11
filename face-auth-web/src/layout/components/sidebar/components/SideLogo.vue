<template>
  <router-link h-60 f-c-c to="/">
    <h2 v-show="!appStore.collapsed" ml-10 max-w-140 flex-shrink-0 text-16 font-bold color-primary>管理平台</h2>
  </router-link>
</template>

<script setup>
import { useAppStore } from '@/store'

const appStore = useAppStore()
</script>
