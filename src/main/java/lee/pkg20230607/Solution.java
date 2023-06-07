package lee.pkg20230607;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Solution {

    //    https://leetcode.cn/problems/mice-and-cheese/
    public int miceAndCheese(int[] reward1, int[] reward2, int k) {
        int ans = 0;
        int n = reward1.length;

        int[] diff = new int[n];

        for (int i = 0; i < n; i++) {
            diff[i] = reward2[i] - reward1[i];
            ans += reward2[i];
        }

        Arrays.sort(diff);
        for (int i = 0; i < k; i++) {
            ans -= diff[i];
        }


        return ans;
    }
}
