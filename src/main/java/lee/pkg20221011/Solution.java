package lee.pkg20221011;

public class Solution {
    //    https://leetcode.cn/problems/check-if-one-string-swap-can-make-strings-equal/
    public boolean areAlmostEqual(String s1, String s2) {
        int n = s1.length(), a = -1, b = -1;
        for (int i = 0; i < n; i++) {
            if (s1.charAt(i) == s2.charAt(i)) continue;
            if (a == -1) a = i;
            else if (b == -1) b = i;
            else return false;
        }
        if (a == -1) return true;
        if (b == -1) return false;
        return s1.charAt(a) == s2.charAt(b) && s1.charAt(b) == s2.charAt(a);
    }

}
