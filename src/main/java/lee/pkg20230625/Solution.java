package lee.pkg20230625;

public class Solution {

    //    https://leetcode.cn/problems/circle-and-rectangle-overlapping/
    public boolean checkOverlap(int radius, int xCenter, int yCenter, int x1, int y1, int x2, int y2) {
        int a = f(x1, x2, xCenter);
        int b = f(y1, y2, yCenter);
        return a * a + b * b <= radius * radius;
    }

    private int f(int x1, int x2, int xCenter) {
        if (x1 <= xCenter && xCenter <= x2) {
            return 0;
        }
        return xCenter < x1 ? x1 - xCenter : xCenter - x2;
    }
}
