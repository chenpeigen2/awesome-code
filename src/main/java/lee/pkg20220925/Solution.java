package lee.pkg20220925;

public class Solution {
    /**
     * 我们称一个数 X 为好数,
     * 如果它的每位数字逐个地被旋转 180 度后，
     * 我们仍可以得到一个有效的，且和 X 不同的数。
     * 除了这些以外其他的数字旋转以后都不再是有效的数字。
     * <p>
     * https://leetcode.cn/problems/rotated-digits/
     */
    public int rotatedDigits(int n) {
        int ans = 0;
        out:
        for (int i = 1; i <= n; i++) {
            boolean ok = false;
            int x = i;
            while (x != 0) {
                int t = x % 10;
                x /= 10;
                if (t == 2 || t == 5 || t == 6 || t == 9) ok = true;
                else if (t != 0 && t != 1 && t != 8) continue out;
            }
            if (ok) ans++;
        }
        return ans;
    }
}
