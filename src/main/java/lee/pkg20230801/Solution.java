package lee.pkg20230801;

import java.util.Arrays;

public class Solution {
    //    https://leetcode.cn/problems/power-of-heroes/solution/gong-xian-fa-pythonjavacgo-by-endlessche-d4jx/
    public int sumOfPower(int[] nums) {
        final long MOD = (long) 1e9 + 7;
        Arrays.sort(nums);
        long ans = 0, s = 0;
        for (long x : nums) { // x 作为最大值
            ans = (ans + x * x % MOD * (x + s)) % MOD; // 中间模一次防止溢出
            s = (s * 2 + x) % MOD; // 递推计算下一个 s
        }
        return (int) ans;
    }
}
