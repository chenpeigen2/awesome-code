package lee.pkg20210927;

public class Solution {
    static final int MOD = 1000000007;

    public int numDecodings(String s) {
        int n = s.length();

        long a = 0, b = 1, c = 0;

        for (int i = 1; i <= n; i++) {
            c = b * check1digit(s.charAt(i - 1)) % MOD;
            if (i > 1) {
                c = (c + a * check2digits(s.charAt(i - 2), s.charAt(i - 1))) % MOD;
            }
            // a 代表 【-2】次
            a = b;
            // b 代表前面之前有几次的 【-1】次
            b = c;
        }

        return (int) c;
    }


    private int check1digit(char ch) {
        if (ch == '0') {
            return 0;
        }
        return ch == '*' ? 9 : 1;
    }

    private int check2digits(char c0, char c1) {
        if (c0 == '*' && c1 == '*') {
            // [11,19] --> [21,26]
            return 15;
        }
        if (c0 == '*') {
//             * [1,2,3,4,5,6] can choose 1 or 2
            return c1 <= '6' ? 2 : 1;
        }
        if (c1 == '*') {
            // 1,2,3,4,5,6,7,8,9 *
            if (c0 == '1') {
                return 9;
            }
            if (c0 == '2') {
                return 6;
            }
            return 0;
        }
        // 两个都不是* 即两个都是数字
        return (c0 != '0' && (c0 - '0') * 10 + (c1 - '0') <= 26) ? 1 : 0;
    }

}
