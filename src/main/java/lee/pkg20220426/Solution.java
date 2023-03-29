package lee.pkg20220426;

public class Solution {
    //    https://leetcode-cn.com/problems/projection-area-of-3d-shapes/
    public int projectionArea(int[][] grid) {
        int[] rows = new int[grid.length];
        int[] cols = new int[grid.length];
        int ans = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] > 0) {
                    ans += 1;
                    rows[i] = Math.max(rows[i], grid[i][j]);
                    cols[j] = Math.max(cols[j], grid[i][j]);
                }
            }
        }
        for (int row : rows) {
            ans += row;
        }
        for (int col : cols) {
            ans += col;
        }

        return ans;
    }
}
