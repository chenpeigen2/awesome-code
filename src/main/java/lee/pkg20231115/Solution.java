package lee.pkg20231115;

import java.util.Arrays;

public class Solution {

    //    https://leetcode.cn/problems/maximum-sum-with-exactly-k-elements/?envType=daily-question&envId=2023-11-15
    public int maximizeSum(int[] nums, int k) {
        int sum = 0;
        int len = nums.length;
        for (int i = 0; i < k; i++) {
            Arrays.sort(nums);
            sum += nums[len - 1]++;
        }
        return sum;
    }

    public int maximizeSum1(int[] nums, int k) {
        int max = 0;
        for (int x : nums) max = Math.max(max, x);
        return k * (max + max + k - 1) / 2;
    }

    public static void main(String[] args) {
        int[] nums = new int[]{5, 5, 5};
        var app = new Solution();
        var ans = app.maximizeSum(nums, 2);
        System.out.println(ans);

    }
}
