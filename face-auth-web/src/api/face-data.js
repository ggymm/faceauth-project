import request from './req'

const baseUrl = '/v1/face/data/'

export const faceDataApi = {
  getPage(params) {
    return request({
      url: baseUrl + 'getPage',
      method: 'get',
      params
    })
  },

  createData(data) {
    return request.post(baseUrl + 'createData', data, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  updateData(data) {
    return request.post(baseUrl + 'updateData', data, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  }
}
