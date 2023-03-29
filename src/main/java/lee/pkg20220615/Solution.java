package lee.pkg20220615;

import java.util.Arrays;

public class Solution {
    //    https://leetcode.cn/problems/find-k-th-smallest-pair-distance/
    // 第k小
    public int smallestDistancePair(int[] nums, int k) {
        Arrays.sort(nums);
        int n = nums.length, left = 0, right = nums[n - 1] - nums[0];
        // 【0，max-min】
        while (left < right) {
            // 对于当前mid，计算所有距离<= mid的数量
            int mid = (left + right) >> 1;
            int cnt = 0;
            // end idx j
            for (int j = 0; j < n; j++) {
                int i = binarySearch(nums, j, nums[j] - mid);
                // 0 1 2 3 4
                // h       h
                // cnt should be [4,0] [4,1] [4,2] [4,3] = 4 - 0 = 4 对
                cnt += j - i;
            }
            if (cnt < k) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }
        return right;
    }

    // end can be 【0 - len(n)】
    // 2 *** 3 return the 4
    // ** means find
    // but we return the 2-idx
    // 3 4 5 [target 4] return the 4
    private int binarySearch(int[] nums, int end, int target) {
        int left = 0;
        int right = end;
        while (left < right) {
            int mid = (left + right) >> 1;
            if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }
        return left;
    }
}
