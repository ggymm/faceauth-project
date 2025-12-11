<template>
  <n-config-provider wh-full :locale="zhCN" :date-locale="dateZhCN" :theme-overrides="getThemeOverrides">
    <slot />
  </n-config-provider>
</template>

<script setup>
import { zhCN, dateZhCN } from 'naive-ui'

import setting from '@/setting'
import { lighten } from '@/utils'

const getThemeOverrides = computed(() => {
  const appTheme = setting.appTheme
  const lightenStr = lighten(setting.appTheme, 6)
  return {
    common: {
      primaryColor: appTheme,
      primaryColorHover: lightenStr,
      primaryColorPressed: lightenStr
    },
    LoadingBar: {
      colorLoading: appTheme
    }
  }
})
</script>
