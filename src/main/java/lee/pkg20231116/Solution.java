package lee.pkg20231116;

public class Solution {
    //    https://leetcode.cn/problems/longest-even-odd-subarray-with-threshold/?envType=daily-question&envId=2023-11-16
    public int longestAlternatingSubarray(int[] nums, int threshold) {
        int ans = 0;
        int nowCnt = 0;
        for (int i = 0; i < nums.length; i++) {
            if (nowCnt > 0) {
                if (nums[i] <= threshold && nums[i] % 2 != nums[i - 1] % 2) {
                    nowCnt++;
                } else {
                    ans = Math.max(ans, nowCnt);
                    nowCnt = 0;
                }
            }

            if (nowCnt == 0 && nums[i] <= threshold && nums[i] % 2 == 0) {
                nowCnt = 1;
            }

        }
        return Math.max(ans, nowCnt);
    }

    public int longestAlternatingSubarray1(int[] nums, int threshold) {
        int n = nums.length, ans = 0, i = 0;
        while (i < n) {
            if ((nums[i] % 2 != 0 || nums[i] > threshold)) {
                ++i;
                continue;
            }
            int j = i + 1, cur = nums[i] % 2;
            while (j < n) {
                if (nums[j] > threshold || nums[j] % 2 == cur) break;
                cur = nums[j++] % 2;
            }
            ans = Math.max(ans, j - i);
            i = j;
        }
        return ans;
    }
}
