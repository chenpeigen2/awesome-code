package lee.pkg20230515;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Solution {
    //    https://leetcode.cn/problems/flip-columns-for-maximum-number-of-equal-rows/solution/javapython3xun-zhao-xiang-tong-huo-zhe-h-1ivc/
//    我们首先来明确一点，如果两个不同的行可以通过翻转相同列使得这两个行分别实现行内元素相同，那么这两个行的元素要么完全相同，要么完全互补。
    public int maxEqualRowsAfterFlips(int[][] matrix) {
        int m = matrix.length, n = matrix[0].length;
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < m; i++) {
            char[] tmp = new char[n];
            Arrays.fill(tmp, '0');
            for (int j = 0; j < n; j++) {
                // 如果 matrix[i][0] 为 1，则对该行元素进行翻转
                tmp[j] = (char) ('0' + matrix[i][0] ^ matrix[i][j]);
            }
            String vv = new String(tmp);
            map.put(vv, map.getOrDefault(vv, 0) + 1);
        }

        int res = 0;
        for (var entry : map.entrySet()) {
            res = Math.max(res, entry.getValue());
        }
        return res;

    }
}
