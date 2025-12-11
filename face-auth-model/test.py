#!/usr/bin/env python3
"""
Face Authentication Model Service 测试脚本
"""
import requests
import base64
import json
import sys

BASE_URL = "http://localhost:5001"


def test_health():
    """测试健康检查接口"""
    print("=" * 50)
    print("测试健康检查接口...")
    print("=" * 50)

    try:
        response = requests.get(f"{BASE_URL}/health", timeout=5)
        print(f"状态码: {response.status_code}")
        print(f"响应: {json.dumps(response.json(), indent=2, ensure_ascii=False)}")
        return response.status_code == 200
    except requests.exceptions.ConnectionError:
        print("❌ 无法连接到服务，请确保服务已启动")
        return False
    except Exception as e:
        print(f"❌ 错误: {str(e)}")
        return False


def test_extract(image_path):
    """测试特征提取接口"""
    print("\n" + "=" * 50)
    print("测试特征提取接口...")
    print("=" * 50)

    try:
        # 读取图片并转换为base64
        with open(image_path, 'rb') as f:
            image_data = base64.b64encode(f.read()).decode('utf-8')
            image_base64 = f"data:image/jpeg;base64,{image_data}"

        # 调用API
        response = requests.post(
            f"{BASE_URL}/api/face/extract",
            json={'image': image_base64},
            timeout=30
        )

        print(f"状态码: {response.status_code}")

        if response.status_code == 200:
            result = response.json()
            print(f"✅ 特征提取成功")
            print(f"   - 特征向量维度: {len(result['embedding'])}")
            print(f"   - 检测置信度: {result['det_score']:.4f}")
            print(f"   - 检测到的人脸数: {result['face_count']}")
            print(f"   - 人脸位置: {result['bbox']}")
            return True
        else:
            print(f"❌ 请求失败: {response.text}")
            return False

    except FileNotFoundError:
        print(f"❌ 图片文件不存在: {image_path}")
        return False
    except Exception as e:
        print(f"❌ 错误: {str(e)}")
        return False


def test_compare():
    """测试相似度比对接口"""
    print("\n" + "=" * 50)
    print("测试相似度比对接口...")
    print("=" * 50)

    # 创建两个相似的向量
    import random
    base_vector = [random.random() for _ in range(512)]

    # 第二个向量添加少量噪声
    similar_vector = [v + random.uniform(-0.01, 0.01) for v in base_vector]

    try:
        response = requests.post(
            f"{BASE_URL}/api/face/compare",
            json={
                'embedding1': base_vector,
                'embedding2': similar_vector,
                'threshold': 0.6
            },
            timeout=5
        )

        print(f"状态码: {response.status_code}")

        if response.status_code == 200:
            result = response.json()
            print(f"✅ 相似度比对成功")
            print(f"   - 相似度: {result['similarity']:.4f}")
            print(f"   - 是否同一人: {result['is_same_person']}")
            print(f"   - 阈值: {result['threshold']}")
            return True
        else:
            print(f"❌ 请求失败: {response.text}")
            return False

    except Exception as e:
        print(f"❌ 错误: {str(e)}")
        return False


def main():
    """主测试函数"""
    print("\n🚀 Face Authentication Model Service 测试")
    print("=" * 50)

    # 1. 测试健康检查
    health_ok = test_health()
    if not health_ok:
        print("\n❌ 健康检查失败，请先启动服务: ./dev.sh")
        sys.exit(1)

    # 2. 测试相似度比对
    compare_ok = test_compare()

    # 3. 测试特征提取（如果提供了图片路径）
    extract_ok = True
    if len(sys.argv) > 1:
        image_path = sys.argv[1]
        extract_ok = test_extract(image_path)
    else:
        print("\n💡 提示: 可以提供图片路径测试特征提取")
        print("   用法: uv run python test.py /path/to/image.jpg")

    # 总结
    print("\n" + "=" * 50)
    print("测试总结")
    print("=" * 50)
    print(f"健康检查: {'✅ 通过' if health_ok else '❌ 失败'}")
    print(f"相似度比对: {'✅ 通过' if compare_ok else '❌ 失败'}")
    if len(sys.argv) > 1:
        print(f"特征提取: {'✅ 通过' if extract_ok else '❌ 失败'}")

    if health_ok and compare_ok and extract_ok:
        print("\n🎉 所有测试通过！")
        sys.exit(0)
    else:
        print("\n❌ 部分测试失败")
        sys.exit(1)


if __name__ == '__main__':
    main()
