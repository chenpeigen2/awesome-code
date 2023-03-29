package lee.pkg20230316;

import java.util.HashMap;

public class Solution {

    //    https://leetcode.cn/problems/count-subarrays-with-median-k/solution/deng-jie-zhuan-huan-pythonjavacgo-by-end-5w11/
    public int countSubarrays(int[] nums, int k) {
        // 1 2 3 4 -> 3
        // 1 2 3 4 5 -> 3

        // pos 就是我们所说的位置
        int pos = 0, n = nums.length;

        while (nums[pos] != k) ++pos;

        var cnt = new HashMap<Integer, Integer>();
        // 4只有一个值的情况
        cnt.put(0, 1); // i=pos 的时候 x 是 0

        for (int i = pos - 1, x = 0; i >= 0; i--) {
            x += nums[i] < k ? 1 : -1;
            cnt.merge(x, 1, Integer::sum);
        }

        // 4 or 3 4 且有可能无右边值的情况
        // i=pos 的时候 x 是 0，直接加到答案中，这样下面不是大于 k 就是小于 k
        int ans = cnt.get(0) + cnt.getOrDefault(-1, 0);

        for (int i = pos + 1, x = 0; i < n; i++) {
            x += nums[i] > k ? 1 : -1;
            // ll - lg = rg - rl - 1 偶数
            // ll - lg = rg - rl 奇数
            ans += cnt.getOrDefault(x, 0) + cnt.getOrDefault(x - 1, 0);
        }

        return ans;
    }
}
