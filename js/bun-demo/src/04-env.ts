/**
 * Demo 4: 环境变量
 *
 * Bun 自动加载 .env 文件，支持 Bun.env 和 process.env。
 */
export async function demoEnv() {
  // Bun 自动加载了 .env 文件中的变量
  console.log("  APP_NAME:", Bun.env.APP_NAME);
  console.log("  APP_PORT:", Bun.env.APP_PORT);
  console.log("  APP_SECRET:", Bun.env.APP_SECRET ? "****(已隐藏)" : "(未设置)");

  // process.env 同样可用
  console.log("  HOME:", process.env.HOME);

  // 设置环境变量
  process.env.MY_VAR = "hello-bun";
  console.log("  设置 MY_VAR:", Bun.env.MY_VAR);

  // 类型转换辅助
  const port = parseInt(Bun.env.APP_PORT ?? "3000", 10);
  console.log("  端口号 (number):", port, typeof port);
}
