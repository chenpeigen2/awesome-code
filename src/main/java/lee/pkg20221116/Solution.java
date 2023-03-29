package lee.pkg20221116;

public class Solution {
    //    https://leetcode.cn/problems/global-and-local-inversions/description/
    public boolean isIdealPermutation(int[] nums) {
        int n = nums.length, minSuff = nums[n - 1];
        for (int i = n - 3; i >= 0; i--) {
            if (nums[i] > minSuff) return false;
            minSuff = Math.min(minSuff, nums[i + 1]);
        }
        return true;
    }
}
