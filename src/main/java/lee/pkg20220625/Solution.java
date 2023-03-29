package lee.pkg20220625;

public class Solution {
//    https://leetcode.cn/problems/JEj789/
    public int minCost(int[][] cs) {
        int n = cs.length;
        int a = cs[0][0], b = cs[0][1], c = cs[0][2];
        for (int i = 1; i < n; i++) {
            int d = Math.min(b, c) + cs[i][0];
            int e = Math.min(a, c) + cs[i][1];
            int f = Math.min(a, b) + cs[i][2];
            a = d;
            b = e;
            c = f;
        }
        return Math.min(a, Math.min(b, c));
    }
}
