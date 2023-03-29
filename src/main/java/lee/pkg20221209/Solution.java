package lee.pkg20221209;

public class Solution {
    // 想一想十进制转二进制怎么来的
    //    https://leetcode.cn/problems/check-if-number-is-a-sum-of-powers-of-three/
    public boolean checkPowersOfThree(int n) {
        while (n != 0) {
            if (n % 3 == 2) return false;
            n /= 3;
        }
        return true;
    }
}
