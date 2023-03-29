package lee.pkg20220113;

import java.util.Arrays;

public class Solution {
//    https://leetcode-cn.com/problems/largest-number-at-least-twice-of-others/

    public int dominantIndex(int[] nums) {
        int first = 0, second = 0;
        int idx = -1;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] > second) {
                first = second;
                second = nums[i];
                idx = i;
            } else if (nums[i] > first) {
                first = nums[i];
            }
        }
        System.out.println(first);
        System.out.println(second);
        if (second >= first * 2) {
            return idx;
        }
        idx = -1;
        return idx;
    }

    public int dominantIndex1(int[] nums) {
        int n = nums.length;
        if (n == 1) return 0;
        int a = -1, b = 0;
        for (int i = 1; i < n; i++) {
            if (nums[i] > nums[b]) {
                a = b;
                b = i;
            } else if (a == -1 || nums[i] > nums[a]) {
                a = i;
            }
        }
        return nums[b] >= nums[a] * 2 ? b : -1;
    }

    public static void main(String[] args) {
        var app = new Solution();
        var ans = app.dominantIndex(new int[]{
                0, 0, 3, 2
        });
        System.out.println(ans);
    }
}
