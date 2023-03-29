package lee.pkg20220119;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Solution {
    //    https://leetcode-cn.com/problems/contains-duplicate-ii/
    public boolean containsNearbyDuplicate(int[] nums, int k) {
        Set<Integer> s = new HashSet<>();
        int len = nums.length;
//        0 1 2 3 4
        // idx4 and will remove idx0 k = 3
        for (int i = 0; i < len; i++) {
            if (i > k) {
                s.remove(nums[i - k - 1]);
            }
            if (!s.add(nums[i])) {
                return true;
            }
        }
        return false;
    }

    public boolean containsNearbyDuplicate1(int[] nums, int k) {
        int n = nums.length;
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < n; ++i) {
            int j = map.getOrDefault(nums[i], -1); //取到最近的下标
            if (j != -1 && i - j <= k) return true;
            map.put(nums[i], i);
        }
        return false;
    }
}
