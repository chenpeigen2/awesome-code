package lee.pkg20240314;

public class Solution {
    //    https://leetcode.cn/problems/largest-element-in-an-array-after-merge-operations/?envType=daily-question&envId=2024-03-13
    public long maxArrayValue(int[] nums) {
        int len = nums.length;
        long ans = nums[len - 1];
        for (int i = len - 2; i >= 0; i--) {
            if (nums[i] <= ans) {
                ans = nums[i] + ans;
            } else {
                ans = nums[i];
            }
        }
        return ans;
    }

    public long maxArrayValue1(int[] nums) {
        int len = nums.length;
        long ans = nums[len - 1];
        for (int i = len - 2; i >= 0; i--) {
            if (nums[i] <= ans) {
                ans = nums[i] + ans;
            } else {
                ans = nums[i];
            }
        }
        return ans;
    }

    public long maxArrayValue2(int[] nums) {
        int len = nums.length;
        long ans = nums[len - 1];
        for (int i = len - 1; i >= 0; i--) {
            int prev = i - 1;
            if (prev < 0) break;
            int cur = i;
            if (nums[prev] <= ans) {
                ans += nums[prev];
            } else {
                ans = nums[prev];
            }
        }
        return ans;
    }
}
