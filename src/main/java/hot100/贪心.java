package hot100;

public class 贪心 {
    //    https://leetcode.cn/problems/best-time-to-buy-and-sell-stock/submissions/518165430/?envType=study-plan-v2&envId=top-100-liked
    public int maxProfit(int[] prices) {
        int min = prices[0];

        int res = 0;
        for (int price : prices) {
            res = Math.max(res, price - min);
            min = Math.min(min, price);
        }
        return res;
    }
}
