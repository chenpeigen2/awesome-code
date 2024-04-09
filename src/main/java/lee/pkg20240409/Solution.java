package lee.pkg20240409;

public class Solution {
    //    https://leetcode.cn/problems/maximum-count-of-positive-integer-and-negative-integer/description/?envType=daily-question&envId=2024-04-09
    public int maximumCount(int[] nums) {
        int i = 0;
        int j = nums.length - 1;
        int a = 0;
        int b = 0;
        while (i <= j && (nums[i] < 0 || nums[j] > 0)) {
            if (nums[i] < 0) {
                i++;
                a++;
            }
            if (nums[j] > 0) {
                j--;
                b++;
            }
        }
        return Math.max(a, b);
    }
}
