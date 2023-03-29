package lee.pkg20220303;

public class Solution {
    //    https://leetcode-cn.com/problems/add-digits/
    public int addDigits(int num) {
        while (num >= 10) {
            int tmp = 0;
            while (num > 0) {
                tmp += num % 10;
                num /= 10;
            }
            num = tmp;
        }
        return num;
    }

    public static void main(String[] args) {
        var app = new Solution();
        var ans = app.addDigits(38);
        System.out.println(ans);
    }
}
