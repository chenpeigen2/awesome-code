package lee.pkg20221123;

public class Solution {
    //    https://leetcode.cn/problems/maximum-number-of-balls-in-a-box/
    public int countBalls(int l, int r) {
        int ans = 0;
        int[] cnts = new int[50];
        for (int i = l; i <= r; i++) {
            int j = i, cur = 0;
            while (j != 0) {
                cur += j % 10;
                j /= 10;
            }
            if (++cnts[cur] > ans) ans = cnts[cur];
        }
        return ans;
    }
}
