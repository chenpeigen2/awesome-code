package lee.pkg20221219;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Solution {
    //    https://leetcode.cn/problems/find-if-path-exists-in-graph/
    public boolean validPath(int n, int[][] edges, int source, int destination) {
        List<Integer>[] adj = new List[n];
        for (int i = 0; i < n; i++) {
            adj[i] = new ArrayList<>();
        }
        for (int[] edge : edges) {
            int x = edge[0], y = edge[1];
            adj[x].add(y);
            adj[y].add(x);
        }
        boolean[] visited = new boolean[n];
        Queue<Integer> queue = new ArrayDeque<>();

        queue.offer(source);
        visited[source] = true;

        while (!queue.isEmpty()) {
            int vertex = queue.poll();
            if (vertex == destination) break;
            for (int next : adj[vertex]) {
                if (!visited[next]) {
                    queue.offer(next);
                    visited[next] = true;
                }
            }
        }

        return visited[destination];
    }

    private int[] p;

    public boolean validPath1(int n, int[][] edges, int source, int destination) {
        p = new int[n];
        for (int i = 0; i < n; i++) {
            p[i] = i;
        }
        for (int[] e : edges) {
            p[find(e[0])] = find(e[1]);
        }

        return find(source) == find(destination);

    }

    /**
     * 最后查询 source 和 destination 的祖宗节点是否相同，相同则说明两个节点连通。
     * <a href="https://leetcode.cn/problems/find-if-path-exists-in-graph/solutions/2025571/by-lcbin-96dp/">...</a>
     *
     * @param x
     * @return
     */
    private int find(int x) {
        if (p[x] != x) {
            p[x] = find(p[x]);
        }
        return p[x];
    }

    /**
     * 我们将图中的每个强连通分量视为一个集合，强连通分量中任意两点均可达，如果两个点 source\textit{source}source 和 destination\textit{destination}destination 处在同一个强连通分量中，则两点一定可连通，因此连通性问题可以使用并查集解决。
     * <p>
     * 并查集初始化时，nnn 个顶点分别属于 nnn 个不同的集合，每个集合只包含一个顶点。初始化之后遍历每条边，由于图中的每条边均为双向边，因此将同一条边连接的两个顶点所在的集合做合并。
     * <p>
     * 遍历所有的边之后，判断顶点 source\textit{source}source 和顶点 destination\textit{destination}destination 是否在同一个集合中，如果两个顶点在同一个集合则两个顶点连通，如果两个顶点所在的集合不同则两个顶点不连通。
     * <p>
     * 作者：力扣官方题解
     * 链接：<a href="https://leetcode.cn/problems/find-if-path-exists-in-graph/solutions/2024085/xun-zhao-tu-zhong-shi-fou-cun-zai-lu-jin-d0q0/">...</a>
     * 来源：力扣（LeetCode）
     * 著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
     */
    public boolean validPath2(int n, int[][] edges, int source, int destination) {
        if (source == destination) {
            return true;
        }
        UnionFind uf = new UnionFind(n);
        for (int[] edge : edges) {
            uf.uni(edge[0], edge[1]);
        }
        return uf.connect(source, destination);
    }

    private static class UnionFind {
        private int[] parent;
        private int[] rank;

        public UnionFind(int n) {
            parent = new int[n];
            rank = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = i;
            }
        }

        public void uni(int x, int y) {
            int rootx = find(x);
            int rooty = find(y);
            if (rootx != rooty) {
                if (rank[rootx] > rank[rooty]) {
                    parent[rooty] = rootx;
                } else if (rank[rootx] < rank[rooty]) {
                    parent[rootx] = rooty;
                } else {
                    parent[rooty] = rootx;
                    rank[rootx]++;
                }
            }
        }

        public int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]);
            }
            return parent[x];
        }

        public boolean connect(int x, int y) {
            return find(x) == find(y);
        }
    }
}
