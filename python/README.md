# Awesome Python

Python 学习示例集合，包含多个主题的代码示例和实践。

## 项目结构

```
python/
├── basic/              # Python 基础语法示例
├── builtin_funcs/      # 内置函数使用示例
├── cli_tools/          # 命令行工具示例 (Typer)
├── dataset/            # 数据集目录
├── docs/               # 项目文档
├── fortran/            # Fortran 与 Python 互操作示例
├── ios/                # iOS 相关示例
├── lambdas/            # Lambda 表达式示例
├── lee/                # LeetCode 算法练习
├── modules/            # 模块系统示例
├── oop/                # 面向对象编程示例
├── pangu/              # 数据处理工具
├── plot/               # Matplotlib 绑图示例
├── pytorch_tutorials/  # PyTorch 深度学习教程
├── regex/              # 正则表达式示例
└── threads/            # 多线程编程示例
```

## 环境设置

使用 [uv](https://docs.astral.sh/uv/) 管理依赖：

```bash
# 安装依赖
uv sync

# 安装开发依赖
uv sync --extra dev
```

## 主要依赖

- **pandas** - 数据处理
- **numpy** - 数值计算
- **matplotlib** - 数据可视化
- **torch/torchvision** - 深度学习
- **typer** - 命令行工具开发
- **Pillow** - 图像处理

## 模块说明

### builtin_funcs
Python 内置函数的使用示例和文档。

### pytorch_tutorials
PyTorch 深度学习框架的学习教程，包含：
- lesson_01 - 基础入门
- lesson_02 - 数据加载
- lesson_07 - 高级主题
- onehots - One-hot 编码示例
- read_data - 数据读取工具

### cli_tools
使用 Typer 构建命令行工具的示例。

### pangu
CSV/Excel 数据处理工具集。

## 许可证

MIT License
