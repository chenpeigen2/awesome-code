/**
 * Demo 9: HTTP 客户端 (Fetch)
 *
 * Bun 内置了基于 libcurl 的 fetch 实现，
 * 默认支持 HTTP/2、连接池、自动压缩等。
 */
export async function demoFetch() {
  // 启动本地服务器用于测试
  const server = Bun.serve({
    port: 0,
    routes: {
      "/api/hello": () => Response.json({ message: "Hello from Bun!" }),
      "/api/users": () =>
        Response.json([
          { id: 1, name: "Alice" },
          { id: 2, name: "Bob" },
        ]),
      "/api/echo": {
        POST: async (req) => {
          const body = await req.json();
          return Response.json({ received: body });
        },
      },
    },
    fetch() {
      return new Response("Not Found", { status: 404 });
    },
  });

  const base = server.url.toString();

  // GET 请求
  const res1 = await fetch(`${base}api/hello`);
  console.log("  GET /api/hello:", await res1.json());

  // GET 请求 - 列表数据
  const res2 = await fetch(`${base}api/users`);
  console.log("  GET /api/users:", await res2.json());

  // POST 请求 - 发送 JSON
  const res3 = await fetch(`${base}api/echo`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ name: "Test", value: 42 }),
  });
  console.log("  POST /api/echo:", await res3.json());

  // 查看响应头
  const res4 = await fetch(`${base}api/hello`);
  console.log("  响应状态:", res4.status, res4.statusText);
  console.log("  Content-Type:", res4.headers.get("content-type"));

  server.stop();
  console.log("  测试服务器已关闭");
}
