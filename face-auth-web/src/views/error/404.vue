<script setup>
import { useRouter } from 'vue-router'
import AppPage from '@/components/app/AppPage.vue'

const { replace } = useRouter()
</script>
<template>
  <AppPage>
    <n-result m-auto status="404" description="抱歉，您访问的页面不存在。">
      <template #icon>
        <img src="@/assets/images/404.webp" width="500" alt="" />
      </template>
      <template #footer>
        <n-button @click="replace('/')">返回首页</n-button>
      </template>
    </n-result>
  </AppPage>
</template>
