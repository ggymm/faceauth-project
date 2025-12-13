#!/bin/bash

# 启动 face-auth-model 服务
cd "$(dirname "$0")/.."
cd face-auth-model
echo "Starting face-auth-model service..."

uv run python app.py

if [ $? -eq 0 ]; then
    echo "face-auth-model service started successfully."
else
    echo "Failed to start face-auth-model service."
fi