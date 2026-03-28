/**
 * Demo 1: HTTP 服务器
 *
 * Bun 内置了高性能 HTTP 服务器，使用 Bun.serve() 即可启动。
 * 支持路由匹配、参数提取、静态文件服务等。
 */
export async function demoHttpServer() {
  // 启动一个临时服务器用于演示
  const server = Bun.serve({
    port: 0, // 随机可用端口
    routes: {
      // 静态路由 - 直接返回 Response
      "/": new Response("Welcome to Bun HTTP Server!"),

      // 动态路由 - 带参数
      "/users/:id": (req) => {
        return Response.json({
          userId: req.params.id,
          message: `获取用户 ${req.params.id} 的信息`,
        });
      },

      // POST 请求处理
      "/api/echo": {
        POST: async (req) => {
          const body = await req.json();
          return Response.json({ echo: body });
        },
      },

      // 查询参数
      "/search": (req) => {
        const url = new URL(req.url);
        const q = url.searchParams.get("q") ?? "";
        return Response.json({ query: q, results: [] });
      },
    },

    // 未匹配路由的 fallback
    fetch(req) {
      return new Response("Not Found", { status: 404 });
    },
  });

  console.log(`  服务器启动: ${server.url}`);

  // 测试各个路由
  let res = await fetch(`${server.url}`);
  console.log(`  GET /         => ${res.status} ${await res.text()}`);

  res = await fetch(`${server.url}/users/42`);
  console.log(`  GET /users/42 => ${res.status}`, await res.json());

  res = await fetch(`${server.url}/search?q=bun`);
  console.log(`  GET /search   => ${res.status}`, await res.json());

  res = await fetch(`${server.url}/api/echo`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ hello: "world" }),
  });
  console.log(`  POST /api/echo => ${res.status}`, await res.json());

  // 停止服务器
  server.stop();
  console.log("  服务器已关闭");
}
