# Flutter Rust Bridge Android Build Image
# 包含 Flutter SDK, Rust 工具链, Android SDK/NDK

FROM ghcr.io/cirruslabs/flutter:stable

# 设置环境变量
ENV DEBIAN_FRONTEND=noninteractive
ENV ANDROID_HOME=/opt/android-sdk
ENV ANDROID_SDK_ROOT=/opt/android-sdk
ENV NDK_VERSION=27.0.12077973
ENV PATH="${PATH}:${HOME}/.cargo/bin"

# 配置 pip 阿里云镜像
RUN pip config set global.index-url https://mirrors.aliyun.com/pypi/simple/ 2>/dev/null || true

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

# 配置 Git 代理设置（加速 GitHub 访问）
RUN git config --global url."https://ghproxy.com/https://github.com/".insteadOf "https://github.com/" || true

# 安装 Rust（使用清华大学镜像）
ENV RUSTUP_DIST_SERVER=https://mirrors.tuna.tsinghua.edu.cn/rustup
RUN curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh -s -- -y
RUN . $HOME/.cargo/env && rustup default stable

# 配置 Cargo 使用中国镜像
RUN mkdir -p $HOME/.cargo && \
    echo '[source.crates-io]' > $HOME/.cargo/config.toml && \
    echo 'replace-with = "tuna"' >> $HOME/.cargo/config.toml && \
    echo '[source.tuna]' >> $HOME/.cargo/config.toml && \
    echo 'registry = "https://mirrors.tuna.tsinghua.edu.cn/git/crates.io-index.git"' >> $HOME/.cargo/config.toml

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

# 配置 Gradle 全局镜像
RUN mkdir -p $HOME/.gradle && \
    echo 'distributionUrl=https://mirrors.cloud.tencent.com/gradle/gradle-8.14-bin.zip' > /tmp/gradle-wrapper.properties

# 设置工作目录
WORKDIR /app

# 入口脚本
COPY docker_build.sh /docker_build.sh
RUN chmod +x /docker_build.sh

# 构建命令
CMD ["/docker_build.sh"]