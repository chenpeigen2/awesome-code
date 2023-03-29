package lee.pkg20220517;

public class Solution {

    //    https://leetcode.cn/problems/verifying-an-alien-dictionary/
    public boolean isAlienSorted(String[] words, String order) {
        int[] idx = new int[order.length()];
        for (int i = 0; i < order.length(); i++) {
            idx[order.charAt(i) - 'a'] = i;
        }

        search:
        for (int i = 0; i < words.length - 1; i++) {
            String word1 = words[i];
            String word2 = words[i + 1];

            for (int k = 0; k < Math.min(word1.length(), word2.length()); k++) {
                if (word1.charAt(k) != word2.charAt(k)) {
                    if (idx[word1.charAt(k) - 'a'] > idx[word2.charAt(k) - 'a']) return false;
                    continue search;
                }
            }
            if (word1.length() > word2.length()) return false;
        }
        return true;
    }

}
