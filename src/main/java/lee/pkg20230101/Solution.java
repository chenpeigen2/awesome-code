package lee.pkg20230101;

public class Solution {

    //    https://leetcode.cn/problems/first-letter-to-appear-twice/
    public char repeatedCharacter(String s) {
        int[] ch = new int[26];
        int len = s.length();
        for (int i = 0; i < len; i++) {
            if (++ch[s.charAt(i) - 'a'] == 2) return s.charAt(i);
        }
        return '0';
    }

    public char repeatedCharacter1(String s) {
        int seen = 0;
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            int x = ch - 'a';
            if ((seen & (1 << x)) != 0) {
                return ch;
            }
            seen |= (1 << x);
        }
        // impossible
        return ' ';
    }
}
