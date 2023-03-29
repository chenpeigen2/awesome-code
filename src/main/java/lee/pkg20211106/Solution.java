package lee.pkg20211106;

import java.util.Arrays;

public class Solution {
    public int missingNumber(int[] nums) {
        Arrays.sort(nums);
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] != i) {
                return i;
            }
        }
        return nums.length;
    }

    public int missingNumber1(int[] nums) {
        int n = nums.length;
        int cur = 0, sum = n * (n + 1) / 2;
        for (int i : nums) cur += i;
        return sum - cur;
    }

    public int missingNumber2(int[] nums) {
        boolean[] f = new boolean[nums.length + 1];
        for (int i : nums) {
            f[i] = true;
        }
        for (int i = 0; i < nums.length; i++) {
            if (!f[i]) return i;
        }
        return nums.length;
    }
}