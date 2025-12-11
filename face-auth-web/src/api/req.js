import axios from 'axios'
import { getEnv } from '@/utils'

axios.defaults.withCredentials = true

const { VITE_API_BASE_URL } = getEnv()
const service = axios.create({
  headers: { 'Content-Type': 'application/json' },
  baseURL: VITE_API_BASE_URL,
  timeout: 300000
})

let dialog = false
const options = {
  closable: false,
  closeOnEsc: false,
  maskClosable: false
}
const showUnauthorized = () => {
  if (!dialog) {
    dialog = true
    window['$dialog'].warning({
      ...options,
      title: '警告',
      content: '用户身份验证失败，请重新登录',
      positiveText: '确定',
      onPositiveClick: () => {
        dialog = false
        window.location.href = '#/login'
      }
    })
  }
}

service.interceptors.request.use(
  (config) => {
    return config
  },
  (error) => {
    console.log('req error', error)
    return Promise.reject(error)
  }
)

service.interceptors.response.use(
  (response) => {
    const { status } = response
    if (status === 401) {
      showUnauthorized()
      return Promise.reject('Unauthorized')
    }
    return response.data
  },
  (error) => {
    console.log('resp error', error)
    const { response } = error
    if (response) {
      const { status } = response
      if (status === 401) {
        showUnauthorized()
        return Promise.reject('Unauthorized')
      }
    }
    return Promise.reject(error)
  }
)

export default service
