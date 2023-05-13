package lee.pkg20230513;

import java.util.HashSet;
import java.util.Set;

public class Solution {
    //    https://leetcode.cn/problems/largest-positive-integer-that-exists-with-its-negative/
    public int findMaxK(int[] nums) {
        Set<Integer> s = new HashSet<>();
        int max = -1;
        for (int num : nums) {
            if (s.contains(-num)) max = Math.max(max, Math.abs(num));
            s.add(num);
        }
        return max;
    }
}
