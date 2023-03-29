package lee.pkg20220419;

public class Solution {
    //    https://leetcode-cn.com/problems/shortest-distance-to-a-character/
    public int[] shortestToChar(String s, char c) {
        char[] ch = s.toCharArray();
        int[] ans = new int[ch.length];
        for (int i = 0, k = -1; i < ch.length; ++i) {
            if (ch[i] == c) k = i;
            ans[i] = k;
        }
        for (int i = ch.length - 1, j = -1; i >= 0; i--) {
            if (ch[i] == c) j = i;
            if (j == -1) {
                ans[i] = Math.abs(ans[i] - i);
            } else {
                if (ans[i] == -1) {
                    ans[i] = Math.abs(i - j);
                } else {
                    ans[i] = Math.min(Math.abs(i - ans[i]), Math.abs(i - j));
                }
            }

        }
        return ans;
    }


}
