package lee.pkg20240330;

import java.util.Arrays;

public class Solution {

    //    https://leetcode.cn/problems/minimum-number-of-coins-to-be-added/description/?envType=daily-question&envId=2024-03-30
    public int minimumAddedCoins(int[] coins, int target) {
        Arrays.sort(coins);
        int ans = 0;
        for (int i = 0, s = 1; s <= target; ) {
            if (i < coins.length && coins[i] <= s) {
                s += coins[i++];
            } else {
                s <<= 1;
                ans++;
            }
        }
        return ans;
    }
}
