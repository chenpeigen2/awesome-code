package lee.pkg20220829;

import java.util.Arrays;

public class Solution {
    //    https://leetcode.cn/problems/shuffle-the-array/
    public int[] shuffle(int[] nums, int n) {
        int[] ans = new int[2 * n];
        for (int i = 0; i < n; i++) {
            ans[2 * i] = nums[i];
            ans[2 * i + 1] = nums[i + n];
        }
        return ans;
    }
}
