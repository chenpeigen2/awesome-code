package lee.pkg20211201;

public class Solution {
    public int maxPower(String s) {
        int l = 0, r = 0;
        int ans = 0;
        int len = s.length();
        while (r < len) {
            if (s.charAt(l) == s.charAt(r)) {
                r++;
            } else {
                ans = Math.max(ans, r - l);
                l = r;
            }
        }
        ans = Math.max(ans, r - l);
        return ans;
    }

    public static void main(String[] args) {
        var app = new Solution();
        var result = app.maxPower("j");
        System.out.println(result);
    }
}
