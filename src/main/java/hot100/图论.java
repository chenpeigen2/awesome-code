package hot100;

import java.util.ArrayDeque;
import java.util.Queue;

public class 图论 {
    //    https://leetcode.cn/problems/number-of-islands/?envType=study-plan-v2&envId=top-100-liked
    public int numIslands(char[][] grid) {
        int count = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == '1') {
                    bfs(grid, i, j);
                    count++;
                }
            }
        }
        return count;
    }

    private void bfs(char[][] grid, int i, int j) {
        Queue<int[]> queue = new ArrayDeque<>();
        queue.offer(new int[]{i, j});
        while (!queue.isEmpty()) {
            int[] point = queue.poll();
            int x = point[0];
            int y = point[1];
            if (x >= 0 && x < grid.length && y >= 0 && y < grid[0].length && grid[x][y] == '1') {
                grid[x][y] = 0;
                queue.offer(new int[]{x + 1, y});
                queue.offer(new int[]{x - 1, y});
                queue.offer(new int[]{x, y + 1});
                queue.offer(new int[]{x, y - 1});
            }
        }
    }
}
