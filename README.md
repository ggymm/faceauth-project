### 目标

实现人脸认证的简单demo

### 技术栈

#### 服务端
- Java Spring Boot
- MySQL

#### 浏览器
- Vue3 + Vite

#### 客户端
- Electron
- Javascript


### 功能需求

#### face-auth-web
在 face-auth-web 目录下创建前端工程
1. 使用vite+vue3+naive-ui技术栈
2. 只需要有一个列表页面，展示注册的人脸数据
3. 列表页面有新增，编辑，删除功能
4. 新增和编辑功能需要有一个表单，表单包含用户ID，人脸图片

#### face-auth-server
在 face-auth-server 目录下创建后端工程
1. 使用Spring Boot技术栈
2. 设计人脸数据表结构
3. 实现人脸数据的增删改查接口
4. 使用sqlite数据库

#### face-auth-client
在 face-auth-client 目录下创建客户端工程
1. 使用Electron技术栈
2. 实现人脸数据采集功能，采集后调用服务端接口进行
3. 实现人脸校验功能，调用服务端接口进行校验

#### face-auth-model
在 face-auth-model目录下创建人脸识别模型工程
1. 使用 uv 构建
2. 实现特征提取和比对功能
3. 提供 api 给 server 调用