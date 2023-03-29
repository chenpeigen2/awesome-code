package lee.pkg20211105;

import java.util.HashMap;
import java.util.Map;

public class Solution {

    public int longestSubsequence(int[] arr, int difference) {
        int ans = 0;
        int[] dp = new int[40001];
        for (int num : arr) {
            dp[num + 20000] = dp[num + 20000 - difference] + 1;
            ans = Math.max(ans, dp[num + 20000]);
        }
        return ans;
    }

    public int longestSubsequence1(int[] arr, int difference) {
        int ans = 1;
        Map<Integer, Integer> m = new HashMap<>();
        for (int i : arr) {
            m.put(i, m.getOrDefault(i - difference, 0) + 1);
            ans = Math.max(ans, m.get(i));
        }
        return ans;
    }
}
