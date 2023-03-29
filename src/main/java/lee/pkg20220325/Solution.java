package lee.pkg20220325;

public class Solution {
    //    https://leetcode-cn.com/problems/factorial-trailing-zeroes/
    public int trailingZeroes(int n) {
        return n == 0 ? 0 : n / 5 + trailingZeroes(n / 5);
    }

    public int trailingZeroes1(int n) {
        int ans = 0;
        while (n > 0) {
            n /= 5;
            ans += n;
        }
        return ans;
    }

    // 1 = 1
    // 1 2 = 2
    // 1 2 3 = 6
    // 1 2 3 4 = 24
    // 1 2 3 4 5 = 150
    // 1 2 3 4 5 6 = 720
    // 1 2 3 4 5 6 7 = 5040
    // 1 2 3 4 5 6 7 8 = 40,320
    // 1 2 3 4 5 6 7 8 9 = 362,880
    // 1 2 3 4 5 6 7 8 9 10 = 3628,800

    public static void main(String[] args) {
        var app = new Solution();
        var ans = app.trailingZeroes(6);
        System.out.println(ans);
    }
}
