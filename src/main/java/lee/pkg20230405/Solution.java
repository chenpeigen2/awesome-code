package lee.pkg20230405;

public class Solution {

    //    https://leetcode.cn/problems/number-of-common-factors/
    public int commonFactors(int a, int b) {
        int ans = 0;
        for (int i = 1; i <= Math.min(a, b); i++) {
            if (a % i == 0 && b % i == 0) {
                System.out.println(i);
                ans++;
            }
        }
        return ans;
    }

    public static void main(String[] args) {
        var app = new Solution();
        app.commonFactors(12, 6);
    }
}
