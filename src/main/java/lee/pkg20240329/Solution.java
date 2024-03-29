package lee.pkg20240329;

public class Solution {
    //    https://leetcode.cn/problems/minimum-sum-of-mountain-triplets-i/description/?envType=daily-question&envId=2024-03-29
    public int minimumSum(int[] nums) {
        int len = nums.length;
        int[] left = new int[len];

        left[0] = nums[0];
        for (int i = 1; i < len; i++) {
            left[i] = Math.min(left[i - 1], nums[i]);
        }

        int res = Integer.MAX_VALUE;
        int right = nums[len - 1];
        for (int i = len - 1; i > 0; i--) {
            if (nums[i] > left[i] && nums[i] > right) {
                res = Math.min(res, nums[i] + right + left[i]);
            }
            right = Math.min(right, nums[i]);
        }

        return res == Integer.MAX_VALUE ? -1 : res;
    }
}
