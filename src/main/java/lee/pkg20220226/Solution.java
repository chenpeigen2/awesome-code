package lee.pkg20220226;

public class Solution {
    //    https://leetcode-cn.com/problems/maximum-difference-between-increasing-elements/
    public int maximumDifference(int[] nums) {
        int ans = -1;
        int pre = nums[0];
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] > pre) {
                ans = Math.max(ans, nums[i] - pre);
            } else {
                pre = nums[i];
            }
        }
        return ans;
    }
}
