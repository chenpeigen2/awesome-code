package lee.pkg20240505;

public class Solution {

    //    https://leetcode.cn/problems/defuse-the-bomb/?envType=daily-question&envId=2024-05-05
    public int[] decrypt(int[] code, int k) {
        int n = code.length;
        int[] ans = new int[n];
        if (k == 0) return ans;
        int[] sum = new int[n * 2 + 1];
        for (int i = 1; i <= 2 * n; i++) {
            sum[i] += sum[i - 1] + code[(i - 1) % n];
        }
        for (int i = 1; i <= n; i++) {
            if (k < 0) ans[i - 1] = sum[i - 1 + n] - sum[i - 1 + n + k];
            else ans[i - 1] = sum[i + k] - sum[i];
        }
        return ans;
    }
}
