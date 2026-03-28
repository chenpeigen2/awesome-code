/**
 * Demo 7: Glob 文件匹配 (Bun.Glob)
 *
 * Bun.Glob 提供快速的文件模式匹配，
 * 支持标准 glob 语法：*、**、?、[abc]、{a,b}。
 */
export async function demoGlob() {
  // 匹配当前目录下所有 .ts 文件
  const tsGlob = new Bun.Glob("*.ts");
  console.log("  当前目录 .ts 文件:");
  for await (const file of tsGlob.scan({ cwd: import.meta.dir })) {
    console.log(`    ${file}`);
  }

  // 匹配所有 TypeScript 文件（递归）
  const allTsGlob = new Bun.Glob("**/*.ts");
  let count = 0;
  for await (const file of allTsGlob.scan({ cwd: import.meta.dir })) {
    count++;
  }
  console.log(`  递归匹配 .ts 文件数: ${count}`);

  // 测试字符串匹配
  const jsonGlob = new Bun.Glob("*.json");
  console.log("  匹配 package.json:", jsonGlob.match("package.json"));
  console.log("  匹配 src/data.json:", jsonGlob.match("src/data.json"));

  const anyGlob = new Bun.Glob("**/*.json");
  console.log("  递归匹配 src/data.json:", anyGlob.match("src/data.json"));

  // 获取绝对路径
  console.log("  绝对路径模式:");
  for await (const file of tsGlob.scan({
    cwd: import.meta.dir,
    absolute: true,
  })) {
    console.log(`    ${file}`);
    break; // 只展示一个
  }
}
