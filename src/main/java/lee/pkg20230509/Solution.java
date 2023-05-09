package lee.pkg20230509;

public class Solution {
    private static final char questionMark = '?';

    //    https://leetcode.cn/problems/number-of-valid-clock-times/
    public int countTime(String time) {
        int ans = 1;
        if (time.charAt(0) == questionMark && time.charAt(1) == questionMark) {
            ans *= 24;

        } else if (time.charAt(0) == questionMark) {
            ans = Integer.valueOf(String.valueOf(time.charAt(1))) < 4 ? ans * 3 : ans * 2;
        } else if (time.charAt(1) == questionMark) {
            ans = Integer.valueOf(String.valueOf(time.charAt(0))) < 2 ? ans * 10 : ans * 4;
        }

        if (time.charAt(3) == questionMark) {
            ans *= 6;
        }
        if (time.charAt(4) == questionMark) {
            ans *= 10;
        }

        return ans;
    }
}
