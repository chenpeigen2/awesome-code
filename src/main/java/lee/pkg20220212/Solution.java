package lee.pkg20220212;

import java.util.ArrayDeque;
import java.util.Queue;

public class Solution {
//    https://leetcode-cn.com/problems/number-of-enclaves/submissions/
    public int numEnclaves(int[][] g) {
        int m = g.length;
        int n = g[0].length;
        boolean[][] vis = new boolean[m][n];
        Queue<int[]> d = new ArrayDeque<>();
        // q init
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (i == 0 || j == 0 || i == m - 1 || j == n - 1) {
                    if (g[i][j] == 0) continue;
                    vis[i][j] = true;
                    d.offer(new int[]{i, j});
                }
            }
        }
        // the place we are about to visit
        int[][] dirs = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        // bfs start ans i get it
        while (!d.isEmpty()) {
            int[] poll = d.poll();
            int x = poll[0], y = poll[1];
            for (int[] di : dirs) {
                int nx = x + di[0], ny = y + di[1];
                if (nx < 0 || nx >= m || ny < 0 || ny >= n) continue;
                if (g[nx][ny] != 1) continue;
                if (vis[nx][ny]) continue;
                vis[nx][ny] = true;
                d.offer(new int[]{nx, ny});
            }
        }

        int ans = 0;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (g[i][j] == 1 && !vis[i][j]) ans++;
            }
        }
        return ans;
    }
}
