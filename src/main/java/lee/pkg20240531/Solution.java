package lee.pkg20240531;

public class Solution {
    //    https://leetcode.cn/problems/find-missing-and-repeated-values/?envType=daily-question&envId=2024-05-31
    public int[] findMissingAndRepeatedValues(int[][] grid) {
        int[] ans = new int[2];
        boolean[] count = new boolean[grid.length * grid.length + 1];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid.length; j++) {
                if (count[grid[i][j]]) {
                    ans[0] = grid[i][j];
                }
                count[grid[i][j]] = true;
            }
        }
        for (int i = 1; i < count.length; i++) {
            if (!count[i]) {
                ans[1] = i;
                break;
            }
        }
        return ans;
    }
}
