package lee.pkg20221231;

import java.util.Arrays;

public class Solution {
    //    https://leetcode.cn/problems/minimum-number-of-moves-to-seat-everyone/
    public int minMovesToSeat(int[] seats, int[] students) {
        Arrays.sort(seats);
        Arrays.sort(students);
        int res = 0;
        for (int i = 0; i < seats.length; i++) {
            res += Math.abs(seats[i] - students[i]);
        }
        return res;
    }
}
