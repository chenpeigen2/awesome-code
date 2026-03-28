/**
 * Demo 8: 性能计时
 *
 * Bun 提供纳秒级计时器和高精度性能测量。
 */
export async function demoPerformance() {
  // 纳秒级计时
  const start = Bun.nanoseconds();

  // 执行一些计算
  let sum = 0;
  for (let i = 0; i < 1_000_000; i++) {
    sum += i;
  }

  const end = Bun.nanoseconds();
  const elapsedNs = end - start;
  console.log(`  100 万次加法: ${elapsedNs.toLocaleString()} ns (~${(elapsedNs / 1e6).toFixed(2)} ms)`);
  console.log(`  计算结果: ${sum}`);

  // 对比 JSON 解析性能
  const largeJson = JSON.stringify(
    Array.from({ length: 10_000 }, (_, i) => ({
      id: i,
      name: `user-${i}`,
      active: i % 2 === 0,
    }))
  );

  const parseStart = Bun.nanoseconds();
  const parsed = JSON.parse(largeJson);
  const parseEnd = Bun.nanoseconds();
  console.log(`  解析 1 万条 JSON: ${(parseEnd - parseStart).toLocaleString()} ns (~${((parseEnd - parseStart) / 1e6).toFixed(2)} ms)`);

  // performance.now() 也可用
  const t0 = performance.now();
  await Bun.sleep(10); // Bun 内置的 sleep
  const t1 = performance.now();
  console.log(`  Bun.sleep(10) 实际耗时: ${(t1 - t0).toFixed(2)} ms`);
}
