package lee.pkg20220917;

import java.util.Arrays;

public class Solution {
    //    https://leetcode.cn/problems/largest-substring-between-two-equal-characters/
    public int maxLengthBetweenEqualCharacters(String s) {
        int[] firstIndex = new int[26];
        Arrays.fill(firstIndex, -1);
        int maxLen = -1;
        for (int i = 0; i < s.length(); i++) {
            if (firstIndex[s.charAt(i) - 'a'] < 0) {
                firstIndex[s.charAt(i) - 'a'] = i;
            } else {
                maxLen = Math.max(maxLen, i - 1 - firstIndex[s.charAt(i) - 'a']);
            }
        }
        return maxLen;
    }
}
