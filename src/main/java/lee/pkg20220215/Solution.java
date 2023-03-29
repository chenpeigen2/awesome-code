package lee.pkg20220215;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Solution {
    //    https://leetcode-cn.com/problems/lucky-numbers-in-a-matrix/
    public List<Integer> luckyNumbers(int[][] matrix) {
        int m = matrix.length, n = matrix[0].length;
        int[] minR = new int[m], maxC = new int[n];
        Arrays.fill(minR, Integer.MAX_VALUE);
        Arrays.fill(maxC, Integer.MIN_VALUE);
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                minR[i] = Math.min(minR[i], matrix[i][j]);
                maxC[j] = Math.max(maxC[j], matrix[i][j]);
            }
        }

        List<Integer> l = new ArrayList<>();
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (matrix[i][j] == minR[i] && matrix[i][j] == maxC[j]) {
                    l.add(matrix[i][j]);
                }
            }
        }

        return l;
    }
}
