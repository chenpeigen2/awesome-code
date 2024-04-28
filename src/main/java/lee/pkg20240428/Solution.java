package lee.pkg20240428;

public class Solution {
    //    https://leetcode.cn/problems/convert-to-base-2/?envType=daily-question&envId=2024-04-28
    public String baseNeg2(int n) {
        if (n == 0) return "0";
        int k = 1;
        StringBuilder sb = new StringBuilder();
        while (n != 0) {
            if (n % 2 != 0) {
                sb.append(1);
                n -= k;
            } else {
                sb.append(0);
            }
            n /= 2;
            k *= -1;
        }
        return sb.reverse().toString();
    }
}
