package lee.pkg20220117;

import java.util.Arrays;

//https://leetcode-cn.com/problems/count-vowels-permutation/

public class Solution {
    int MOD = (int) 1e9 + 7;

    public int countVowelPermutation(int n) {
        long[][] f = new long[n][5];
        Arrays.fill(f[0], 1);
        for (int i = 0; i < n - 1; i++) {
            f[i + 1][1] += f[i][0];

            f[i + 1][0] += f[i][1];
            f[i + 1][2] += f[i][1];

            f[i + 1][0] += f[i][2];
            f[i + 1][1] += f[i][2];
            f[i + 1][3] += f[i][2];
            f[i + 1][4] += f[i][2];

            f[i + 1][2] += f[i][3];
            f[i + 1][4] += f[i][3];

            f[i + 1][0] += f[i][4];

            for (int j = 0; j < 5; j++) f[i + 1][j] %= MOD;
        }

        long ans = 0;
        for (int i = 0; i < 5; i++) {
            ans += f[n - 1][i];
        }
        return (int) (ans % MOD);
    }
}
