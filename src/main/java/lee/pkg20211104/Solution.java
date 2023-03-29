package lee.pkg20211104;

public class Solution {
    public boolean isPerfectSquare(int num) {
        long l = 0;
        long r = num;
        while (l < r) {
            long tmp = (l + r + 1) >> 1;
            if (tmp * tmp <= num) {
                l = tmp;
            } else {
                r = tmp - 1;
            }
        }
        return l * l == num;
    }

    public boolean isPerfectSquare1(int num) {
        for (int i = 1; num > 0; i += 2) {
            num -= i;
        }
        return num == 0;
    }
}
