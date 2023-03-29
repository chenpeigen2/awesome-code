package lee.pkg20220509;

public class Solution {
    //    https://leetcode.cn/problems/di-string-match/
    public int[] diStringMatch(String s) {
        int n = s.length();
        int[] ans = new int[n + 1];
        int l = 0, r = n;
        int i = 0;
        for (; i < n; i++) {
            ans[i] = s.charAt(i) == 'I' ? l++ : r--;
        }
        ans[i] = l;
        return ans;
    }
}
