package lee.pkg20220728;

import java.util.*;

public class Solution {
    //    https://leetcode.cn/problems/rank-transform-of-an-array/
    public int[] arrayRankTransform(int[] arr) {
        int n = arr.length;
        int[] clone = arr.clone();
        Arrays.sort(clone);
        Map<Integer, Integer> m = new HashMap<>();
        int cnt = 1;
        for (int i : clone) {
            if (!m.containsKey(i)) {
                m.put(i, cnt++);
            }
        }

        int[] ans = new int[n];
        for (int i = 0; i < n; i++) {
            ans[i] = m.get(arr[i]);
        }

        return ans;
    }
}
