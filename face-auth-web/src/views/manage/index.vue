<script setup>
import { NButton, NSpace } from 'naive-ui'

import { faceDataApi } from '@/api/face-data.js'
import { useWindowResize } from '@/hooks/index.js'

const columns = [
  {
    key: 'userId',
    align: 'center',
    title: '用户ID',
    width: 240
  },
  {
    key: 'updateTime',
    align: 'center',
    title: '更新时间',
    width: 200
  },
  {
    title: '操作',
    key: 'actions',
    width: 200,
    fixed: 'right',
    align: 'center',
    render(row) {
      return h(
        NSpace,
        { justify: 'center' },
        {
          default: () => [
            h(
              NButton,
              {
                size: 'small',
                type: 'primary',
                onClick: async () => {}
              },
              { default: () => '编辑' }
            ),
            h(
              NButton,
              {
                size: 'small',
                type: 'error',
                onClick: async () => {}
              },
              { default: () => '删除' }
            )
          ]
        }
      )
    }
  }
]

const dataShow = ref(false)
const dataModel = ref({
  id: null,
  userId: null,
  faceImage: null
})
const dataLoading = ref(false)

const saveData = async () => {
  dataLoading.value = true
  const formData = new FormData()
  formData.append('userId', dataModel.value.userId)
  formData.append('faceImage', dataModel.value.faceImage)

  const { data, success, message } = await faceDataApi.createData(formData)
  dataShow.value = false
  dataLoading.value = false
  if (success) {
    console.log(data)
  } else {
    window['$message'].error(message)
    console.error(message)
  }
}

const handleCreate = () => {
  dataShow.value = true
}

const handleChooseImage = (list) => {
  if (list.length > 0) {
    dataModel.value.faceImage = list[0]['file']
  }
}

const tableModel = ref([])
const tableQuery = ref({
  userId: null
})
const tableLoading = ref(false)

const tablePagination = reactive({
  page: 1,
  pageSize: 50,
  showSizePicker: true,
  pageSizes: [50, 100, 200, 500],
  itemCount: 0
})

const fetchTable = async () => {
  tableLoading.value = true
  const { data, success, message } = await faceDataApi.getPage({
    ...toRaw(tableQuery.value),
    page: tablePagination.page,
    size: tablePagination.pageSize
  })
  tableLoading.value = false
  if (success) {
    const { total, records } = data
    tableModel.value = records
    tablePagination.itemCount = total
  } else {
    window['$message'].error(message)
    console.error(message)
  }
}

const handleSearch = async () => {
  tablePagination.page = 1
  await fetchTable()
}

const handleTablePageChange = (page) => {
  tablePagination.page = page
  fetchTable()
}

const handleTablePageSizeChange = (pageSize) => {
  tablePagination.page = 1
  tablePagination.pageSize = pageSize
  fetchTable()
}

const height = ref(200)
const container = ref()
const tableContainer = ref()

const resetTableHeight = () => {
  if (!container.value || !tableContainer.value) return
  const padding = 40
  const { top: containerTop } = container.value.getBoundingClientRect()
  const { top: tableContainerTop } = tableContainer.value.getBoundingClientRect()
  height.value = container.value.clientHeight - (tableContainerTop - containerTop) - padding
}
useWindowResize(resetTableHeight, 0)

onMounted(() => {
  fetchTable()

  nextTick(() => {
    resetTableHeight()
  })
})
</script>

<template>
  <div ref="container" flex-row flex-gap-20 wh-full overflow-auto p-20>
    <div flex-col wh-full min-w-1080 p-20 bg-white>
      <div flex-row flex-justify-between pb-20>
        <div flex-row flex-gap-20>
          <n-button type="primary" @click="handleCreate()">创建</n-button>
        </div>
        <div flex-row flex-gap-20 w-1200>
          <n-input flex-1 v-model:value="tableQuery.userId" type="text" clearable :input-props="{ 'data-1p-ignore': true }" />
          <n-button type="primary" @click="handleSearch()">查找</n-button>
        </div>
      </div>
      <div ref="tableContainer">
        <n-data-table
          remote
          flex-height
          :data="tableModel"
          :columns="columns"
          :pagination="tablePagination"
          :loading="tableLoading"
          :style="{ width: '100%', height: `${height}px` }"
          @update:page="handleTablePageChange"
          @update:pageSize="handleTablePageSizeChange"
        />
      </div>
    </div>
    <n-modal v-model:show="dataShow" title="创建数据" preset="dialog" :auto-focus="false" :mask-closable="false" style="width: 80%">
      <div flex-col pt-24>
        <n-form-item label="用户ID" :label-width="200" label-placement="left">
          <n-input v-model:value="dataModel.userId"></n-input>
        </n-form-item>
        <n-form-item label="图片数据" :label-width="200" label-placement="left">
          <n-upload :max="1" list-type="image-card" :default-upload="false" file-list-class="face-image" @update:fileList="handleChooseImage" />
        </n-form-item>
      </div>
      <template #action>
        <n-button type="primary" @click="saveData" :loading="dataLoading">保存</n-button>
      </template>
    </n-modal>
  </div>
</template>

<style lang="scss">
.face-image {
  .n-upload-file {
    width: 200px !important;
    height: 200px !important;
  }
  .n-upload-trigger {
    width: 200px !important;
    height: 200px !important;
  }
}
</style>
