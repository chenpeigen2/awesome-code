package lee.pkg20220919;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Solution {

    //    https://leetcode.cn/problems/sort-array-by-increasing-frequency/
    public int[] frequencySort(int[] nums) {
        Map<Integer, Integer> m = new HashMap<>();
        for (int num : nums) {
            m.put(num, m.getOrDefault(num, 0) + 1);
        }
        return Arrays.stream(nums).boxed().sorted((a, b) -> {
            int ca = m.get(a);
            int cb = m.get(b);
            if (ca == cb) return b - a;
            return ca - cb;
        }).mapToInt(Integer::intValue).toArray();
    }
}
