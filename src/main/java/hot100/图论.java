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
        for (int r = 0; r < M; r++) {
            for (int c = 0; c < N; c++) {
                if (grid[r][c] == 1) {
                    count++;
                } else if (grid[r][c] == 2) {
                    queue.offer(new int[]{r, c});
                }
            }
        }

        int round = 0;
        while (count > 0 && !queue.isEmpty()) {
            round++;
            int n = queue.size();

            for (int i = 0; i < n; i++) {
                int[] orange = queue.poll();
                int r = orange[0];
                int c = orange[1];

                if (r - 1 >= 0 && grid[r - 1][c] == 1) {
                    grid[r - 1][c] = 2;
                    count--;
                    queue.offer(new int[]{r - 1, c});
                }

                if (r + 1 < M && grid[r + 1][c] == 1) {
                    grid[r + 1][c] = 2;
                    count--;
                    queue.add(new int[]{r + 1, c});
                }
                if (c - 1 >= 0 && grid[r][c - 1] == 1) {
                    grid[r][c - 1] = 2;
                    count--;
                    queue.add(new int[]{r, c - 1});
                }
                if (c + 1 < N && grid[r][c + 1] == 1) {
                    grid[r][c + 1] = 2;
                    count--;
                    queue.add(new int[]{r, c + 1});
                }
            }
        }
        if (count > 0) return -1;
        return round;
    }
}