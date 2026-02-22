#!/bin/bash
# 在 Docker 中构建 Flutter APK
# 用法: ./build_apk_docker.sh [debug|release]
# 默认构建 debug APK

set -e

# 设置默认构建类型为 debug
BUILD_TYPE="${1:-debug}"
# 定义 Docker 镜像名称
IMAGE_NAME="flutter-rust-apk-builder"
# 获取当前脚本所在目录的绝对路径
PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"

# 检查指定的 Docker 镜像是否存在
if ! docker image inspect "$IMAGE_NAME" &>/dev/null; then
    echo "=== Docker 镜像不存在，正在构建... ==="
    # 如果镜像不存在，则构建新的 Docker 镜像
    docker build -t "$IMAGE_NAME" "$PROJECT_DIR"
fi

# 输出构建信息
echo "=== 在 Docker 中构建 ${BUILD_TYPE} APK ==="
echo "项目目录: $PROJECT_DIR"

# 运行 Docker 容器执行构建
docker run --rm \
    -v "$PROJECT_DIR:/app" \                    # 挂载项目目录到容器内
    -v "$PROJECT_DIR/docker_build.sh:/docker_build.sh:ro" \  # 挂载构建脚本（只读）
    -v flutter_cache:/root/.pub-cache \         # 挂载 Flutter 缓存卷
    -v cargo_cache:/root/.cargo/registry \      # 挂载 Cargo 缓存卷
    "$IMAGE_NAME" \                             # 使用指定的镜像
    /docker_build.sh "$BUILD_TYPE"              # 在容器内执行构建脚本

# 构建完成后输出信息
echo ""
echo "=== 构建完成 ==="

# 根据构建类型设置 APK 文件路径
if [[ "$BUILD_TYPE" == "debug" ]]; then
    APK_PATH="$PROJECT_DIR/build/app/outputs/flutter-apk/app-debug.apk"
else
    APK_PATH="$PROJECT_DIR/build/app/outputs/flutter-apk/app-release.apk"
fi

# 检查 APK 文件是否存在并输出相关信息
if [ -f "$APK_PATH" ]; then
    echo "APK 位置: $APK_PATH"
    echo "文件大小: $(du -h "$APK_PATH" | cut -f1)"
fi
