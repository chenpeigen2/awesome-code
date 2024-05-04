package lee.pkg20240503;

import java.util.Arrays;

public class Solution {
//    https://leetcode.cn/problems/average-salary-excluding-the-minimum-and-maximum-salary/?envType=daily-question&envId=2024-05-03
    public double average(int[] salary) {
        Arrays.sort(salary);
        double avg =0;
        int sz = 0;
        for (int i = 1; i <= salary.length-2;i++,sz++) {
            avg += salary[i];
        }
        avg /= sz;
        return avg;
    }
}
