package lee.pkg20210923;

public class Solution {
    public boolean isPowerOfThree(int n) {
//        while (n != 0 && n % 3 == 0) {
//            n /= 3;
//        }
//        return n == 1;
        return n > 0 && 1162261467 % n == 0;

    }
}
