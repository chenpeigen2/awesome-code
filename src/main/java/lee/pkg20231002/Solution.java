package lee.pkg20231002;

public class Solution {
    //    https://leetcode.cn/problems/best-time-to-buy-and-sell-stock-ii/?envType=daily-question&envId=Invalid%20Date
    public int maxProfit(int[] prices) {
        int profit = 0;
        for (int i = 1; i < prices.length;i++) {
            int tmp = prices[i] - prices[i - 1];
            if (tmp > 0 ) profit += tmp;
        }
        return profit;
    }
}
