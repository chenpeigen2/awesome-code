package lee.pkg20221108;

public class Solution {
    //    https://leetcode.cn/problems/count-the-number-of-consistent-strings/description/
    public int countConsistentStrings(String allowed, String[] words) {
        int ans = 0;
        int len = allowed.length();
        int[] characters = new int[26];
        for (int i = 0; i < len; i++) {
            characters[allowed.charAt(i) - 'a']++;
        }
        for (String word : words) {
            int l = word.length();
            boolean added = true;
            for (int i = 0; i < l; i++) {
                if (characters[word.charAt(i) - 'a'] == 0) {
                    added = false;
                    break;
                }
            }
            if (added) ans++;
        }
        return ans;
    }
}
