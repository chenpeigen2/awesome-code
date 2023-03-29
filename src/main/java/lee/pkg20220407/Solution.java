package lee.pkg20220407;

//https://leetcode-cn.com/problems/rotate-string/
public class Solution {

    public boolean rotateString(String s, String goal) {
        if (s.length() != goal.length()) return false;
        int len = s.length();
        char start = s.charAt(0);
        int fromIndex = 0;
        while (goal.indexOf(start, fromIndex) != -1) {
            int idx = goal.indexOf(start, fromIndex);
            String key = goal.substring(idx) + goal.substring(0, idx);
            if (s.equals(key)) return true;
            if (idx + 1 < len) fromIndex = idx + 1;
        }
        return false;
    }

    public boolean rotateString1(String s, String goal) {
        if (s.length() != goal.length()) return false;
        int len = s.length();
        for (int i = 1; i <= s.length(); ++i) {
            String str = s.substring(i, s.length()) + s.substring(0, i);
            if (goal.equals(str)) return true;
        }
        return false;
    }

    public boolean rotateString2(String s, String goal) {
        if (s.length() != goal.length()) return false;
        return (s + s).contains(goal);
    }

    public static void main(String[] args) {
        var app = new Solution();
        var ans = app.rotateString("abcde", "abced");
        System.out.println(ans);
    }
}
