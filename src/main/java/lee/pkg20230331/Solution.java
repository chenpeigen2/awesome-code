package lee.pkg20230331;

import java.util.HashSet;
import java.util.Set;

public class Solution {
    //    https://leetcode.cn/problems/number-of-arithmetic-triplets/
    public int arithmeticTriplets(int[] nums, int diff) {
        int ans = 0;
        Set<Integer> set = new HashSet<>();
        for (int num : nums) set.add(num);
        for (int num : nums) {
            if (set.contains(num + diff) && set.contains(num + 2 * diff)) ans++;
        }
        return ans;
    }
}
