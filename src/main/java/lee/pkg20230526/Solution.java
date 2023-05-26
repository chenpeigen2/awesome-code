package lee.pkg20230526;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

public class Solution {
    //    https://leetcode.cn/problems/shortest-path-in-binary-matrix/
    public int shortestPathBinaryMatrix(int[][] grid) {
        if (grid[0][0] == 1) return -1;

        // indicate the dist walking step
        int len = grid.length;
        int[][] dist = new int[len][len];
        for (int i = 0; i < len; i++) {
            Arrays.fill(dist[i], Integer.MAX_VALUE);
        }
        dist[0][0] = 1;

        Queue<int[]> queue = new ArrayDeque<>();

        queue.offer(new int[]{0, 0});

        while (!queue.isEmpty()) {
            int[] arr = queue.poll();
            int x = arr[0];
            int y = arr[1];
            if (x == len - 1 && y == len - 1) return dist[x][y];
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if (x + dx < 0 || x + dx >= len || y + dy < 0 || y + dy >= len) continue;
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
