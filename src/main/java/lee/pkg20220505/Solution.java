package lee.pkg20220505;

public class Solution {
    //    https://leetcode-cn.com/problems/subarray-product-less-than-k/
    public int numSubarrayProductLessThanK(int[] nums, int k) {
        int n = nums.length, ret = 0;
        int prod = 1, i = 0;
        for (int j = 0; j < n; j++) {
            prod *= nums[j];
//            [1,2,3] avoiding
//            0
            while (i <= j && prod >= k) {
                prod /= nums[i];
                i++;
            }
            ret += j - i + 1;
        }
        return ret;
    }
}
