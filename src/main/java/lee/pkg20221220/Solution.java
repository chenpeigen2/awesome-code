package lee.pkg20221220;

import java.util.Arrays;

public class Solution {
    //    https://leetcode.cn/problems/minimum-limit-of-balls-in-a-bag/solutions/2026576/javac-er-fen-cha-zhao-by-tizzi-t04d/
    public int minimumSize(int[] nums, int op) {
        int l = 1, r = Arrays.stream(nums).max().getAsInt();
        while (l < r) {
            int mid = (l + r) / 2;
            if (check(mid, nums, op)) r = mid;
            else l = mid + 1;
        }
        return r;
    }

    /**
     * true: 分的太大了，所以目标值在上半部分
     */
    boolean check(int mid, int[] nums, int op) {
        for (int x : nums) {
            op -= (x - 1) / mid;
        }
        return op >= 0;
    }
}
