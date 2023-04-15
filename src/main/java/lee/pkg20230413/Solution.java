package lee.pkg20230413;

import java.util.HashMap;
import java.util.Map;

public class Solution {
    //    https://leetcode.cn/problems/most-frequent-even-element/
    public int mostFrequentEven(int[] nums) {
        Map<Integer, Integer> m = new HashMap<>();
        for (int num : nums) {
            if (num % 2 == 0) {
                m.put(num, m.getOrDefault(num, 0) + 1);
            }
        }
        int ans = -1;
        int max = 0;
        for (Map.Entry<Integer, Integer> entry : m.entrySet()) {
            if (ans == -1 || entry.getValue() > max || entry.getValue() == max && ans > entry.getKey()) {
                ans = entry.getKey();
                max = entry.getValue();
            }
        }
        return ans;
    }
}
