package lee.pkg20221119;

public class Solution {
    //    https://leetcode.cn/problems/find-the-highest-altitude/description/
    public int largestAltitude(int[] g) {
        int cur = 0, ans = 0;
        for (int x : g) {
            cur += x;
            ans = Math.max(ans, cur);
        }
        return ans;
    }
}
