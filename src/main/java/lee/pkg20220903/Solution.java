package lee.pkg20220903;

import java.util.Arrays;

public class Solution {
    //    https://leetcode.cn/problems/maximum-length-of-pair-chain/
    public int findLongestChain(int[][] pairs) {
        Arrays.sort(pairs, (a, b) -> a[1] - b[1]);
        int ans = 0;
        int max = Integer.MIN_VALUE;
        for (int[] pair : pairs) {
            if (pair[0] > max) {
                max = pair[1];
                ans++;
            }
        }
        return ans;
    }
}
