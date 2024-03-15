package lee.pkg20240313;

public class Solution {
    //    https://leetcode.cn/problems/maximum-odd-binary-number/?envType=daily-question&envId=2024-03-13
    public String maximumOddBinaryNumber(String s) {
        int len = s.length();
        int sizeOf1 = 0;
        for (int i = 0; i < len; i++) {
            if (s.charAt(i) - '1' == 0) {
                sizeOf1++;
            }
        }
        int sizeOf0 = len - sizeOf1;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sizeOf1 - 1; i++) {
            sb.append('1');
        }
        for (int i = 0; i < sizeOf0; i++) {
            sb.append('0');
        }
        sb.append('1');
        return sb.toString();
    }
}
