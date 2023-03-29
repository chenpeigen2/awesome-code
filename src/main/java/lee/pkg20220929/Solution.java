package lee.pkg20220929;

public class Solution {
    //    https://leetcode.cn/problems/string-rotation-lcci/
    public boolean isFlipedString(String s1, String s2) {
        return s1.length() == s2.length() && (s1 + s1).contains(s2);
    }
}
