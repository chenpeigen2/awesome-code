package lee.pkg20221124;

public class Solution {
    //    https://leetcode.cn/problems/number-of-subarrays-with-bounded-maximum/
    public int numSubarrayBoundedMax(int[] nums, int left, int right) {
        int res = 0, last2 = -1, last1 = -1;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] >= left && nums[i] <= right) last1 = i;
            else if (nums[i] > right) {
                last2 = i;
                last1 = -1;
            }
            if (last1 != -1) res += (last1 - last2);
        }
        return res;
    }

    public static void main(String[] args) {
        var app = new Solution();
        // -1, 0, 1, 2
        // 0 1 2
        // 1 2
        // 2
        // 3 - (-1) = 4

        var ans = app.numSubarrayBoundedMax(new int[]{-1, 0, 1, 2}, 2, 4);
        System.out.println(ans);
    }
}
