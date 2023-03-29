package lee.pkg20220811;

public class Solution {
    //    https://leetcode.cn/problems/reformat-the-string/
    public String reformat(String s) {
        StringBuilder a = new StringBuilder(), b = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (c >= 'a') a.append(c);
            else b.append(c);
        }
        int n = a.length(), m = b.length(), tot = n + m;
        if (Math.abs(n - m) > 1) return "";
        StringBuilder sb = new StringBuilder();
        while (sb.length() != tot) {
            if (n > m) sb.append(a.charAt(--n));
            else if (n < m) sb.append(b.charAt(--m));
            else {
                if (!sb.isEmpty() && sb.charAt(sb.length() - 1) >= 'a') sb.append(b.charAt(--m));
                else sb.append(a.charAt(--n));
            }
        }
        return sb.toString();
    }
}