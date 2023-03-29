package lee.pkg20221213;

public class Solution {

    //    https://leetcode.cn/problems/check-if-the-sentence-is-pangram/
    public boolean checkIfPangram(String sentence) {
        if (sentence.length() < 26) return false;
        int[] cnts = new int[26];
        for (int i = 0; i < sentence.length(); i++) {
            cnts[sentence.charAt(i) - 'a']++;
        }
        for (int i = 0; i < 26; i++) {
            if (cnts[i] == 0) return false;
        }
        return true;
    }
}
