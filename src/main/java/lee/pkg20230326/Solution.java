package lee.pkg20230326;

import java.util.HashSet;
import java.util.Set;

public class Solution {

    //    https://leetcode.cn/problems/find-subarrays-with-equal-sum/
    public boolean findSubarrays(int[] nums) {
        Set<Integer> set = new HashSet<>();
        for (int i = 1; i < nums.length; i++) {
            int sum = nums[i] + nums[i - 1];
            if (set.contains(sum)) return true;
            set.add(sum);
        }
        return false;
    }
}
