package lee.pkg20221101;

public class Solution {
    //    https://leetcode.cn/problems/check-if-two-string-arrays-are-equivalent/
    public boolean arrayStringsAreEqual(String[] word1, String[] word2) {
        int len1 = word1.length;
        int len2 = word2.length;
        int idx1 = 0;
        int idx2 = 0;

        int p = 0, q = 0;

        while (idx1 < len1 && idx2 < len2) {
            if (word1[idx1].charAt(p) != word2[idx2].charAt(q)) return false;
            p++;
            q++;
            if (p == word1[idx1].length()) {
                idx1++;
                p = 0;
            }
            if (q == word2[idx2].length()) {
                idx2++;
                q = 0;
            }
        }

        return idx1 == len1 && idx2 == len2;
    }
}
