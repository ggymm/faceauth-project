import os
import logging

import numpy as np
from PIL import Image
from flask import Flask, request, jsonify
from insightface.app import FaceAnalysis

# 配置日志
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

logger.info("=" * 50)
logger.info("开始加载InsightFace模型...")
logger.info("=" * 50)

# 加载模型
face_model = FaceAnalysis(
    name='buffalo_l',
    root='./',
    download=False,
    providers=['CPUExecutionProvider']
)
face_model.prepare(ctx_id=0, det_size=(640, 640))

logger.info("InsightFace模型加载成功")

app = Flask(__name__)


@app.route('/health', methods=['GET'])
def health_check():
    """健康检查接口"""
    return jsonify({
        'status': 'healthy',
        'model_loaded': face_model is not None
    })


@app.route('/api/face/extract', methods=['POST'])
def extract_feature():
    """
    提取人脸特征向量接口

    请求格式: multipart/form-data
    - file: 图片文件

    响应格式:
    {
        "embedding": [512维浮点数数组],
        "bbox": [x1, y1, x2, y2],
        "det_score": 0.99,
        "face_count": 1
    }
    """
    try:
        # 检查模型是否加载
        if face_model is None:
            return jsonify({
                'error': '模型未加载',
                'message': 'Model not initialized'
            }), 500

        # 检查是否有文件上传
        if 'file' not in request.files:
            return jsonify({
                'error': '缺少file参数',
                'message': 'Missing required parameter: file'
            }), 400

        file = request.files['file']

        # 检查文件名是否为空
        if file.filename == '':
            return jsonify({
                'error': '未选择文件',
                'message': 'No file selected'
            }), 400

        # 解码图片（直接使用 Pillow）
        logger.info(f"开始处理文件: {file.filename}")
        img = Image.open(file.stream)

        # 转换为RGB模式（处理RGBA、灰度图等）
        if img.mode != 'RGB':
            img = img.convert('RGB')

        # 转换为numpy数组并转为BGR格式（InsightFace要求）
        img = np.array(img)[:, :, ::-1]
        logger.info(f"图片尺寸: {img.shape}")

        # 检测人脸并提取特征
        logger.info("开始检测人脸...")
        faces = face_model.get(img)

        # 检查人脸数量
        if len(faces) == 0:
            return jsonify({
                'error': '未检测到人脸',
                'message': 'No face detected in the image'
            }), 400

        if len(faces) > 1:
            logger.warning(f"检测到{len(faces)}张人脸，将使用第一张")

        # 获取第一张人脸的特征
        face = faces[0]

        # 提取特征向量
        embedding = face.embedding.tolist()  # 转换为Python列表
        bbox = face.bbox.tolist()  # 人脸边界框
        det_score = float(face.det_score)  # 检测置信度

        logger.info(f"特征提取成功，向量维度: {len(embedding)}, 置信度: {det_score:.4f}")

        # 返回结果
        return jsonify({
            'embedding': embedding,
            'bbox': bbox,
            'det_score': det_score,
            'face_count': len(faces)
        }), 200

    except ValueError as e:
        logger.error(f"参数错误: {str(e)}")
        return jsonify({
            'error': '参数错误',
            'message': str(e)
        }), 400

    except Exception as e:
        logger.error(f"处理失败: {str(e)}", exc_info=True)
        return jsonify({
            'error': '服务器内部错误',
            'message': str(e)
        }), 500


if __name__ == '__main__':
    # 启动Flask服务
    port = int(os.environ.get('PORT', 30000))

    logger.info("=" * 50)
    logger.info(f"Face Authentication Model Service 启动在端口: {port}")
    logger.info("=" * 50)

    app.run(
        host='0.0.0.0',
        port=port,
        debug=False  # 生产环境设置为False
    )
