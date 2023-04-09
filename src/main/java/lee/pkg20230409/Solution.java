package lee.pkg20230409;

import java.util.HashMap;
import java.util.Map;

public class Solution {
    //    https://leetcode.cn/problems/check-distances-between-same-letters/
    public boolean checkDistances(String s, int[] distance) {
        Map<Integer, Integer> set = new HashMap<>();
        char[] arr = s.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            if (set.containsKey(arr[i] - 'a')) {
                int idx = arr[i] - 'a';
                int dis = i - set.get(arr[i] - 'a') - 1;
                if (dis != distance[idx]) return false;
            }
            set.put(arr[i] - 'a', i);
        }
        return true;
    }
}
