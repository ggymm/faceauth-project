### 人脸识别模型服务


#### 下载模型

默认模型保存位置

~/.insightface/models

手动下载地址

https://github.com/deepinsight/insightface/releases

[下载地址](https://github.com/deepinsight/insightface/releases/download/v0.7/buffalo_l.zip)


#### 运行

```bash

uv run python app.py

```


#### 部署

```bash

# 导出依赖项
uv lock
uv export --frozen --no-hashes > requirements.txt

```