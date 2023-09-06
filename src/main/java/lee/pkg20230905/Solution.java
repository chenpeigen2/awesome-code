package lee.pkg20230905;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Solution {
    //    https://leetcode.cn/problems/form-smallest-number-from-two-digit-arrays/?envType=daily-question&envId=2023-09-05
    public int minNumber(int[] nums1, int[] nums2) {
        Arrays.sort(nums1);
        Set<Integer> set = new HashSet<>();
        for (int num : nums2) {
            set.add(num);
        }
        for (int num : nums1) {
            if (set.contains(num)) {
                return num;
            }
        }
        int a = Arrays.stream(nums1).min().getAsInt();
        int b = Arrays.stream(nums2).min().getAsInt();
        if (a < b) {
            return a * 10 + b;
        } else {
            return b * 10 + a;
        }
    }

    public static void main(String[] args) {
        var app = new Solution();
        var ans = app.minNumber(new int[]{3, 5, 2, 6}, new int[]{3, 1, 7});
        System.out.println(ans);
    }
}
