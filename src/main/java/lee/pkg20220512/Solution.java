package lee.pkg20220512;

public class Solution {
    //    https://leetcode.cn/problems/delete-columns-to-make-sorted/
    public int minDeletionSize(String[] strs) {
        int n = strs.length, m = strs[0].length();
        int ans = 0;
        for (int i = 0; i < m; i++) {
            for (int j = 0, cur = -1; j < n; j++) {
                int t = strs[j].charAt(i);
                if (t < cur) {
                    ans++;
                    break;
                }
                cur = t;
            }
        }
        return ans;
    }
}
