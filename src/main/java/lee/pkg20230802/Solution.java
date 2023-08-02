package lee.pkg20230802;

import java.util.HashSet;
import java.util.Set;

public class Solution {
    //    https://leetcode.cn/problems/card-flipping-game/
    public int flipgame(int[] fronts, int[] backs) {

        Set<Integer> set = new HashSet<>();
        for (int i = 0; i < fronts.length; i++) {
            if (fronts[i] == backs[i]) {
                set.add(fronts[i]);
            }
        }

        int ans = 3000;

        for (int x : fronts) {
            if (x < ans && !set.contains(x)) {
                ans = x;
            }
        }
        for (int x : backs) {
            if (x < ans && !set.contains(x)) {
                ans = x;
            }
        }

        return ans % 3000;
    }
}
