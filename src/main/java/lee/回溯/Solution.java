package lee.回溯;

import java.util.Arrays;

public class Solution {
    //    https://leetcode.cn/problems/matchsticks-to-square/submissions/539605463/
    public boolean makesquare(int[] matchsticks) {
        int sumLen = Arrays.stream(matchsticks).sum();
        if (sumLen % 4 != 0) {
            return false;
        }
        Arrays.sort(matchsticks);
        for (int i = 0, j = matchsticks.length - 1; i < j; i++, j--) {
            int temp = matchsticks[i];
            matchsticks[i] = matchsticks[j];
            matchsticks[j] = temp;
        }
        int[] arr = new int[4];
        return dfs(0, matchsticks, arr, sumLen / 4);
    }

    private boolean dfs(int idx, int[] matchsticks, int[] edges, int target) {
        if (idx == matchsticks.length) return true;
        for (int i = 0; i < edges.length; i++) {
            edges[i] += matchsticks[idx];
            if (edges[i] <= target && dfs(idx + 1, matchsticks, edges, target)) {
                return true;
            }
            edges[i] -= matchsticks[idx];
        }
        return false;
    }
}
