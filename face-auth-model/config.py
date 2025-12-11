"""
Face Authentication Model Service 配置文件
"""

# 服务配置
SERVER_HOST = '0.0.0.0'
SERVER_PORT = 5000

# 模型配置
MODEL_NAME = 'buffalo_l'  # InsightFace模型名称 (buffalo_s/buffalo_m/buffalo_l)
DETECTION_SIZE = (640, 640)  # 人脸检测尺寸
PROVIDERS = ['CPUExecutionProvider']  # 推理引擎 (CPUExecutionProvider/CUDAExecutionProvider)

# 人脸识别阈值
SIMILARITY_THRESHOLD = 0.6  # 相似度阈值，高于此值判定为同一人

# 日志配置
LOG_LEVEL = 'INFO'  # DEBUG, INFO, WARNING, ERROR
