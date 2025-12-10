<script setup>
import { ref, h, onMounted } from 'vue'
import { NButton, NSpace, NDataTable, NModal, NForm, NFormItem, NInput, NUpload, useMessage, useDialog } from 'naive-ui'
import { faceDataApi } from '../api/face-data.js'

const message = useMessage()
const dialog = useDialog()

const faceDataList = ref([])
const loading = ref(false)
const showModal = ref(false)
const isEdit = ref(false)
const modalTitle = ref('新增人脸数据')
const formRef = ref(null)
const fileList = ref([])
const imagePreview = ref('')

const formData = ref({
  id: null,
  userId: '',
  faceImageBase64: null
})

const pagination = {
  pageSize: 10
}

const rules = {
  userId: {
    required: true,
    message: '请输入用户ID',
    trigger: 'blur'
  },
  faceImage: {
    required: true,
    message: '请上传人脸图片',
    trigger: 'change'
  }
}

const columns = [
  {
    title: 'ID',
    key: 'id',
    width: 80
  },
  {
    title: '用户ID',
    key: 'userId',
    width: 150
  },
  {
    title: '人脸图片',
    key: 'imageUrl',
    width: 150,
    render(row) {
      return h('img', {
        src: row.imageUrl,
        style: 'width: 60px; height: 60px; object-fit: cover; cursor: pointer',
        onClick: () => {
          dialog.info({
            title: '人脸图片预览',
            content: () => h('img', { src: row.imageUrl, style: 'width: 100%; max-width: 400px' })
          })
        }
      })
    }
  },
  {
    title: '创建时间',
    key: 'createTime',
    width: 180
  },
  {
    title: '操作',
    key: 'actions',
    width: 200,
    render(row) {
      return h(NSpace, null, {
        default: () => [
          h(
            NButton,
            {
              size: 'small',
              type: 'info',
              onClick: () => handleEdit(row)
            },
            { default: () => '编辑' }
          ),
          h(
            NButton,
            {
              size: 'small',
              type: 'error',
              onClick: () => handleDelete(row)
            },
            { default: () => '删除' }
          )
        ]
      })
    }
  }
]

const loadData = async () => {
  loading.value = true
  try {
    const response = await faceDataApi.getAll()
    faceDataList.value = response
  } catch (error) {
    message.error('加载数据失败：' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  isEdit.value = false
  modalTitle.value = '新增人脸数据'
  formData.value = {
    id: null,
    userId: '',
    faceImageBase64: null
  }
  fileList.value = []
  imagePreview.value = ''
  showModal.value = true
}

const handleEdit = async (row) => {
  isEdit.value = true
  modalTitle.value = '编辑人脸数据'
  formData.value = {
    id: row.id,
    userId: row.userId,
    faceImageBase64: null
  }

  if (row.imageUrl) {
    imagePreview.value = row.imageUrl
    fileList.value = [
      {
        id: 'current',
        name: '当前图片',
        status: 'finished',
        url: row.imageUrl
      }
    ]
  }

  showModal.value = true
}

const handleDelete = (row) => {
  dialog.warning({
    title: '确认删除',
    content: `确定要删除用户ID为 ${row.userId} 的人脸数据吗？`,
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        await faceDataApi.delete(row.id)
        message.success('删除成功')
        loadData()
      } catch (error) {
        message.error('删除失败：' + (error.response?.data?.message || error.message))
      }
    }
  })
}

const handleBeforeUpload = ({ file }) => {
  const reader = new FileReader()
  reader.onload = (e) => {
    imagePreview.value = e.target.result
    formData.value.faceImageBase64 = e.target.result
  }
  reader.readAsDataURL(file.file)

  return false
}

const handleRemove = () => {
  formData.value.faceImageBase64 = null
  imagePreview.value = ''
  fileList.value = []
}

const handleSubmit = async () => {
  try {
    await formRef.value?.validate()

    const submitData = {
      userId: formData.value.userId,
      faceImageBase64: formData.value.faceImageBase64
    }

    if (isEdit.value) {
      submitData.id = formData.value.id
      await faceDataApi.update(submitData)
      message.success('更新成功')
    } else {
      await faceDataApi.create(submitData)
      message.success('创建成功')
    }

    showModal.value = false
    loadData()
  } catch (error) {
    if (error?.errors) {
      return false
    }
    message.error('操作失败：' + (error.message || '未知错误'))
    return false
  }
}

onMounted(() => {
  loadData()
})
</script>

<template>
  <n-space vertical :size="20">
    <n-space justify="space-between">
      <h1>人脸数据管理</h1>
      <n-button type="primary" @click="handleAdd">新增</n-button>
    </n-space>

    <n-data-table :columns="columns" :data="faceDataList" :loading="loading" :pagination="pagination" />

    <n-modal
      v-model:show="showModal"
      :title="modalTitle"
      preset="dialog"
      :positive-text="isEdit ? '更新' : '创建'"
      negative-text="取消"
      @positive-click="handleSubmit"
    >
      <n-form ref="formRef" :model="formData" :rules="rules" label-placement="left" label-width="100px" style="margin-top: 20px">
        <n-form-item label="用户ID" path="userId">
          <n-input v-model:value="formData.userId" placeholder="请输入用户ID" :disabled="isEdit" />
        </n-form-item>

        <n-form-item label="人脸图片" path="faceImage">
          <n-upload
            :max="1"
            :default-file-list="fileList"
            accept="image/*"
            list-type="image-card"
            @before-upload="handleBeforeUpload"
            @remove="handleRemove"
          >
            点击上传
          </n-upload>
        </n-form-item>

        <n-form-item label="预览">
          <img v-if="imagePreview" :src="imagePreview" style="max-width: 200px; max-height: 200px" />
        </n-form-item>
      </n-form>
    </n-modal>
  </n-space>
</template>

<style scoped>
h1 {
  margin: 0;
  font-size: 24px;
}
</style>
