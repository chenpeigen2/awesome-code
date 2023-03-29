package lee.pkg20230320;

import java.util.Arrays;

public class Solution {
    char s[];
    int memo[][];

    //    https://leetcode.cn/problems/numbers-with-repeated-digits/solution/by-endlesscheng-c5vg/
    public int numDupDigitsAtMostN(int n) {
        s = String.valueOf(n).toCharArray();
        int m = s.length;
        memo = new int[m][1 << 9 + 1];
        for (int i = 0; i < m; i++) {
            Arrays.fill(memo[i], -1);
        }
        return n - f(0, 0, true, false);
    }

    int f(int i, int mask, boolean isLimit, boolean isNum) {
        if (i == s.length) //如果到投了，如果前一位填的是数字，直接返回，相当于走到了length，这个时候向后走无意义
            return isNum ? 1 : 0;

        if (!isLimit && isNum && memo[i][mask] != -1) // 如果还没有到123limit，并且之前就存在这个数字，那就直接返回
            return memo[i][mask];


        int ret = 0;
        if (!isNum) // 表示前面一位是否填了数字，那就直接继续，但是此时没有limit
            ret = f(i + 1, mask, false, false);

        int up = isLimit ? s[i] - '0' : 9; // 123 最后一位只能填 110 - 119
        // isNum如果为真相当于12 后面要从0开始填写
        for (int d = isNum ? 0 : 1; d <= up; d++) {
            if ((mask >> d & 1) == 0) {
                ret += f(i + 1, mask | (1 << d), isLimit && d == up, true);
            }
        }
        // 如果还没有到123limit的限制和还是个数字的话就先存一份dp
        if (!isLimit && isNum) {
            memo[i][mask] = ret;
        }
        // 返回数据
        return ret;
    }
}
