package lee.pkg20240325;


public class Solution {
    //    https://leetcode.cn/problems/coin-change-ii/?envType=daily-question&envId=2024-03-25
    public int change(int amount, int[] coins) {
        int[] dp = new int[amount + 1];
        dp[0] = 1;

        for (int coin : coins) {
            for (int i = coin; i <= amount; i++) {
                dp[i] += dp[i - coin];
            }
        }

        return dp[amount];
    }
}
