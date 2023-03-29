package lee.pkg20220414;

import java.util.Arrays;

public class Solution {
    //    https://leetcode-cn.com/problems/richest-customer-wealth/
    public int maximumWealth(int[][] accounts) {
        int ans = 0;
        for (int[] account : accounts) {
            ans = Math.max(Arrays.stream(account).sum(), ans);
        }

        return ans;
    }
}
