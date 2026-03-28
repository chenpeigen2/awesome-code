/**
 * Demo 6: 密码学哈希 (Bun.hash)
 *
 * Bun 内置了多种哈希算法，无需安装 crypto 模块。
 * 支持: MD5, SHA-1, SHA-256, SHA-512 等。
 */
import { hash, CRC32 } from "bun";

export async function demoHash() {
  const data = "Hello, Bun!";

  // SHA-256 哈希（默认）
  const sha256 = hash(data);
  console.log("  SHA-256:", sha256.toString(16).slice(0, 32) + "...");

  // 不同算法
  const algorithms = ["md5", "sha-1", "sha-256", "sha-512"] as const;
  for (const algo of algorithms) {
    // @ts-expect-error - Bun.hash 支持多种算法
    const h = new Bun.CryptoHasher(algo).update(data).digest("hex");
    console.log(`  ${algo.toUpperCase()}:`, h.slice(0, 40) + (h.length > 40 ? "..." : ""));
  }

  // HMAC 签名
  const hmac = new Bun.CryptoHasher("sha256", "secret-key")
    .update(data)
    .digest("hex");
  console.log("  HMAC-SHA256:", hmac.slice(0, 32) + "...");

  // 对文件内容计算哈希
  const fileHash = hash("some file content");
  console.log("  文件内容哈希:", fileHash.toString(16).slice(0, 32) + "...");

  // 密码哈希（推荐用于密码存储）
  const password = "my-password-123";
  const hashed = await Bun.password.hash(password);
  console.log("  密码哈希:", hashed.slice(0, 30) + "...");

  const isValid = await Bun.password.verify(password, hashed);
  console.log("  密码验证:", isValid);

  const isInvalid = await Bun.password.verify("wrong-password", hashed);
  console.log("  错误密码验证:", isInvalid);
}
