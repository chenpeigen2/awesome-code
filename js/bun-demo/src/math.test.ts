/**
 * Bun 测试示例
 *
 * Bun 内置了 Jest 兼容的测试框架 (bun:test)。
 * 运行: bun test
 */
import { test, describe, expect, it } from "bun:test";

// --- 基本数学函数 ---
function add(a: number, b: number): number {
  return a + b;
}

function multiply(a: number, b: number): number {
  return a * b;
}

function divide(a: number, b: number): number {
  if (b === 0) throw new Error("Division by zero");
  return a / b;
}

// --- 测试用例 ---
describe("数学运算", () => {
  test("加法", () => {
    expect(add(1, 2)).toBe(3);
    expect(add(-1, 1)).toBe(0);
    expect(add(0.1, 0.2)).toBeCloseTo(0.3);
  });

  test("乘法", () => {
    expect(multiply(2, 3)).toBe(6);
    expect(multiply(-1, 5)).toBe(-5);
    expect(multiply(0, 100)).toBe(0);
  });

  test("除法 - 正常情况", () => {
    expect(divide(10, 2)).toBe(5);
    expect(divide(7, 2)).toBe(3.5);
  });

  test("除法 - 除零错误", () => {
    expect(() => divide(1, 0)).toThrow("Division by zero");
  });
});

describe("Bun 特性测试", () => {
  it("支持 Snapshot 测试", () => {
    const data = { name: "Bun", version: "1.x", features: ["fast", "sqlite"] };
    expect(data).toMatchSnapshot();
  });

  test("异步测试", async () => {
    const promise = Promise.resolve(42);
    const result = await promise;
    expect(result).toBe(42);
  });

  test("数组匹配", () => {
    const arr = [1, 2, 3, 4, 5];
    expect(arr).toContain(3);
    expect(arr).toHaveLength(5);
    expect(arr).toEqual([1, 2, 3, 4, 5]);
  });

  test("对象匹配", () => {
    const user = { id: 1, name: "Alice", email: "alice@test.com" };
    expect(user).toMatchObject({ name: "Alice" });
    expect(user).toHaveProperty("email");
  });
});
