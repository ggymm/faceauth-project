"""
Face Authentication Model Service
提供人脸特征提取接口，使用InsightFace模型
"""
import os
import base64
import logging

import cv2
import numpy as np
from flask import Flask, request, jsonify
from insightface.app import FaceAnalysis

# 配置日志
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

# 创建Flask应用
app = Flask(__name__)

# 初始化InsightFace模型（模块加载时执行一次）
logger.info("=" * 50)
logger.info("开始加载InsightFace模型...")
logger.info("=" * 50)

face_model = FaceAnalysis(
    name='buffalo_l',  # 使用buffalo_l模型（高精度）
    providers=['CPUExecutionProvider']  # 使用CPU，如有GPU可改为CUDAExecutionProvider
)
face_model.prepare(ctx_id=0, det_size=(640, 640))

logger.info("InsightFace模型加载成功")


def decode_base64_image(image_base64: str) -> np.ndarray:
    """
    解码base64图片为OpenCV格式

    Args:
        image_base64: base64编码的图片字符串

    Returns:
        OpenCV格式的图片数组
    """
    try:
        # 处理data:image/xxx;base64,xxxxx格式
        if ',' in image_base64:
            image_base64 = image_base64.split(',')[1]

        # 解码base64
        image_bytes = base64.b64decode(image_base64)

        # 转换为numpy数组
        nparr = np.frombuffer(image_bytes, np.uint8)

        # 解码为图片
        img = cv2.imdecode(nparr, cv2.IMREAD_COLOR)

        if img is None:
            raise ValueError("图片解码失败")

        return img

    except Exception as e:
        logger.error(f"图片解码错误: {str(e)}")
        raise


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

    请求格式:
    {
        "image": "base64编码的图片"
    }

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

        # 获取请求数据
        data = request.get_json()

        if not data or 'image' not in data:
            return jsonify({
                'error': '缺少image参数',
                'message': 'Missing required parameter: image'
            }), 400

        # 解码图片
        logger.info("开始处理图片...")
        img = decode_base64_image(data['image'])
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


@app.route('/api/face/compare', methods=['POST'])
def compare_features():
    """
    比对两个特征向量的相似度（可选接口）

    请求格式:
    {
        "embedding1": [512维浮点数数组],
        "embedding2": [512维浮点数数组]
    }

    响应格式:
    {
        "similarity": 0.85,
        "is_same_person": true,
        "threshold": 0.6
    }
    """
    try:
        data = request.get_json()

        if not data or 'embedding1' not in data or 'embedding2' not in data:
            return jsonify({
                'error': '缺少参数',
                'message': 'Missing required parameters: embedding1, embedding2'
            }), 400

        # 转换为numpy数组
        emb1 = np.array(data['embedding1'])
        emb2 = np.array(data['embedding2'])

        # 检查维度
        if emb1.shape != emb2.shape:
            return jsonify({
                'error': '特征向量维度不匹配',
                'message': f'Dimension mismatch: {emb1.shape} vs {emb2.shape}'
            }), 400

        # 计算余弦相似度
        similarity = np.dot(emb1, emb2) / (
            np.linalg.norm(emb1) * np.linalg.norm(emb2)
        )

        # 设置阈值
        threshold = data.get('threshold', 0.6)
        is_same_person = float(similarity) > threshold

        logger.info(f"相似度计算完成: {similarity:.4f}, 阈值: {threshold}")

        return jsonify({
            'similarity': float(similarity),
            'is_same_person': is_same_person,
            'threshold': threshold
        }), 200

    except Exception as e:
        logger.error(f"比对失败: {str(e)}", exc_info=True)
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
