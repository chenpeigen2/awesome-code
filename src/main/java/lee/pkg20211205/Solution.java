package lee.pkg20211205;

public class Solution {
    /**
     * 你的任务是计算 ab 对 1337 取模，a 是一个正整数，b 是一个非常大的正整数且会以数组形式给出。
     *
     * @param a lower
     * @param b higher
     * @return the val
     */

    int MOD = 1337;

    // 2 ^ 123
    public int superPow(int a, int[] b) {
        return dfs(a, b, b.length - 1);
    }

    int dfs(int a, int[] b, int u) {
        if (u == -1) return 1;
        // next 2^120 * 2 ^ 3 AND we still need to MOD
        //99^2345 == ( (99^234)^10 * 99^5 ) % MOD ===>
        return qpow(dfs(a, b, u - 1), 10) * qpow(a, b[u]) % MOD;
    }

    // 2 ^ (1001b) ==>
    // 2 ^ (1+8) = (2^1 * 2^8) % MOD == split MOD
    int qpow(int a, int b) {
        int ans = 1;
        a %= MOD;
        while (b != 0) {
            // if b 那个位置存在1 奇数
            if ((b & 1) != 0) ans = ans * a % MOD;
            a = a * a % MOD;
            b >>= 1;
        }
        return ans;
    }
}
