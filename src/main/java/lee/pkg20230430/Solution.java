package lee.pkg20230430;

public class Solution {
    //    https://leetcode.cn/problems/moving-stones-until-consecutive/
    public int[] numMovesStones(int a, int b, int c) {
        int x = Math.min(a, Math.min(b, c));
        int z = Math.max(a, Math.max(b, c));
        int y = a + b + c - x - z;

        int[] res = new int[2];
        res[0] = 2;
        if (y - x == 1 && z - y == 1) res[0] = 0;
        else if (y - x <= 2 || z - y <= 2) res[0] = 1;  // 2 4 6 => 4 5 6
        res[1] = z - x - 1 - 1;
        return res;
    }

    public static void main(String[] args) {
        var app = new Solution();
        var ans = app.numMovesStones(1, 5, 8);
        for (int i : ans) {
            System.out.println(i);
        }
    }
}
