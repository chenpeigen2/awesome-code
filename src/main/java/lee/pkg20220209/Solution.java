package lee.pkg20220209;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Solution {
    //    https://leetcode-cn.com/problems/count-number-of-pairs-with-absolute-difference-k/
    public int countKDifference(int[] nums, int k) {
        // 1 2 2 1
        int[] cnts = new int[110];
        int n = nums.length, ans = 0;
        for (int t : nums) {
            if (t - k >= 1) ans += cnts[t - k];
            if (t + k <= 100) ans += cnts[t + k];
            cnts[t]++;
        }
        return ans;
    }

    public int countKDifference1(int[] nums, int k) {
        int count = 0;
        Arrays.sort(nums);
        Map<Integer, Integer> m = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            // 最大的那个数的 个数
            int value = m.getOrDefault(nums[i], 0) + 1;
            // 第二个数的那个value的个数
            int tmp = m.getOrDefault(nums[i] - k, 0);
            count = count + tmp;
            m.put(nums[i], value);
        }

        return count;
    }

    public static void main(String[] args) {
        var app = new Solution();
        int[] arr = new int[]{1, 2, 2, 1};
        app.countKDifference1(arr, 1);
    }
}
