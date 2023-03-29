package lee.pkg20221024;

public class Solution {
    //    https://leetcode.cn/problems/partition-array-into-disjoint-intervals/
    public int partitionDisjoint(int[] nums) {
        int n = nums.length;
        int[] min = new int[n];
        min[n - 1] = nums[n - 1];
        for (int i = n - 2; i >= 0; i--) {
            min[i] = Math.min(min[i + 1], nums[i]);
        }
        for (int i = 0, max = 0; i < n - 1; i++) {
            max = Math.max(max, nums[i]);
            if (max <= min[i + 1]) return i + 1;
        }
        return -1;
    }
}
