package lee.pkg20210919;

public class Solution {
    public int minSteps(int n) {

        if (n == 1) {
            return 0;
        }

        int[] dp = new int[n + 1];
        dp[1] = 0;
        for (int i = 2; i <= n; i++) {

            if (i % 2 == 0) {
                dp[i] = dp[i / 2] + 2;
            }
            for (int j = 3; j <= i; j += 2) {
                if (i % j == 0) {
                    // j 代表份数
                    dp[i] = dp[i / j] + 1 + (j - 1);
                    break;
                }
            }
        }

        return dp[n];
    }

    public static void main(String[] args) {
        var result = new Solution().minSteps(9);
        System.out.println(result);
        System.out.println(3 % 3);
    }
}
