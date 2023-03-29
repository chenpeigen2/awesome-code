package lee.pkg20230214;

import java.util.HashMap;
import java.util.Map;

public class Solution {
    //    https://leetcode.cn/problems/longest-well-performing-interval/
    public int longestWPI(int[] hours) {
        int n = hours.length;
        Map<Integer, Integer> map = new HashMap<>();
        int s = 0, res = 0;

        for (int i = 0; i < n; i++) {
            s += hours[i] > 8 ? 1 : -1;
            if (s > 0) {
                res = Math.max(res, i + 1);
            } else {
              //
                // -1 -2 -3 -4 -5 -4 -3 -2 -1
                //     j                    i

                // [j+1, i]

                // 如果是负数的话找到 -2 ---> -1 (sum)
                if (map.containsKey(s - 1)) {
                    res = Math.max(res, i - map.get(s - 1));
                }
            }
            if (!map.containsKey(s)) {
                map.put(s, i);
            }
        }

        return res;
    }
}
