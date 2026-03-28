/**
 * Demo 2: SQLite 数据库
 *
 * Bun 内置了 bun:sqlite 模块，无需额外安装依赖即可使用 SQLite。
 * 支持预处理语句、事务、类映射等特性。
 */
import { Database } from "bun:sqlite";

export async function demoSqlite() {
  // 使用内存数据库（不持久化到文件）
  const db = new Database(":memory:");

  // 启用 WAL 模式提升性能
  db.run("PRAGMA journal_mode = WAL;");

  // 建表
  db.run(`
    CREATE TABLE IF NOT EXISTS users (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      name TEXT NOT NULL,
      email TEXT UNIQUE NOT NULL,
      age INTEGER
    )
  `);
  console.log("  建表成功");

  // 预处理语句 - 插入数据
  const insertStmt = db.query(
    "INSERT INTO users (name, email, age) VALUES ($name, $email, $age)"
  );
  insertStmt.run({ $name: "Alice", $email: "alice@demo.com", $age: 28 });
  insertStmt.run({ $name: "Bob", $email: "bob@demo.com", $age: 32 });
  insertStmt.run({ $name: "Charlie", $email: "charlie@demo.com", $age: 25 });
  console.log("  插入 3 条数据");

  // 查询所有数据
  const allUsers = db.query("SELECT * FROM users").all();
  console.log("  查询所有用户:", allUsers);

  // 查询单条数据
  const user = db.query("SELECT * FROM users WHERE name = ?").get("Alice");
  console.log("  查询 Alice:", user);

  // 聚合查询
  const stats = db
    .query("SELECT COUNT(*) as count, AVG(age) as avg_age FROM users")
    .get();
  console.log("  统计信息:", stats);

  // 事务操作
  const insertBatch = db.transaction((users: Array<{ $name: string; $email: string; $age: number }>) => {
    for (const u of users) {
      insertStmt.run(u);
    }
    return users.length;
  });

  const count = insertBatch([
    { $name: "Dave", $email: "dave@demo.com", $age: 30 },
    { $name: "Eve", $email: "eve@demo.com", $age: 27 },
  ]);
  console.log(`  事务批量插入 ${count} 条数据`);

  // 迭代器遍历大数据集
  let iterCount = 0;
  for (const row of db.query("SELECT name FROM users").iterate()) {
    iterCount++;
  }
  console.log(`  迭代器遍历 ${iterCount} 条数据`);

  db.close();
  console.log("  数据库已关闭");
}
