package lee.pkg20220527;

public class Solution {
    //    https://leetcode.cn/problems/find-closest-lcci/
    public int findClosest(String[] words, String word1, String word2) {
        int n = words.length, ans = n;
        for (int i = 0, p = -1, q = -1; i < n; i++) {
            String t = words[i];
            if (t.equals(word1)) p = i;
            if (t.equals(word2)) q = i;
            if (p != -1 && q != -1) ans = Math.min(ans, Math.abs(p - q));
        }
        return ans;
    }

}
