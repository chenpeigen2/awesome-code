package lee.pkg20230321;

public class Solution {
    //    https://leetcode.cn/problems/convert-the-temperature/
    public double[] convertTemperature(double celsius) {
        return new double[]{celsius + 273.15, celsius * 1.80 + 32.00};
    }
}
