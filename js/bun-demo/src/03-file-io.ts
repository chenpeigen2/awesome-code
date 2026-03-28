/**
 * Demo 3: 文件 I/O
 *
 * Bun 提供了高效的文件操作 API：
 * - Bun.file(): 创建文件引用（懒加载）
 * - Bun.write(): 写入文件
 * - FileSink: 增量写入
 */
import { join } from "path";
import { rmdirSync } from "fs";

const TMP_DIR = join(import.meta.dir, "_tmp");

export async function demoFileIO() {
  // 创建临时目录
  await Bun.write(join(TMP_DIR, ".gitkeep"), "");
  console.log("  创建临时目录:", TMP_DIR);

  // 基本写入
  const textPath = join(TMP_DIR, "hello.txt");
  await Bun.write(textPath, "Hello from Bun!\n第二行内容\n");
  console.log("  写入文件:", textPath);

  // 读取为文本
  const text = await Bun.file(textPath).text();
  console.log("  读取文本:", JSON.stringify(text));

  // 写入 JSON
  const jsonPath = join(TMP_DIR, "data.json");
  await Bun.write(jsonPath, JSON.stringify({ name: "Bun", version: "1.x", features: ["fast", "typescript"] }, null, 2));
  const json = await Bun.file(jsonPath).json();
  console.log("  读取 JSON:", json);

  // 文件元信息
  const file = Bun.file(textPath);
  console.log("  文件大小:", file.size, "bytes");
  console.log("  MIME 类型:", file.type);

  // 文件是否存在
  const exists = await file.exists();
  console.log("  文件存在:", exists);

  // 复制文件
  const copyPath = join(TMP_DIR, "hello-copy.txt");
  await Bun.write(copyPath, Bun.file(textPath));
  console.log("  复制文件:", copyPath);

  // FileSink 增量写入（适合日志等场景）
  const logPath = join(TMP_DIR, "app.log");
  const writer = Bun.file(logPath).writer();
  writer.write("[INFO] 应用启动\n");
  writer.write("[DEBUG] 处理请求\n");
  writer.write("[INFO] 应用关闭\n");
  writer.flush();
  writer.end();
  const logContent = await Bun.file(logPath).text();
  console.log("  日志文件内容:", JSON.stringify(logContent));

  // 读取为 ArrayBuffer / Uint8Array
  const bytes = await Bun.file(textPath).bytes();
  console.log("  字节数据:", bytes.length, "bytes");

  // 清理临时文件
  for (const name of ["hello.txt", "data.json", "hello-copy.txt", "app.log", ".gitkeep"]) {
    await Bun.file(join(TMP_DIR, name)).delete();
  }
  // 删除临时目录
  rmdirSync(TMP_DIR);
  console.log("  已清理临时文件");
}
