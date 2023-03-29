package lee.pkg20220613;

import java.util.Arrays;

public class Solution {
    //    https://leetcode.cn/problems/height-checker/
    public int heightChecker(int[] heights) {
        int[] t = heights.clone();
        Arrays.sort(t);
        int n = heights.length, ans = 0;
        for (int i = 0; i < n; i++) {
            if (t[i] != heights[i]) ans++;
        }
        return ans;
    }
}
