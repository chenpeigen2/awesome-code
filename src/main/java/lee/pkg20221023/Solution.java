package lee.pkg20221023;

public class Solution {
    //    https://leetcode.cn/problems/merge-strings-alternately/
    public String mergeAlternately(String word1, String word2) {
        int len1 = word1.length();
        int len2 = word2.length();

        int len = Math.min(len1, len2);

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < len; i++) {
            sb.append(word1.charAt(i));
            sb.append(word2.charAt(i));
        }

        sb.append(word1, len, len1);
        sb.append(word2, len, len2);

        return sb.toString();
    }
}
