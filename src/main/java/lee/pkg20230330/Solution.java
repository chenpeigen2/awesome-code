package lee.pkg20230330;

import java.util.Arrays;
import java.util.Comparator;

public class Solution {

    //    https://leetcode.cn/problems/widest-vertical-area-between-two-points-containing-no-points/
    public int maxWidthOfVerticalArea(int[][] points) {
        Arrays.sort(points, new Comparator<int[]>() {
            @Override
            public int compare(int[] a, int[] b) {
                return a[0] - b[0];
            }
        });
        int mx = 0;
        for (int i = 1; i < points.length; i++) {
            mx = Math.max(mx, points[i][0] - points[i - 1][0]);
        }
        return mx;
    }
}
