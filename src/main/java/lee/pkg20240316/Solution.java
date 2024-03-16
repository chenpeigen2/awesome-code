package lee.pkg20240316;

import java.util.HashSet;
import java.util.Set;

public class Solution {
    //    https://leetcode.cn/problems/maximum-number-of-moves-in-a-grid/?envType=daily-question&envId=2024-03-16
    public int maxMoves(int[][] grid) {
        int m = grid.length, n = grid[0].length;
        Set<Integer> q = new HashSet<>();
        // 所有行坐标加入到集合中，作为出发点。
        for (int i = 0; i < m; i++) {
            q.add(i);
        }
        // 然后对其依次遍历，对每一个单元格，找到下一个列的相邻单元格
        // 遍历列
        for (int j = 1; j < n; j++) {
            Set<Integer> q2 = new HashSet<>();

            for (int i : q) {
                for (int i2 = i - 1; i2 <= i + 1; i2++) {
                    if (0 <= i2 && i2 < m && grid[i][j - 1] < grid[i2][j]) {
                        q2.add(i2); // 把所有可到达的单元格行坐标加到集合中
                    }
                }
            }
            q = q2;
//            当到达最后一列或者集合为空
            if (q.isEmpty()) {
                return j - 1;
            }
        }
        return n - 1;
    }
}
