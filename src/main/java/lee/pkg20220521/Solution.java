package lee.pkg20220521;

import java.util.Arrays;
import java.util.Random;

public class Solution {
    //    https://leetcode.cn/problems/n-repeated-element-in-size-2n-array/
    public int repeatedNTimes(int[] nums) {
        Arrays.sort(nums);
        int mid = nums.length / 2;
        if (nums[mid] == nums[mid + 1]) return nums[mid];
        return nums[mid - 1];
    }

    public int repeatedNTimes1(int[] nums) {
        int len = nums.length;
        Random r = new Random();
        while (true) {
            int i = r.nextInt(len), j = r.nextInt(len);
            if (i != j && nums[i] == nums[j]) {
                return nums[i];
            }
        }
    }
}
