package lee.pkg20220611;

public class Solution {
    //    https://leetcode.cn/problems/flip-string-to-monotone-increasing/
    public int minFlipsMonoIncr(String s) {
//        1 两种数值，我们知道最后的目标序列形如 000...000、000...111 或 111...111 的形式。
        char[] cs = s.toCharArray();
        int n = cs.length, ans = n;
        int[] sum = new int[n + 1];
        for (int i = 1; i <= n; i++) sum[i] = sum[i - 1] + (cs[i - 1] - '0');
        for (int i = 1; i <= n; i++) {
//            分割点 idxidx 左边有多少个 1
//            分割点 idxidx 右边有多少个 0

            int l = sum[i - 1], r = (n - i) - (sum[n] - sum[i]);
            ans = Math.min(ans, l + r);
        }
        return ans;
    }
}
