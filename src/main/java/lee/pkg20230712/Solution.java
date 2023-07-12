package lee.pkg20230712;

public class Solution {
    //    https://leetcode.cn/problems/alternating-digit-sum/
    public int alternateDigitSum(int n) {
        String s = String.valueOf(n);
        int ret = 0;
        for (int i = 0; i < s.length(); i++) {
            if (i % 2 == 0) {
                ret += s.charAt(i) - '0';
            } else {
                ret -= s.charAt(i) - '0';
            }
        }
        return ret;
    }

    // https://leetcode.cn/problems/alternating-digit-sum/solution/javapython3shu-xue-mo-ni-tu-jie-by-lxk12-uvbg/
    public int alternateDigitSum1(int n) {
        int sign = 1;   // 符号位
        int sum = 0;    // 交替数字和
        while (n > 0) {
            sum += sign * (n % 10);     // n % 10取当前最低位，乘以sign得到交替符号
            sign *= -1;                 // sign值交替变化
            n /= 10;                    // 去除n的最低位
        }
        // 如果结束时sign为负，说明结束时高位为正，sum * (-sign) = sum * 1 = sum
        // 如果结束时sign为正，说明结束时高位为负，sum * (-sign) = sum * (-1) = -sum
        return -sign * sum;
    }

    public static void main(String[] args) {

    }
}
