# Flutter Rust Bridge Android Build Image
# 包含 Flutter SDK, Rust 工具链, Android SDK/NDK

FROM ghcr.io/cirruslabs/flutter:stable

# 设置环境变量
ENV DEBIAN_FRONTEND=noninteractive
ENV ANDROID_HOME=/opt/android-sdk
ENV ANDROID_SDK_ROOT=/opt/android-sdk
ENV NDK_VERSION=27.0.12077973
ENV PATH="${PATH}:${HOME}/.cargo/bin"

# 安装必要的构建工具
RUN apt-get update && apt-get install -y \
    curl \
    git \
    unzip \
    xz-utils \
    zip \
    libglu1-mesa \
    clang \
    cmake \
    ninja-build \
    pkg-config \
    libgtk-3-dev \
    && rm -rf /var/lib/apt/lists/*

# 安装 Rust
RUN curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh -s -- -y
RUN . $HOME/.cargo/env && rustup default stable

# 添加 Android 编译目标
RUN . $HOME/.cargo/env && rustup target add \
    aarch64-linux-android \
    armv7-linux-androideabi \
    x86_64-linux-android \
    i686-linux-android

# 安装 flutter_rust_bridge_codegen
RUN . $HOME/.cargo/env && cargo install flutter_rust_bridge_codegen

# 接受 Android SDK 许可证并安装 NDK
RUN yes | sdkmanager --licenses || true
RUN sdkmanager "ndk;${NDK_VERSION}"
RUN sdkmanager "build-tools;34.0.0"
RUN sdkmanager "platforms;android-34"

# 设置工作目录
WORKDIR /app

# 入口脚本
COPY docker_build.sh /docker_build.sh
RUN chmod +x /docker_build.sh

# 构建命令
CMD ["/docker_build.sh"]
