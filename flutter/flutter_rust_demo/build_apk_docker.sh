#!/bin/bash
# 在 Docker 中构建 Flutter APK
# 用法: ./build_apk_docker.sh [debug|release]
# 默认构建 debug APK

set -e

BUILD_TYPE="${1:-debug}"
IMAGE_NAME="flutter-rust-apk-builder"
PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"

# 检查 Docker 镜像是否存在
if ! docker image inspect "$IMAGE_NAME" &>/dev/null; then
    echo "=== Docker 镜像不存在，正在构建... ==="
    docker build -t "$IMAGE_NAME" "$PROJECT_DIR"
fi

echo "=== 在 Docker 中构建 ${BUILD_TYPE} APK ==="
echo "项目目录: $PROJECT_DIR"

# 运行 Docker 容器执行构建
docker run --rm \
    -v "$PROJECT_DIR:/app" \
    -v "$PROJECT_DIR/docker_build.sh:/docker_build.sh:ro" \
    -v flutter_cache:/root/.pub-cache \
    -v cargo_cache:/root/.cargo/registry \
    "$IMAGE_NAME" \
    /docker_build.sh "$BUILD_TYPE"

echo ""
echo "=== 构建完成 ==="

if [[ "$BUILD_TYPE" == "debug" ]]; then
    APK_PATH="$PROJECT_DIR/build/app/outputs/flutter-apk/app-debug.apk"
else
    APK_PATH="$PROJECT_DIR/build/app/outputs/flutter-apk/app-release.apk"
fi

if [ -f "$APK_PATH" ]; then
    echo "APK 位置: $APK_PATH"
    echo "文件大小: $(du -h "$APK_PATH" | cut -f1)"
fi