package lee.pkg20220520;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Solution {
    //    https://leetcode.cn/problems/find-right-interval/
    public int[] findRightInterval(int[][] intervals) {
        int len = intervals.length;

        Map<int[], Integer> m = new HashMap<>();

        for (int i = 0; i < len; i++) {
            m.put(intervals[i], i);
        }

        Arrays.sort(intervals, new Comparator<int[]>() {
            @Override
            public int compare(int[] i1, int[] i2) {
                if (i1[0] != i2[0]) return i1[0] - i2[0];
                return i1[1] - i2[1];
            }
        });

        int[] ans = new int[len];
        Arrays.fill(ans, -1);

        for (int i = 0; i < len - 1; i++) {
            int end = intervals[i][1];

            if (end == intervals[i][0]) {
                ans[m.get(intervals[i])] = m.get(intervals[i]);
                continue;
            }

            for (int j = i + 1; j < len; j++) {
                int nextStart = intervals[j][0];
                if (end <= nextStart) {
                    ans[m.get(intervals[i])] = m.get(intervals[j]);
                    break;
                }
            }
        }

        return ans;
    }

    public int[] findRightInterval1(int[][] intervals) {
        int n = intervals.length;
        int[][] startIntervals = new int[n][2];
        int[][] endIntervals = new int[n][2];
        for (int i = 0; i < n; i++) {
            startIntervals[i][0] = intervals[i][0];
            startIntervals[i][1] = i;
            endIntervals[i][0] = intervals[i][1];
            endIntervals[i][1] = i;
        }
        Arrays.sort(startIntervals, (o1, o2) -> o1[0] - o2[0]);
        Arrays.sort(endIntervals, (o1, o2) -> o1[0] - o2[0]);

        int[] ans = new int[n];
        for (int i = 0, j = 0; i < n; i++) {
            while (j < n && endIntervals[i][0] > startIntervals[j][0]) {
                j++;
            }
            if (j < n) {
                ans[endIntervals[i][1]] = startIntervals[j][1];
            } else {
                ans[endIntervals[i][1]] = -1;
            }
        }
        return ans;
    }
}
