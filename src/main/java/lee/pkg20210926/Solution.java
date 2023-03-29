package lee.pkg20210926;

public class Solution {
    public int getSum(int a, int b) {
//        a ^ b 判断加法
//        a & b 判断进位
        return b == 0 ? a : getSum(a ^ b, (a & b) << 1);
    }
}