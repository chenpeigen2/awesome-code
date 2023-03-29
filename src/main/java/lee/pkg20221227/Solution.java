package lee.pkg20221227;

public class Solution {

    //    https://leetcode.cn/problems/minimum-moves-to-convert-string/
    public int minimumMoves(String s) {
        int len = s.length();
        int ans = 0;
        for (int i = 0; i < len; i++) {
            if (s.charAt(i) == 'X') {
                ans++;
                i += 2;
            }
        }
        return ans;
    }
}
