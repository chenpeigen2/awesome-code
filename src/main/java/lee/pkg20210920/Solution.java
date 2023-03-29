package lee.pkg20210920;

public class Solution {

    // 1 2 4 3 5 4 7 2

    public int findNumberOfLIS(int[] nums) {
        int n = nums.length, maxLen = 0, ans = 0;
        // 最长长度以 dp[i]结尾的
        int[] dp = new int[n];
        // 最长长度的数量，即路线数量,以nums[i] attach到的最大长度的数量
        int[] cnt = new int[n];
        for (int i = 0; i < n; i++) {
            dp[i] = 1;
            cnt[i] = 1;

            for (int j = 0; j < i; j++) {
                // 可以attach到父亲的数量
                if (nums[i] > nums[j]) {
                    if (dp[j] + 1 > dp[i]) {
                        dp[i] = dp[j] + 1;
                        cnt[i] = cnt[j];
                    } else if (dp[j] + 1 == dp[i]) {
                        // 1 2 2 3 条件
                        cnt[i] += cnt[j];
                    }
                }
            }

            if (dp[i] > maxLen) {
                // 1 2 2 3
                maxLen = dp[i];
                ans = cnt[i];
            } else if (dp[i] == maxLen) {
                // 1 2 4 3 情况
                ans += cnt[i];
            }

        }

        return ans;
    }


    public static void main(String[] args) {
        var app = new Solution();

        int[] arr = new int[]{1, 2, 2, 3};
        var result = app.findNumberOfLIS(arr);
        System.out.println(result);
    }
}
