package lee.pkg20230311;

import java.util.HashMap;
import java.util.Map;

public class Solution {

    // https://leetcode.cn/problems/find-longest-subarray-lcci/solution/python3javacgotypescript-yi-ti-yi-jie-qi-qy7i/
    public String[] findLongestSubarray(String[] array) {
        Map<Integer, Integer> vis = new HashMap<>();
        vis.put(0, -1);
        // mx 最长长度
        // k 为左边界
        int mx = 0, k = 0;

        for (int i = 0, s = 0; i < array.length; i++) {
            s += array[i].charAt(0) >= 'A' ? 1 : -1;
            if (vis.containsKey(s)) {
                // [j+1, i] 为 存在为0的区间
                // 比如说从4到4
                int j = vis.get(s);
                if (mx < i - j) {
                    mx = i - j;
                    k = j + 1;
                }
            } else {
                vis.put(s, i);
            }
        }
        String[] ans = new String[mx];
        System.arraycopy(array, k, ans, 0, mx);
        return ans;

    }
}
