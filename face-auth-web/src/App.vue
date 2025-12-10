<script setup>
import { NConfigProvider, NMessageProvider, NDialogProvider } from 'naive-ui'
import FaceDataList from './components/FaceDataList.vue'
</script>

<template>
  <n-config-provider>
    <n-message-provider>
      <n-dialog-provider>
        <div style="padding: 20px">
          <FaceDataList />
        </div>
      </n-dialog-provider>
    </n-message-provider>
  </n-config-provider>
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}
</style>
