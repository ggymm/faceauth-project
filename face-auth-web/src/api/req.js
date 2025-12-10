import axios from 'axios'

import { getEnv } from '@/utils'

axios.defaults.withCredentials = true

const { VITE_API_BASE_URL } = getEnv()
const service = axios.create({
  headers: { 'Content-Type': 'application/json' },
  baseURL: VITE_API_BASE_URL,
  timeout: 1800000
})

service.interceptors.request.use(
  (config) => {
    return config
  },
  (error) => {
    console.log(error) // for debug
    return Promise.reject(error)
  }
)

service.interceptors.response.use(
  (response) => {
    /*const { data, message, success } = response.data
    if (!success) {
      window.$message.error(message)
      return Promise.reject(message)
    }*/
    return response.data
  },
  (error) => {
    return Promise.reject(error)
  }
)

export default service
