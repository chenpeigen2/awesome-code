package lee.pkg20221201;

public class Solution {

    //    https://leetcode.cn/problems/find-nearest-point-that-has-the-same-x-or-y-coordinate/
    public int nearestValidPoint(int x, int y, int[][] points) {
        int minIdx = -1, minDistance = Integer.MAX_VALUE;
        for (int i = 0; i < points.length; i++) {
            if (x == points[i][0] || y == points[i][1]) {
                int distance = Math.abs(points[i][0] - x) + Math.abs(points[i][1] - y);
                if (minDistance > distance) {
                    minDistance = distance;
                    minIdx = i;
                }
            }
        }
        return minIdx;
    }

    public static void main(String[] args) {
        int[][] arr = new int[][]{{1, 2}, {3, 1}, {2, 4}, {2, 3}, {4, 4}};
        var app = new Solution();
        var ans = app.nearestValidPoint(3, 4, arr);
        System.out.println(ans);
    }
}
