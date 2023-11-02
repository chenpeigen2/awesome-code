package lee.pkg20231102;

import java.util.Arrays;

public class Solution {
    //    https://leetcode.cn/problems/rings-and-rods/?envType=daily-question&envId=2023-11-02
    public int countPoints(String rings) {
        int[] circle = new int[10];
        int length = rings.length();
        for (int i = 0; i < length; i += 2) {
            char c = rings.charAt(i);
            int posi = rings.charAt(i + 1) - '0';
            if (c == 'R') {
                circle[posi] = circle[posi] | 1;
            } else if (c == 'G') {
                circle[posi] = circle[posi] | 2;
            } else {
                circle[posi] = circle[posi] | 4;
            }
        }

        int ret = 0;

        for (int cc : circle) {
            if ((cc ^ 7) == 0) ret++;
        }

        return ret;
    }

    public static void main(String[] args) {
        var app = new Solution();
        app.countPoints("B0B6G0R6R0R6G9");

    }
}
