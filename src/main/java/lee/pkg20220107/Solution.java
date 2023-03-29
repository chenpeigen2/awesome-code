package lee.pkg20220107;

public class Solution {
    public int maxDepth(String s) {
        int ans = 0;
        int tmp = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(') {
                tmp++;
            } else if (s.charAt(i) == ')') {
                tmp--;
            }
            ans = Math.max(ans, tmp);
        }
        return ans;
    }

    public static void main(String[] args) {
        var s = "1";
        var app = new Solution();
        var ans = app.maxDepth(s);
        System.out.println(ans);
    }
}
