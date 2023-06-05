package lee.pkg20230605;

public class Solution {
    //    https://leetcode.cn/problems/apply-operations-to-an-array/
    public int[] applyOperations(int[] nums) {
        int len = nums.length;
        for (int i = 1; i < len; i++) {
            if (nums[i - 1] == nums[i]) {
                nums[i - 1] *= 2;
                nums[i] = 0;
            }
        }
        int[] res = new int[len];

        int k = 0;

        for (int num : nums) {
            if (num != 0) {
                res[k] = num;
                k++;
            }
        }

        return res;
    }
}
