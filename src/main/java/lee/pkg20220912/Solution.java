package lee.pkg20220912;

import java.util.Arrays;

public class Solution {

    //    https://leetcode.cn/problems/special-array-with-x-elements-greater-than-or-equal-x/
    public int specialArray(int[] nums) {
        Arrays.sort(nums);
        int len = nums.length;
        int cnt = 0;
        for (int i = 0; i < len; i++) {
            if (cnt == nums[i]) continue;
            int leftCnt = len - i;
            if (leftCnt == nums[i]) return leftCnt;
            if (nums[i] > leftCnt && cnt < leftCnt) {
                return leftCnt;
            }
            cnt = nums[i];
        }
        return -1;
    }

    public static void main(String[] args) {
        var app = new Solution();
        var arr = new int[]{1, 0, 0, 6, 4, 9};
        app.specialArray(arr);
    }
}
