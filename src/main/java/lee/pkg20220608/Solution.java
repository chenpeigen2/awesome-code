package lee.pkg20220608;

public class Solution {
    //    https://leetcode.cn/problems/valid-boomerang/
    public boolean isBoomerang(int[][] points) {
        int k1 = (points[1][1] - points[0][1]) * (points[2][0] - points[0][0]);
        int k2 = (points[2][1] - points[0][1]) * (points[1][0] - points[0][0]);
        return k1 != k2;
    }
}
