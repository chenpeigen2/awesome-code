package lee.pkg20220328;

public class Solution {
    //    https://leetcode-cn.com/problems/binary-number-with-alternating-bits/
    public boolean hasAlternatingBits(int n) {
        int k = -1;
        do {
            int ans = n & 1;
            if ((k ^ ans) == 0) return false;
            k = ans;
            n >>= 1;
        } while (n > 0);
        return true;
    }

    public static void main(String[] args) {
        var app = new Solution();
        var ans = app.hasAlternatingBits(11);
        System.out.println(ans);
    }
}
