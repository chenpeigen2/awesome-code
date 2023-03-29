package lee.pkg20221125;

public class Solution {
    //    https://leetcode.cn/problems/expressive-words/description/
    public int expressiveWords(String s, String[] words) {
        int ans = 0;
        for (String word : words) {
            if (expandable(s, word)) ++ans;
        }
        return ans;
    }

    private boolean expandable(String s, String word) {
        int i = 0, j = 0;
        while (i < s.length() && j < word.length()) {
            if (s.charAt(i) != word.charAt(j)) return false;
            char ch = s.charAt(i);

            int cnti = 0;
            while (i < s.length() && s.charAt(i) == ch) {
                i++;
                cnti++;
            }

            int cntj = 0;
            while (j < word.length() && word.charAt(j) == ch) {
                j++;
                cntj++;
            }

            if (cnti < cntj) return false;
            if (cnti != cntj && cnti < 3) return false;
        }

        return i == s.length() && j == word.length();
    }
}
