package lee.pkg20220224;

public class Solution {
    //    https://leetcode-cn.com/problems/where-will-the-ball-fall/
    public int[] findBall(int[][] grid) {
        int n = grid[0].length;
        int[] ans = new int[n];

        // n个球 ，逐个球计算
        for (int j = 0; j < n; j++) {
            // col = 2
            int col = j; // 球的初始位置
            for (int[] row : grid) {
                // 偏移量
                int dir = row[col];
                col += dir;
                if (col < 0 || col == n || dir != row[col]) {
                    col = -1;
                    break;
                }
            }

            ans[j] = col;
        }
        return ans;
    }
}
