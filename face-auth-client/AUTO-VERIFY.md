# 自动人脸认证功能使用说明

## 功能概述

自动人脸认证页面（auto-verify.html）提供了智能化的人脸识别体验，包含以下特性：

### 核心功能
- ✅ **自动人脸检测**：实时检测视频流中的人脸
- ✅ **质量评分系统**：评估人脸大小、角度、清晰度
- ✅ **活体检测**：通过眨眼检测防止照片攻击
- ✅ **自动抓拍**：质量达标后自动截取并上传
- ✅ **即时反馈**：实时显示检测状态和质量指标

## 启动方式

### 方式1：使用npm脚本（推荐）

```bash
# 启动自动认证页面
npm run start:auto

# 启动原始页面（手动拍摄）
npm start
```

### 方式2：使用命令行参数

```bash
# 启动自动认证页面
electron . --page=auto-verify

# 启动原始页面
electron .
```

### 方式3：使用环境变量

```bash
# 启动自动认证页面
PAGE=auto-verify npm start

# 启动原始页面
npm start
```

## 使用流程

### 1. 启动应用
```bash
cd face-auth-client
npm run start:auto
```

### 2. 输入用户ID
在页面上的"用户ID"输入框中输入要认证的用户ID

### 3. 点击"开始自动认证"按钮
系统将自动启动摄像头并开始检测

### 4. 按照提示操作
- **将人脸置于椭圆引导框内**
- **保持正对摄像头**
- **确保光线充足**
- **按照提示眨眼1-2次**

### 5. 等待自动完成
当满足以下条件时，系统会自动拍摄并认证：
- 人脸大小合适（占画面25%-50%）
- 正对摄像头（偏转角<20度）
- 检测置信度高（>70%）
- 连续3帧质量达标
- 检测到至少1次眨眼

### 6. 查看结果
认证完成后，页面会显示：
- ✅ **成功**：显示相似度、阈值和活体检测结果
- ❌ **失败**：显示失败原因和建议

## 质量指标说明

页面右上角实时显示三个质量指标：

### 1. 人脸大小 (25%-50%)
- **过小**：请靠近摄像头
- **合适**：当前距离正好
- **过大**：请稍微远离

### 2. 正脸角度 (偏转<20度)
- **偏转过大**：请正对摄像头
- **角度良好**：保持当前姿势

### 3. 检测置信度 (>70%)
- **低于70%**：可能光线不足或画面模糊
- **高于70%**：检测质量良好

## 活体检测说明

系统通过**眨眼检测**实现活体识别：

### 检测原理
- 计算眼睛纵横比(EAR)
- 当EAR < 0.2时判定为闭眼
- 检测到"睁眼→闭眼"转换时计数

### 要求
- 至少检测到**1次完整眨眼**
- 眨眼需自然，不要过快或过慢
- 眨眼时保持脸部位置稳定

## 技术参数

### 摄像头设置
- 分辨率：1280x720 (理想值)
- 帧率：自适应
- 检测间隔：100ms

### 质量阈值
```javascript
{
  QUALITY_THRESHOLD: 85,      // 综合质量阈值
  CONSECUTIVE_FRAMES: 3,      // 连续达标帧数
  BLINK_REQUIRED: 1,          // 需要的眨眼次数
  FACE_SIZE_MIN: 0.25,        // 最小人脸占比
  FACE_SIZE_IDEAL: 0.35,      // 理想人脸占比
  ANGLE_LIMIT: 20,            // 最大偏转角度
  CONFIDENCE_MIN: 0.7         // 最小检测置信度
}
```

### API配置
```javascript
{
  BACKEND_URL: 'http://192.168.1.43:20000',
  VERIFY_ENDPOINT: '/api/v1/face/client/verify'
}
```

## 常见问题

### Q: 报错"Failed to fetch"或找不到.bin文件？
**A:** 这是模型文件格式问题，解决方法：

1. **检查文件名**：确保文件是 `.bin` 格式，不是 `-shard1` 格式
   ```bash
   # 正确的文件名
   tiny_face_detector_model.bin
   face_landmark_68_tiny_model.bin

   # 错误的文件名（旧版）
   tiny_face_detector_model-shard1
   face_landmark_68_tiny_model-shard1
   ```

2. **重新下载**：如果文件名不对，删除后重新下载
   ```bash
   cd models
   rm -f *-shard1
   curl -O https://cdn.jsdelivr.net/npm/@vladmandic/face-api/model/tiny_face_detector_model.bin
   curl -O https://cdn.jsdelivr.net/npm/@vladmandic/face-api/model/face_landmark_68_tiny_model.bin
   ```

3. **验证文件**：检查文件是否完整
   ```bash
   ls -lh models/
   # 应该看到 4 个文件，两个.json和两个.bin
   ```

### Q: 一直提示"未检测到人脸"？
**A:** 检查以下项：
- 摄像头是否正常工作
- 光线是否充足
- 人脸是否在画面中
- 是否被遮挡（口罩、墨镜等）

### Q: 质量评分一直很低？
**A:** 尝试以下调整：
- 调整与摄像头的距离（30-50cm最佳）
- 增加光源或调整光线方向
- 清洁摄像头镜头
- 保持脸部正对摄像头

### Q: 眨眼检测失败？
**A:** 注意事项：
- 眨眼动作要自然完整
- 不要连续快速眨眼
- 保持脸部在检测区域内
- 佩戴眼镜可能影响检测

