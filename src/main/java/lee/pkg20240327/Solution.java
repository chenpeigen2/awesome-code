package lee.pkg20240327;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;

public class Solution {

    static final int MOD = 1000000007;


//    https://leetcode.cn/problems/count-ways-to-group-overlapping-ranges/description/?envType=daily-question&envId=2024-03-27

    public int countWays(int[][] ranges) {
        Arrays.sort(ranges, (a, b) -> a[0] - b[0]);

        int n = ranges.length;
        int res = 1;

        for (int i = 0; i < n; ) {
            int r = ranges[i][1];
            int j = i + 1;
            while (j < n && ranges[j][0] <= r) {
                r = Math.max(r, ranges[j][1]);
                j++;
            }
            res = res * 2 % MOD;
            i = j;
        }


        return res;
    }
}


class Solution1 {
    static final int MOD = 1000000007;

    public int countWays(int[][] ranges) {
        int n = ranges.length;
        Arrays.sort(ranges, new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                return o1[0] - o2[0];
            }
        });

        int cnt = 1;

        for (int i = 0; i < n; ) {
            int r = ranges[i][1];
            int j = i + 1;
            while (j < n && ranges[j][0] <= r) {
                r = Math.max(r, ranges[j][1]);
                j++;
            }

            cnt = cnt * 2 % MOD;
            i = j;
        }

        return cnt;
    }
}
