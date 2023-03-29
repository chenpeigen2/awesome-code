package lee.pkg20230111;

import java.util.HashMap;
import java.util.Map;

public class Solution {

    //    https://leetcode.cn/problems/check-if-number-has-equal-digit-count-and-digit-value/
    public boolean digitCount(String num) {
        Map<Integer, Integer> h = new HashMap<>();
        int n = num.length();
        for (int i = 0; i < n; i++) {
            int digit = num.charAt(i) - '0';
            h.put(digit, h.getOrDefault(digit, 0) + 1);
        }
        for (int i = 0; i < n; i++) {
            int v = num.charAt(i) - '0';
            if (h.getOrDefault(i, 0) != v) {
                return false;
            }
        }
        return true;
    }
}
