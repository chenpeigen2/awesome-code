package lee.pkg20211213;

public class Solution {
    public int maxIncreaseKeepingSkyline(int[][] grid) {
        int n = grid.length, m = grid[0].length;
        int[] r = new int[n], c = new int[m];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                r[i] = Math.max(r[i], grid[i][j]);
                c[j] = Math.max(c[j], grid[i][j]);
            }
        }

        int ans = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                ans += Math.min(r[i], c[j]) - grid[i][j];
            }
        }
        return ans;
    }

    public static void main(String[] args) {
        int[][] arr = new int[][]{
                {3, 0, 8, 4}, {2, 4, 5, 7}, {9, 2, 6, 3}, {0, 3, 1, 0}
        };
        var app = new Solution();
        app.maxIncreaseKeepingSkyline(arr);
    }
}
