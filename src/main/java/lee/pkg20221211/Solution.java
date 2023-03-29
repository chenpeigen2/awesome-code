package lee.pkg20221211;

public class Solution {
    //    https://leetcode.cn/problems/minimum-operations-to-make-the-array-increasing/
    public int minOperations(int[] nums) {
        int pre = nums[0] - 1, res = 0;
        for (int num: nums) {
            pre = Math.max(pre+1,num);
            res += (pre-num);
        }
        return res;
    }
}
