package lee.pkg20220519;

import java.util.Arrays;

public class Solution {

    //    https://leetcode.cn/problems/minimum-moves-to-equal-array-elements-ii/
    public int minMoves2(int[] nums) {
        Arrays.sort(nums);
        int len = nums.length;
        int mid = nums[len / 2];
        int ans = 0;
        for (int i : nums) {
            ans += Math.abs(mid - i);
        }
        return ans;
    }

    public static void main(String[] args) {
        int[] arr = new int[]{1, 0, 0, 8, 6};
        var app = new Solution();
        var ans = app.minMoves2(arr);

        System.out.println(ans);
    }
}
