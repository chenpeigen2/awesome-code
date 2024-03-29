package lee.pkg20240328;

import java.util.Arrays;

public class Solution {
    static final int MOD = 1000000007;


//    https://leetcode.cn/problems/first-day-where-you-have-been-in-all-the-rooms/description/?envType=daily-question&envId=2024-03-28

    public int firstDayBeenInAllRooms1(int[] nextVisit) {
        int len = nextVisit.length;
        int[] visit = new int[len];
        int cnt = 0;
        int vis = 0;
        while (cnt < len) {
            if (visit[vis] == 0) {
                cnt++;
            }
            visit[vis]++;

            if (visit[vis] % 2 == 0) {
                vis = (vis + 1) % MOD;
            } else {
                vis = nextVisit[vis];
            }
        }
        return Arrays.stream(visit).sum() - 1;
    }

    public int firstDayBeenInAllRooms(int[] nextVisit) {
        return -1;
    }
}
