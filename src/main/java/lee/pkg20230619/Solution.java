package lee.pkg20230619;

import java.util.Arrays;

public class Solution {
    //    https://leetcode.cn/problems/greatest-sum-divisible-by-three/solution/20xing-dai-ma-qing-song-shuang-bai-yi-ka-hc1k/
    public int maxSumDivThree(int[] nums) {
        int n = nums.length;

        // 每个存的是对3取余的为 0/1/2 的最大值的和
        int[] remainer = new int[3];

        for (int i = 0; i < n; i++) {
            int a, b, c;
            // 这里体现了持续求和
            a = remainer[0] + nums[i];
            b = remainer[1] + nums[i];
            c = remainer[2] + nums[i];
            // a要去的地方，是 a 或 现在那里的最大
            // 这里体现了持续存下最大和
            remainer[a % 3] = Math.max(remainer[a % 3], a);
            remainer[b % 3] = Math.max(remainer[b % 3], b);
            remainer[c % 3] = Math.max(remainer[c % 3], c);
        }

        return remainer[0];

    }

    public static void main(String[] args) {
        int[] ar = new int[]{3, 6, 5, 1, 8};
        var solu = new Solution();
        var ans = solu.maxSumDivThree(ar);
        System.out.println(ans);
    }
}
