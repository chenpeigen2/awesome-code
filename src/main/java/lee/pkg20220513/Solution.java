package lee.pkg20220513;

public class Solution {


    //    https://leetcode.cn/problems/one-away-lcci/
    public boolean oneEditAway(String first, String second) {
        if (Math.abs(first.length() - second.length()) >= 2) return false;
        if (first.length() == second.length()) {
            for (int i = 0, k = -1; i < first.length(); i++) {
                if (first.charAt(i) != second.charAt(i)) {
                    if (k == 1) {
                        return false;
                    }
                    k = 1;
                }
            }
        } else {
            int cnt = 0;
            if (first.length() > second.length()) {
                for (int i = 0, k = -1; i < second.length(); i++) {
                    if (first.charAt(cnt) != second.charAt(i)) {
                        if (k == 1) return false;
                        i--;
                        k = 1;
                    }
                    cnt++;
                }
            } else {
                for (int i = 0, k = -1; i < first.length(); i++) {
                    if (second.charAt(cnt) != first.charAt(i)) {
                        if (k == 1) return false;
                        i--;
                        k = 1;
                    }
                    cnt++;
                }
            }
        }

        return true;
    }

    public boolean oneEditAway1(String first, String second) {
        int n = first.length(), m = second.length();
        if (Math.abs(n - m) > 1) return false;
        if (n > m) return oneEditAway1(second, first);
        int i = 0, j = 0, cnt = 0;
        while (i < n && j < m && cnt <= 1) {
            char c1 = first.charAt(i), c2 = second.charAt(j);
            if (c1 == c2) {
                i++;
                j++;
            } else {
                cnt++;
                if (n == m) {
                    i++;
                }
                j++;
            }
        }
        return cnt <= 1;
    }

    public static void main(String[] args) {
        var app = new Solution();
        var ans = app.oneEditAway("islander", "slander");
        System.out.println(ans);
    }
}
