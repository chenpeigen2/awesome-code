package lee.pkg20230322;

import java.util.*;

public class Solution {

    //    https://leetcode.cn/problems/best-team-with-no-conflicts/solution/wu-mao-dun-de-zui-jia-qiu-dui-by-leetcod-2lxf/
    public int bestTeamScore(int[] scores, int[] ages) {
        int n = scores.length;
        int[][] people = new int[n][2];
        for (int i = 0; i < n; ++i) {
            people[i] = new int[]{scores[i], ages[i]};
        }
        Arrays.sort(people, (a, b) -> a[0] != b[0] ? a[0] - b[0] : a[1] - b[1]);

        int[] dp = new int[n];
        int res = 0;
        for (int i = 0; i < n; ++i) {
            for (int j = i - 1; j >= 0; j--) {
                // 所以为了避免矛盾的产生我们只需要让最后组建球队的最后一名球员的年龄不小于该球员前面一名球员的年龄即可
                if (people[j][1] <= people[i][1]) {
                    dp[i] = Math.max(dp[i], dp[j]);
                }
            }
            // dp[i]代表包含这个的i的idx所在的数组目前能做到的最长的长度
            dp[i] += people[i][0];
            res = Math.max(res, dp[i]);
        }
        return res;
    }
}
