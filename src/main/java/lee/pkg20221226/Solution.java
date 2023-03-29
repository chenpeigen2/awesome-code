package lee.pkg20221226;

public class Solution {

    private static final int MOD = (int) 1e9 + 7;

    //    https://leetcode.cn/problems/count-number-of-homogenous-substrings/
    public int countHomogenous(String s) {
        long ans = 0;
        int i = 0, j = 0;
        int len = s.length();
        while (j < len) {
            if (s.charAt(i) == s.charAt(j)) {
                j++;
            } else {
                ans += (long) (j - i + 1) * (j - i) / 2;
                ans %= MOD;
                i = j;
            }
        }
        ans += (long) (j - i + 1) * (j - i) / 2;
        ans %= MOD;
        return (int) ans;
    }

    public int countHomogenous1(String s) {
        int n = s.length(), k = 1;
        long res = 1;
        char[] ch = s.toCharArray();
        for (int i = 1; i < n; i++) {
            if (ch[i] == ch[i-1]) k++;
            else k = 1;
            res += k;
        }
        return (int) (res % 1000000007);
    }

    public static void main(String[] args) {
        var x = "zzzzz";
        var app = new Solution();
        var ans = app.countHomogenous(x);
        System.out.println(ans);

        System.out.println(1e2 + 7);
    }
}
