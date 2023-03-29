package lee.pkg20220804;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Solution {
    //    https://leetcode.cn/problems/minimum-subsequence-in-non-increasing-order/
    public List<Integer> minSubsequence(int[] nums) {
        Arrays.sort(nums);
        int sum = 0, cur = 0, idx = nums.length - 1;
        for (int i : nums) sum += i; // for high speed
        List<Integer> ans = new ArrayList<>();
        while (cur <= sum) {
            sum -= nums[idx];
            cur += nums[idx];
            ans.add(nums[idx--]);
        }
        return ans;
    }
}
