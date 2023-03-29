package lee.pkg20220914;

import java.util.Arrays;

public class Solution {
    //    https://leetcode.cn/problems/mean-of-array-after-removing-some-elements/
    public double trimMean(int[] arr) {
        Arrays.sort(arr);
        int n = arr.length;
        int sum = 0;
        for (int i = n / 20; i < n - n / 20; i++) {
            sum += arr[i];
        }
        return sum * 1.0 / (n * 0.9);
    }
}
