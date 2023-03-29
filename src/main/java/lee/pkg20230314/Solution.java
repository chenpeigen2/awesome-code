package lee.pkg20230314;

public class Solution {

    //    https://leetcode.cn/problems/find-valid-matrix-given-row-and-column-sums/
    public int[][] restoreMatrix(int[] rowSum, int[] colSum) {
        int m = rowSum.length;
        int n = colSum.length;
        var mat = new int[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                mat[i][j] = Math.min(rowSum[i], colSum[j]);
                rowSum[i] -= mat[i][j];
                colSum[j] -= mat[i][j];
            }
        }
        return mat;
    }

    public int[][] restoreMatrix1(int[] rowSum, int[] colSum) {
        int m = rowSum.length, n = colSum.length;
        var mat = new int[m][n];
        for (int i = 0, j = 0; i < m && j < n; ) {
            int rs = rowSum[i], cs = colSum[j];
            if (rs < cs) {
                colSum[j] -= rs;
                mat[i++][j] = rs;
            } else {
                rowSum[i] -= cs;
                mat[i][j++] = cs;
            }
        }
        return mat;
    }
}
