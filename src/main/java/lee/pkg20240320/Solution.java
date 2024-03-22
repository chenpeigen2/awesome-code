package lee.pkg20240320;

public class Solution {
    private static final int MOD = 1_000_000_007;

    private long pow(long x, int p) {
        x %= MOD;
        long res = 1;
        while (p-- > 0) {
            // 2 3
            // 2 4 8
            // 2 * 4 * 8
            res = res * x % MOD;
            x = x * x % MOD;
        }
        return res;
    }

    public static void main(String[] args) {
        var app = new Solution();
        System.out.println(app.pow(2, 3));
    }
}
