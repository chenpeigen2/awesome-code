package lee.pkg20220720;

import java.util.ArrayList;
import java.util.List;

public class Solution {

    //    https://leetcode.cn/problems/shift-2d-grid/
    public List<List<Integer>> shiftGrid(int[][] grid, int k) {
        int m = grid.length, n = grid[0].length;
        List<List<Integer>> ret = new ArrayList<>();

        for (int i = 0; i < m; i++) {
            List<Integer> row = new ArrayList<>();
            for (int j = 0; j < n; j++) {
                row.add(0);
            }
            ret.add(row);
        }

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                int nIdx = (i * n + j + k) % (m * n);
                ret.get(nIdx / n).set(nIdx % n, grid[i][j]);
            }
        }

        return ret;
    }
}
