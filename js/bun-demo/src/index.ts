/**
 * Bun 学习 Demo - 入口文件
 *
 * 本项目演示 Bun 运行时的核心特性：
 * 1. HTTP 服务器 (Bun.serve)
 * 2. SQLite 数据库 (bun:sqlite)
 * 3. 文件 I/O (Bun.file / Bun.write)
 * 4. 环境变量 (Bun.env)
 * 5. 测试 (bun:test)
 * 6. 子进程 (Bun.spawn)
 * 7. 密码学哈希 (Bun.hash)
 * 8. 性能计时 (Bun.nanoseconds)
 * 9. Glob 文件匹配 (Bun.Glob)
 *
 * 运行方式: bun run src/index.ts
 * 开发模式: bun --watch src/index.ts
 * 运行测试: bun test
 */

import { demoHttpServer } from "./01-http-server";
import { demoSqlite } from "./02-sqlite";
import { demoFileIO } from "./03-file-io";
import { demoEnv } from "./04-env";
import { demoSpawn } from "./05-spawn";
import { demoHash } from "./06-hash";
import { demoGlob } from "./07-glob";
import { demoPerformance } from "./08-performance";
import { demoFetch } from "./09-fetch";

const demos = [
  { name: "HTTP 服务器", fn: demoHttpServer },
  { name: "SQLite 数据库", fn: demoSqlite },
  { name: "文件 I/O", fn: demoFileIO },
  { name: "环境变量", fn: demoEnv },
  { name: "子进程", fn: demoSpawn },
  { name: "密码学哈希", fn: demoHash },
  { name: "Glob 文件匹配", fn: demoGlob },
  { name: "性能计时", fn: demoPerformance },
  { name: "HTTP 客户端 (Fetch)", fn: demoFetch },
];

async function main() {
  console.log("========================================");
  console.log("  Bun 学习 Demo");
  console.log(`  Bun 版本: ${Bun.version}`);
  console.log(`  运行平台: ${process.platform} ${process.arch}`);
  console.log("========================================\n");

  for (const demo of demos) {
    console.log(`\n--- ${demo.name} ---`);
    try {
      await demo.fn();
    } catch (err) {
      console.error(`  [错误] ${err}`);
    }
    console.log();
  }

  console.log("========================================");
  console.log("  所有 Demo 执行完毕!");
  console.log("========================================");
}

main();
