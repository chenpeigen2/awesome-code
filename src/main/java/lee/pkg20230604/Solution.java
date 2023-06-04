package lee.pkg20230604;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Solution {
    //    https://leetcode.cn/problems/number-of-distinct-averages/
    public int distinctAverages(int[] nums) {
        Arrays.sort(nums);
        int l = 0;
        int r = nums.length - 1;
        Set<Float> set = new HashSet<>();
        while (l < r) {
            float mid = ((float) nums[l] + nums[r]) / 2;
            set.add(mid);
            l++;
            r--;
        }
        return set.size();
    }

    public int distinctAverages1(int[] nums) {
        Arrays.sort(nums);
        int l = 0;
        int r = nums.length - 1;
        Set<Integer> set = new HashSet<>();
        while (l < r) {
            set.add(nums[l] + nums[r]);
            l++;
            r--;
        }
        return set.size();
    }
}
