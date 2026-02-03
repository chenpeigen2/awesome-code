# 这是一个示例 Python 脚本。

# 按 Shift+F10 执行或将其替换为您的代码。
# 按 双击 Shift 在所有地方搜索类、文件、工具窗口、操作和设置。


import asyncio
from iflow_sdk import IFlowClient, AssistantMessage, TaskFinishMessage

async def main():
    # SDK 自动处理：
    # 1. 检测 iFlow 是否已安装
    # 2. 启动 iFlow 进程（如果未运行）
    # 3. 查找可用端口并建立连接
    # 4. 退出时自动清理资源
    async with IFlowClient() as client:
        await client.send_message("你好，iFlow！")

        async for message in client.receive_messages():
            if isinstance(message, AssistantMessage):
                print(message.chunk.text, end="\n", flush=True)
            elif isinstance(message, TaskFinishMessage):
                break


asyncio.run(main())
