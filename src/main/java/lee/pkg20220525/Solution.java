package lee.pkg20220525;

import java.util.Arrays;

public class Solution {
    //    https://leetcode.cn/problems/unique-substrings-in-wraparound-string/
    public int findSubstringInWraproundString(String p) {
        int[] dp = new int[26];
        for (int i = 0, k = 0; i < p.length(); i++) {
            if (i > 0 && (p.charAt(i) - p.charAt(i - 1) + 26) % 26 == 1) k++;
            else k = 1;
            dp[p.charAt(i) - 'a'] = Math.max(dp[p.charAt(i) - 'a'], k);
        }
        return Arrays.stream(dp).sum();
    }

    public static void main(String[] args) {
        System.out.println('a' - 'z');
    }
}
