#!/bin/bash
set -e

# 用法: ./docker_test.sh
# 在 Docker 中运行测试

IMAGE_NAME="flutter-rust-demo"
PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"

echo "=== 在 Docker 中运行测试 ==="

docker run --rm \
    -v "$PROJECT_DIR:/app" \
    -v "$PROJECT_DIR/docker_test.sh:/docker_test.sh:ro" \
    -v flutter_cache:/root/.pub-cache \
    -v cargo_cache:/root/.cargo/registry \
    "$IMAGE_NAME" \
    bash -c "
        cd /app
        export PUB_HOSTED_URL=https://pub.flutter-io.cn
        export FLUTTER_STORAGE_BASE_URL=https://storage.flutter-io.cn
        flutter pub get
        flutter test
    "

echo "=== 测试完成 ==="
