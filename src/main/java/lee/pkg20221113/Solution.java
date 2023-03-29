package lee.pkg20221113;

public class Solution {
    //    https://leetcode.cn/problems/custom-sort-string/
    public String customSortString(String order, String s) {
        int[] cnts = new int[26];
        for (char c : s.toCharArray()) cnts[c - 'a']++;
        StringBuilder sb = new StringBuilder();
        for (char c : order.toCharArray()) {
            while (cnts[c - 'a']-- > 0) sb.append(c);
        }
        for (int i = 0; i < 26; i++) {
            while (cnts[i]-- > 0) sb.append((char) (i + 'a'));
        }
        return sb.toString();
    }
}
