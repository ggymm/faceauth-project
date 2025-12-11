<template>
  <router-view v-slot="{ Component, route }">
    <KeepAlive :include="keepAliveNames">
      <component :is="Component" :key="route.fullPath" />
    </KeepAlive>
  </router-view>
</template>

<script setup>
const keepAliveNames = computed(() => {
  return []
})
</script>
