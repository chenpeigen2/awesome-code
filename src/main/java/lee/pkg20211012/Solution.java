package lee.pkg20211012;

public class Solution {

//    假设我们的环境只能存储 32 位有符号整数，其数值范围是 [−231,  231 − 1]。本题中，
//    如果除法结果溢出，则返回 231 − 1。

    public int divide(int dividend, int divisor) {
        if (dividend == 0) {
            return 0;
        }

        if (divisor == 1) {
            return dividend;
        }

        if (divisor == -1) {
            // -2 ** 31 / -1 情况
            if (dividend > Integer.MIN_VALUE) {
                return -dividend;
            }
            return Integer.MAX_VALUE;
        }

        long a = dividend;
        long b = divisor;

        int sign = 1;
        if ((a > 0 && b < 0) || (a < 0 && b > 0)) {
            sign = -1;
        }

        a = a > 0 ? a : -a;
        b = b > 0 ? b : -b;

        int res = divideDetail(a, b);

        if (sign > 0) {
            return res;
        }
        return -res;
    }

    private int divideDetail(long a, long b) {
        if (a < b) return 0;
        int cnt = 1;

        long tb = b;

        while ((tb + tb) <= a) {
            cnt = cnt + cnt;
            tb = tb + tb;
        }
        return cnt + divideDetail(a - tb, b);
    }
}
