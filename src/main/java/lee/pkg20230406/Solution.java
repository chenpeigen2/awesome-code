package lee.pkg20230406;

public class Solution {
    //    https://leetcode.cn/problems/convert-to-base-2/solution/python3javacgotypescript-yi-ti-yi-jie-mo-5edi/
    public String baseNeg2(int n) {
        if (n == 0) return "0";
        int k = 1;
        StringBuilder ans = new StringBuilder();
        while (n != 0) {
            if (n % 2 != 0) {
                ans.append(1);
                n -= k;
            } else {
                ans.append(0);
            }
            k *= -1;
            n /= 2;
        }
        return ans.reverse().toString();
    }

    public static void main(String[] args) {
        var app = new Solution();
        var ans = app.baseNeg2(6);
        System.out.println(ans);
    }
}
