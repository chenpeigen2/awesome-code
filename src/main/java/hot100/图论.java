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

    //    https://leetcode.cn/problems/rotting-oranges/?envType=study-plan-v2&envId=top-100-liked
    public int orangesRotting(int[][] grid) {
        int M = grid.length;
        int N = grid[0].length;

        Queue<int[]> queue = new ArrayDeque<>();

        int count = 0;
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                if (grid[i][j] == 1) {
                    count++;
                } else if (grid[i][j] == 2) {
                    queue.offer(new int[]{i, j});
                }
            }
        }

        int round = 0;
        while (count > 0 && !queue.isEmpty()) {
            round++;
            int sz = queue.size();
            for (int i = 0; i < sz; i++) {
                int[] position = queue.poll();
                int x = position[0];
                int y = position[1];

                if (x - 1 >= 0 && grid[x - 1][y] == 1) {
                    grid[x - 1][y] = 2;
                    count--;
                    queue.offer(new int[]{x - 1, y});
                }

                if (x + 1 < M && grid[x + 1][y] == 1) {
                    grid[x + 1][y] = 2;
                    count--;
                    queue.offer(new int[]{x + 1, y});
                }

                if (y - 1 >= 0 && grid[x][y - 1] == 1) {
                    grid[x][y - 1] = 2;
                    count--;
                    queue.offer(new int[]{x, y - 1});
                }

                if (y + 1 < N && grid[x][y + 1] == 1) {
                    grid[x][y + 1] = 2;
                    count--;
                    queue.offer(new int[]{x, y + 1});
                }
            }
        }

        if (count > 0) return -1;
        return round;

    }
}