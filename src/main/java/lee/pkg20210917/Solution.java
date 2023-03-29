package lee.pkg20210917;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Solution {
//    数字 1-9 在每一行只能出现一次。
//    数字 1-9 在每一列只能出现一次。
//    数字 1-9 在每一个以粗实线分隔的 3x3 宫内只能出现一次。（请参考示例图）

    public boolean isValidSudoku(char[][] board) {
        Map<Integer, Set<Integer>> row = new HashMap<>();
        Map<Integer, Set<Integer>> col = new HashMap<>();
        Map<Integer, Set<Integer>> area = new HashMap<>();
        for (int i = 0; i < 9; i++) {
            row.put(i, new HashSet<>());
            col.put(i, new HashSet<>());
            area.put(i, new HashSet<>());

        }

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                char tmp = board[i][j];
                if (tmp == '.') {
                    continue;
                }
                int ind = tmp - '0';
                int areaCount = (i / 3) * 3 + (j / 3);
                if (row.get(i).contains(ind) || col.get(j).contains(ind) || area.get(areaCount).contains(ind)) {
                    return false;
                }
                row.get(i).add(ind);
                col.get(j).add(ind);
                area.get(areaCount).add(ind);
            }
        }
        return true;
    }

    public static void main(String[] args) {
        System.out.println(2 / 3);
    }
}