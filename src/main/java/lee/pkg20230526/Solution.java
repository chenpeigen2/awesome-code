package lee.pkg20230526;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

public class Solution {
    //    https://leetcode.cn/problems/shortest-path-in-binary-matrix/
    public int shortestPathBinaryMatrix(int[][] grid) {
        if (grid[0][0] == 1) return -1;
        int n = grid.length;

        int[][] dist = new int[n][n];
        for (int i = 0; i < n; i++) {
            Arrays.fill(dist[i], Integer.MAX_VALUE);
        }

        Queue<int[]> queue = new ArrayDeque<>();

        queue.offer(new int[]{0, 0});
        dist[0][0] = 1;

        while (!queue.isEmpty()) {
            int[] arr = queue.poll();
            int x = arr[0], y = arr[1];
            if (x == n - 1 && y == n - 1) {
                return dist[x][y];
            }
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if (x + dx < 0 || x + dx >= n || y + dy < 0 || y + dy >= n) continue;
                    // 不可访问 || 已经访问过了
                    if (grid[x + dx][y + dy] == 1 || dist[x + dx][y + dy] <= dist[x][y] + 1) continue;
                    dist[x + dx][y + dy] = dist[x][y] + 1;
                    queue.offer(new int[]{x + dx, y + dy});
                }
            }
        }

        return -1;
    }
}
