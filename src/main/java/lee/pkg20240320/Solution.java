package lee.pkg20240320;

// https://leetcode.cn/problems/minimum-non-zero-product-of-the-array-elements/solutions/936621/tan-xin-ji-qi-shu-xue-zheng-ming-by-endl-uumv/?envType=daily-question&envId=2024-03-20
public class Solution {
    private static final int MOD = 1_000_000_007;

    // x ^ ((2^p - 1) - 1)
    private long pow(long x, int p) {
        x %= MOD;
        long res = 1;
        while (p-- > 0) {
            // 2 3
            // 2 4 8
            // 2 * 4 * 8
            System.out.println(res);
            System.out.println(x);
            System.out.println();
            res = res * x % MOD;
            x = x * x % MOD;
        }
        return res;
    }


    // 2 ^ (1  2  4)
    // 2*1 = 2  2*2=4 2*4 = 16
    // think about the math method

    // 2 * 4 * 16 = 128

    // 2 ^ 3
    // 2 2 2
    // 2 * 2 * 2 = 8
    private long pow1(long x, int p) {
        x %= MOD;
        long res = 1;
        while (p-- > 0) {
            res = res * x % MOD;

            x = x * x % MOD; //  the core line we should be aware of
        }

        return res;
    }

//sout


    public static void main(String[] args) {
        var app = new Solution();
        int p = 4;
        System.out.println(app.pow(2, p - 1));
    }
}
