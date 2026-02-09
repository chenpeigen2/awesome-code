package 面试题目;

import java.util.ArrayDeque;
import java.util.Queue;

public class 图论 {

    // https://leetcode.cn/problems/number-of-islands/submissions/608581372/?envType=study-plan-v2&envId=top-100-liked

    /**
     * 计算二维网格中的岛屿数量
     * 岛屿由相邻的'1'组成，相邻指水平或垂直方向
     *
     * @param grid 二维字符数组，'1'表示陆地，'0'表示水
     * @return 岛屿的数量
     */
    public int numIslands(char[][] grid) {
        // 初始化岛屿计数器
        int count = 0;

        // 遍历整个网格
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                // 如果发现未访问的陆地
                if (grid[i][j] == '1') {
                    // 使用BFS标记整座岛屿的所有陆地
                    bfs(grid, i, j);
                    // 增加岛屿计数
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * 使用广度优先搜索(BFS)遍历并标记一座岛屿
     * 将访问过的陆地标记为'0'以避免重复计算
     *
     * @param grid 二维字符数组
     * @param i    起始行索引
     * @param j    起始列索引
     */
    private void bfs(char[][] grid, int i, int j) {
        // 创建队列存储待访问的坐标
        Queue<int[]> queue = new ArrayDeque<>();
        // 将起始坐标加入队列
        queue.offer(new int[]{i, j});

        // 当队列不为空时继续搜索
        while (!queue.isEmpty()) {
            // 取出队列头部元素
            int[] poll = queue.poll();
            int x = poll[0];
            int y = poll[1];

            // 检查坐标是否有效且为未访问的陆地
            if (x >= 0 && x < grid.length
                    && y >= 0 && y < grid[0].length
                    && grid[x][y] == '1') {
                // 标记为已访问
                grid[x][y] = '0';
                // 将四个方向的相邻坐标加入队列
                queue.offer(new int[]{x + 1, y}); // 下
                queue.offer(new int[]{x - 1, y}); // 上
                queue.offer(new int[]{x, y + 1}); // 右
                queue.offer(new int[]{x, y - 1}); // 左
            }
        }
    }


    // https://leetcode.cn/problems/rotting-oranges/description/?envType=study-plan-v2&envId=top-100-liked
    /**
     * 计算腐烂橘子使所有新鲜橘子腐烂所需的最短时间
     * 使用多源BFS模拟腐烂过程
     *
     * @param grid 二维数组，0表示空单元格，1表示新鲜橘子，2表示腐烂橘子
     * @return 所需的最短分钟数，如果无法使所有橘子腐烂则返回-1
     */
    public int orangesRotting(int[][] grid) {
        int M = grid.length;
        int N = grid[0].length;
        Queue<int[]> queue = new ArrayDeque<>();
        int freshCount = 0;
        
        // 初始化：统计新鲜橘子数量并将初始腐烂橘子加入队列
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
             if (grid[i][j] == 1) {
                    freshCount++;
                } else if (grid[i][j] == 2) {
                    queue.offer(new int[]{i, j});
                }
            }
        }
        
        // 如果没有新鲜橘子，直接返回0
        if (freshCount == 0) {
            return 0;
        }
        
        int minutes = 0;
        
        // BFS遍历，模拟腐烂过程
        while (freshCount > 0 && !queue.isEmpty()) {
            minutes++;
            int currentSize = queue.size();
            
            // 处理当前轮次的所有腐烂橘子
            for (int i = 0; i < currentSize; i++) {
                int[] current = queue.poll();
                int x = current[0];
                int y = current[1];
                
                // 检查四个方向的相邻格子
                // 上方
                if (x - 1 >= 0 && grid[x - 1][y] == 1) {
                    grid[x - 1][y] = 2;
                    queue.offer(new int[]{x - 1, y});
                    freshCount--;
                }
                // 下方
                if (x + 1 < M && grid[x + 1][y] == 1) {
                    grid[x + 1][y] = 2;
                    queue.offer(new int[]{x + 1, y});
                    freshCount--;
                }
                // 左方
                if (y - 1 >= 0 && grid[x][y - 1] == 1) {
                    grid[x][y - 1] = 2;
                    queue.offer(new int[]{x, y - 1});
                    freshCount--;
                }
                // 右方
                if (y + 1 < N && grid[x][y + 1] == 1) {
                    grid[x][y + 1] = 2;
                    queue.offer(new int[]{x, y + 1});
                    freshCount--;
                }
            }
        }
        
        // 如果还有新鲜橘子剩余，说明无法全部腐烂
        return freshCount > 0 ? -1 : minutes;
    }   

}
