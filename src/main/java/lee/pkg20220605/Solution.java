package lee.pkg20220605;

import java.util.Random;

public class Solution {

    //    https://leetcode.cn/problems/generate-random-point-in-a-circle/
    double r, x, y;
    Random random = new Random();

    public Solution(double radius, double x_center, double y_center) {
        r = radius;
        x = x_center;
        y = y_center;
    }

    public double[] randPoint() {
        double len = Math.sqrt(random.nextDouble(r * r)), ang = random.nextDouble(2 * Math.PI);
        double nx = x + len * Math.cos(ang), ny = y + len * Math.sin(ang);
        return new double[]{nx, ny};
    }
}
