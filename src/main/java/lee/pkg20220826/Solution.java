package lee.pkg20220826;

import java.util.Arrays;

public class Solution {
    //    https://leetcode.cn/problems/maximum-product-of-two-elements-in-an-array/
    public int maxProduct(int[] nums) {
        Arrays.sort(nums);
        int len = nums.length;
        return (nums[len - 1] - 1) * (nums[len - 2] - 1);
    }

    public int maxProduct1(int[] nums) {
        int a = -1, b = -1;
        for (int x : nums) {
            if (x > a) {
                b = a;
                a = x;
            } else if (x > b) {
                b = x;
            }
        }
        return (a - 1) * (b - 1);
    }
}
