package lee.pkg20220702;

import java.util.PriorityQueue;

public class Solution {
    //    https://leetcode.cn/problems/minimum-number-of-refueling-stops/
    public int minRefuelStops(int target, int startFuel, int[][] stations) {
        PriorityQueue<Integer> q = new PriorityQueue<>((a, b) -> b - a);
        int n = stations.length, idx = 0;
        int remain = startFuel, loc = 0, ans = 0;
        while (loc < target) {
            if (remain == 0) {
                if (!q.isEmpty() && ++ans >= 0) {
                    remain = q.poll();
                } else {
                    return -1;
                }
            }
            loc += remain;
            remain = 0;
            while (idx < n && stations[idx][0] <= loc) q.add(stations[idx++][1]);
        }
        return ans;
    }
}
