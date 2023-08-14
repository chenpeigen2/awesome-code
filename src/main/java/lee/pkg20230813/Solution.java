package lee.pkg20230813;

import java.util.Arrays;

public class Solution {

    //    https://leetcode.cn/problems/merge-sorted-array/
    public void merge(int[] nums1, int m, int[] nums2, int n) {
        for (int i = 0; i < n; i++) {
            nums1[m + i] = nums2[i];
        }
        Arrays.sort(nums1);
    }
}
