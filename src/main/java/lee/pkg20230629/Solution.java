package lee.pkg20230629;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Solution {
    // 1 1 1 0 0 0
    // 1 1 1 1 1 1
    public List<List<Integer>> reconstructMatrix(int upper, int lower, int[] colsum) {
        int[][] ret = new int[2][colsum.length];
        for (int i = 0; i < colsum.length; i++) {
            ret[0][i] = colsum[i] == 2 || (colsum[i] == 1 && upper > lower) ? 1 : 0;
            ret[1][i] = colsum[i] == 2 || (colsum[i] == 1 && ret[0][i] == 0) ? 1 : 0;
            upper -= ret[0][i];
            lower -= ret[1][i];
        }
        return lower == 0 && upper == 0 ? new ArrayList(Arrays.asList(ret[0], ret[1])) : new ArrayList<>();
    }

    public static void main(String[] args) {
        var app = new Solution();
        var ans = app.reconstructMatrix(3, 6, new int[]{2, 2, 2, 1, 1, 1});
        System.out.println();
    }
}
