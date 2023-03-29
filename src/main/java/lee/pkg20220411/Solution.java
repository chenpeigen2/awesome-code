package lee.pkg20220411;

public class Solution {
    //    https://leetcode-cn.com/problems/count-numbers-with-unique-digits/
    //    f(0)=1
//    f(1)=10
//    f(2)=9x9+f(1)
//    f(3)=9x9x8+f(2)
//    f(4)=9x9x8x7+f(3)
//    根据以上规律，可以得到递推公式：f(i) = f(i-1) + (f(i-1)-f(i-2))x(10-(i-1))
    public int countNumbersWithUniqueDigits(int n) {
        if (n == 0) return 1;
        int ans = 10;
        for (int i = 2, last = 9; i <= n; i++) {
            // 9-choices 8-choices 7-choices 6-choices 5-choices 4-choices 3-choices
            int cur = last * (10 - i + 1);
            ans += cur;
            last = cur;
        }
        return ans;
    }

    public static void main(String[] args) {
        var app = new Solution();
        var ans = app.countNumbersWithUniqueDigits(3);
        System.out.println(ans);
    }
}
