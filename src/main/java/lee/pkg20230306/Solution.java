package lee.pkg20230306;

public class Solution {

    //    https://leetcode.cn/problems/minimum-deletions-to-make-string-balanced/
    public int minimumDeletions(String s) {
        int leftb = 0, righta = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == 'a') righta++;
        }
        int res = righta;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == 'a') righta--;
            else leftb++;
            res = Math.min(res, leftb + righta);
        }
        return res;
    }

    public int minimumDeletions1(String s) {
        int f = 0, cntB = 0;
        for (char c : s.toCharArray()) {
            if (c == 'b') ++cntB;
            else f = Math.min(f + 1, cntB);
        }
        return f;
    }
}
