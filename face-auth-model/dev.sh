#!/bin/bash

# Face Authentication Model Service 启动脚本
# 使用uv运行Flask服务

echo "启动 Face Authentication Model Service..."
echo "使用端口: ${PORT:-30000}"
echo ""

uv run python app.py