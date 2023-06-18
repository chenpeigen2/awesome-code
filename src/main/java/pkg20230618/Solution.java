package pkg20230618;

import java.util.ArrayDeque;
import java.util.Queue;

public class Solution {
    // https://leetcode.cn/problems/number-of-closed-islands/solution/tong-ji-feng-bi-dao-yu-de-shu-mu-by-leet-ofh3/
    static int[][] dir = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    public int closedIsland(int[][] grid) {
        int m = grid.length;
        int n = grid[0].length;

        int ans = 0;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 0) {
                    Queue<int[]> qu = new ArrayDeque<>();
                    qu.offer(new int[]{i, j});
                    grid[i][j] = 1;
                    boolean closed = true;
                    while (!qu.isEmpty()) {
                        int[] arr = qu.poll();
                        int cx = arr[0];
                        int cy = arr[1];
                        if (cx == 0 || cy == 0 || cx == m - 1 || cy == n - 1) {
                            closed = false;
                        }
                        for (int d = 0; d < 4; d++) {
                            int nx = cx + dir[d][0];
                            int ny = cy + dir[d][1];
                            if (nx < 0 || nx >= m || ny < 0 || ny >= n) continue;
                            if (grid[nx][ny] == 1) continue;
                            grid[nx][ny] = 1;
                            qu.offer(new int[]{nx, ny});
                        }
                    }
                    if (closed) ans++;
                }
            }
        }

        return ans;
    }
}
