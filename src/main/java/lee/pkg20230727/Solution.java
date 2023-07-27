package lee.pkg20230727;

import java.util.Arrays;

public class Solution {
    //    https://leetcode.cn/problems/delete-greatest-value-in-each-row/
    public int deleteGreatestValue(int[][] grid) {
        int ans = 0;

        int len = grid.length;
        for (int[] g : grid) {
            Arrays.sort(g);
        }

        for (int j = 0; j < grid[0].length; j++) {
            int mx = 0;
            for (int i = 0; i < len; i++) {
                mx = Math.max(mx, grid[i][j]);
            }
            ans += mx;
        }

        return ans;
    }
}
