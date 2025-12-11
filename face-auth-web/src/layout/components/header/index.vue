<template>
  <div flex items-center>
    <MenuCollapse />
    <BreadCrumb ml-15 hidden sm:block />
  </div>
  <div ml-auto flex items-center>
    <n-dropdown trigger="hover" :options="options" @select="handleSelect">
      <n-button quaternary>{{ account }}</n-button>
    </n-dropdown>
  </div>
</template>

<script setup>
import BreadCrumb from './components/BreadCrumb.vue'
import MenuCollapse from './components/MenuCollapse.vue'

const account = 'admin'
const options = [{ label: '退出登录', key: 'logout' }]

function handleSelect(key) {
  if (key === 'personal') {
    // TODO: 跳转到个人中心页面
  } else if (key === 'logout') {
    window['$message'].success('退出登录成功')
    window.location.href = '#/login'
  }
}
</script>
