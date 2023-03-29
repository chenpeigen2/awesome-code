package lee.pkg20220305;

public class Solution {
    //    https://leetcode-cn.com/problems/longest-uncommon-subsequence-i/
    public int findLUSlength(String a, String b) {
        return a.equals(b) ? -1 : Math.max(a.length(), b.length());
    }
}
