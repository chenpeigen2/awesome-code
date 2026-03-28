/**
 * Demo 5: 子进程 (Bun.spawn)
 *
 * Bun.spawn 用于执行外部命令和子进程，
 * 比Node.js的 child_process 更简洁高效。
 */
export async function demoSpawn() {
  // 简单执行命令
  const proc1 = Bun.spawn(["echo", "Hello from Bun.spawn!"]);
  await proc1.exited;
  console.log("  echo 退出码:", proc1.exitCode);

  // 捕获输出
  const proc2 = Bun.spawn(["echo", "captured output"], {
    stdout: "pipe",
  });
  const output = await new Response(proc2.stdout).text();
  console.log("  捕获输出:", JSON.stringify(output.trim()));

  // 获取系统信息
  const proc3 = Bun.spawn(["uname", "-a"], { stdout: "pipe" });
  const uname = await new Response(proc3.stdout).text();
  console.log("  系统信息:", uname.trim());

  // 列出当前目录文件
  const proc4 = Bun.spawn(["ls", import.meta.dir], { stdout: "pipe" });
  const files = await new Response(proc4.stdout).text();
  console.log("  当前目录文件:", files.trim().split("\n").join(", "));

  // 设置环境变量和工作目录
  const proc5 = Bun.spawn(["printenv", "MY_VAR"], {
    stdout: "pipe",
    env: { ...process.env, MY_VAR: "spawn-demo" },
  });
  const envVal = await new Response(proc5.stdout).text();
  console.log("  子进程环境变量 MY_VAR:", envVal.trim());
}
