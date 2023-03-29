package lee.pkg20220528;

public class Solution {
    //    https://leetcode.cn/problems/remove-outermost-parentheses/
    public String removeOuterParentheses(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0, idx = -1, cnt = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(') {
                if (cnt == 0) {
                    idx = i;
                }
                cnt++;
            } else {
                cnt--;
                if (cnt == 0) {
                    sb.append(s, idx + 1, i);
                }
            }
        }
        return sb.toString();
    }
}
