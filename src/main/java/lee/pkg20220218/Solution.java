package lee.pkg20220218;

public class Solution {
    public int findCenter(int[][] edges) {
//        https://leetcode-cn.com/problems/find-center-of-star-graph/
        int a = edges[0][0], b = edges[0][1];
        if (a == edges[1][0] || a == edges[1][1]) return a;
        return b;
    }
}
