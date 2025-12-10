import request from './req'

export const faceDataApi = {
  // 获取所有人脸数据
  getAll() {
    return request({
      url: '/face/data',
      method: 'get'
    })
  },

  // 分页获取人脸数据
  getPage(pageNum, pageSize) {
    return request({
      url: '/face/data/page',
      method: 'get',
      params: { pageNum, pageSize }
    })
  },

  // 根据ID获取人脸数据
  getById(id) {
    return request({
      url: `/face/data/${id}`,
      method: 'get'
    })
  },

  // 创建人脸数据
  create(data) {
    return request({
      url: '/face/data/create',
      method: 'post',
      data
    })
  },

  // 更新人脸数据
  update(data) {
    return request({
      url: '/face/data/update',
      method: 'post',
      data
    })
  },

  // 删除人脸数据
  delete(id) {
    return request({
      url: '/face/data/delete',
      method: 'post',
      data: { id }
    })
  }
}

// 人脸注册 API
export const faceRegisterApi = {
  register(data) {
    return request({
      url: '/face/register',
      method: 'post',
      data
    })
  }
}

// 人脸验证 API
export const faceVerifyApi = {
  verify(data) {
    return request({
      url: '/face/verify',
      method: 'post',
      data
    })
  }
}
