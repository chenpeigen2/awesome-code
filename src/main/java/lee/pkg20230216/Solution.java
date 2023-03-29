package lee.pkg20230216;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class Solution {

    //    https://leetcode.cn/problems/maximum-number-of-pairs-in-array/
    public int[] numberOfPairs(int[] nums) {
        Map<Integer, Boolean> cnt = new HashMap<>();
        int res = 0;
        for (int num : nums) {
//            cnt.put(num, !cnt.getOrDefault(num, false));
            cnt.compute(num, (k, v) -> {
                if (v == null) {
                    return true;
                }
                return !v;
            });
            if (!cnt.get(num)) res++;
        }
        return new int[]{res, nums.length - 2 * res};
    }
}