### Q: 认证失败显示相似度低？
**A:** 可能原因：
- 用户ID输入错误
- 该用户尚未注册人脸
- 当前照片与注册照片差异较大
- 建议重新注册或联系管理员

### Q: 如何修改后端地址？
**A:** 编辑 `auto-verify.html` 第456行：
```javascript
const CONFIG = {
    BACKEND_URL: 'http://your-server:port',  // 修改为实际地址
    ...
};
```

## 模型文件说明

### 模型来源

本项目使用 **@vladmandic/face-api** 提供的预训练模型：
- **TinyFaceDetector**: 轻量级人脸检测模型（189KB）
- **FaceLandmark68Tiny**: 68点人脸关键点检测模型（75KB）

### 模型下载方法

如需重新下载或更新模型文件，执行以下命令：

```bash
# 进入客户端目录
cd face-auth-client

# 创建模型目录
mkdir -p models
cd models

# 下载 TinyFaceDetector 模型
curl -O https://cdn.jsdelivr.net/npm/@vladmandic/face-api/model/tiny_face_detector_model-weights_manifest.json
curl -O https://cdn.jsdelivr.net/npm/@vladmandic/face-api/model/tiny_face_detector_model.bin

# 下载 FaceLandmark68Tiny 模型
curl -O https://cdn.jsdelivr.net/npm/@vladmandic/face-api/model/face_landmark_68_tiny_model-weights_manifest.json
curl -O https://cdn.jsdelivr.net/npm/@vladmandic/face-api/model/face_landmark_68_tiny_model.bin
```

**注意**：文件名必须是 `.bin` 格式，不要下载 `-shard1` 格式的文件。

### 模型文件清单

下载完成后，`models/` 目录应包含以下文件：

```
models/
├── tiny_face_detector_model-weights_manifest.json    (3.1 KB)
├── tiny_face_detector_model.bin                      (189 KB)
├── face_landmark_68_tiny_model-weights_manifest.json (4.7 KB)
└── face_landmark_68_tiny_model.bin                   (75 KB)

总大小: 约 270 KB
```

### face-api.js 库文件

如需更新 face-api.js 库本身：

```bash
# 下载最新版本的 face-api.js
cd face-auth-client
curl -o face-api.js https://cdn.jsdelivr.net/npm/@vladmandic/face-api/dist/face-api.min.js
```

**当前版本**: face-api.js (1.3 MB)

### 验证安装

下载完成后，验证文件是否正确：

```bash
ls -lh models/
# 应显示 4 个文件，总大小约 270KB

ls -lh face-api.js
# 应显示约 1.3MB
```

### CDN 替代方案

如果 jsdelivr CDN 访问困难，可使用以下替代源：

```bash
# 使用 unpkg CDN
curl -O https://unpkg.com/@vladmandic/face-api/model/tiny_face_detector_model-weights_manifest.json

# 或直接从 GitHub Releases 下载
# https://github.com/vladmandic/face-api/tree/master/model
```

## 技术栈

- **前端框架**：原生HTML/CSS/JavaScript
- **人脸检测**：face-api.js (TinyFaceDetector)
- **活体检测**：眨眼检测算法 (EAR)
- **摄像头访问**：WebRTC getUserMedia API
- **桌面框架**：Electron 37.10.3

## 文件结构

```
face-auth-client/
├── main.js              # Electron主进程（支持页面切换）
├── app.html             # 原始手动认证页面
├── auto-verify.html     # 新增：自动认证页面 ⭐
├── models/              # face-api.js模型文件 ⭐
│   ├── tiny_face_detector_model-weights_manifest.json
│   ├── tiny_face_detector_model.bin
│   ├── face_landmark_68_tiny_model-weights_manifest.json
│   └── face_landmark_68_tiny_model.bin
├── package.json         # 新增start:auto脚本
└── AUTO-VERIFY.md       # 本文档
```

## 开发者信息

### 调试模式
应用启动时会自动打开开发者工具，查看：
- Console：查看检测日志和错误信息
- Network：查看API请求状态
- Performance：监控性能指标

### 自定义配置
修改 `auto-verify.html` 中的 CONFIG 对象：
```javascript
const CONFIG = {
    BACKEND_URL: 'http://192.168.1.43:20000',    // 后端地址
    MODEL_PATH: './models',                       // 本地模型路径 ⭐
    QUALITY_THRESHOLD: 85,                        // 质量阈值
    CONSECUTIVE_FRAMES: 3,                        // 稳定帧数
    BLINK_REQUIRED: 1,                            // 眨眼次数
    DETECTION_INTERVAL: 100                       // 检测间隔(ms)
};
```

**注意**：模型文件已预下载到 `models/` 目录，无需联网加载。

## 更新日志

### v1.0.0 (2025-12-13)
- ✨ 首次发布
- ✅ 实现自动人脸检测
- ✅ 实现质量评分系统
- ✅ 实现眨眼活体检测
- ✅ 实现自动抓拍上传
- ✅ 集成face-api.js库
- ✅ 预下载模型文件到本地

## 反馈与支持

如遇问题或有改进建议，请联系开发团队或提交Issue。

---

**注意**：模型文件已预下载到 `models/` 目录（约270KB），首次启动即可使用，无需联网下载。
