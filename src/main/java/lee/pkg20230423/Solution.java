package lee.pkg20230423;

import java.util.Arrays;

public class Solution {
    //    https://leetcode.cn/problems/filling-bookcase-shelves/
    public int minHeightShelves(int[][] books, int shelfWidth) {
        int n = books.length;
        int[] dp = new int[n + 1];
        Arrays.fill(dp, 1000000);
        dp[0] = 0;
        for (int i = 0; i < n; i++) {
            int maxHeight = 0, curWidth = 0;
            // 从上一个比如说从i开始找
            // 搞清楚dp 和 books指代的东西不一样
            for (int j = i; j >= 0; j--) {
                curWidth += books[j][0];
                if (curWidth > shelfWidth) break;
                maxHeight = Math.max(maxHeight, books[j][1]);
                dp[i + 1] = Math.min(dp[i + 1], dp[j] + maxHeight);
            }
        }
        return dp[n];
    }

    public static void main(String[] args) {
        var app = new Solution();
        app.minHeightShelves(new int[][]{{2, 1}, {3, 3}}, 4);
    }
}
