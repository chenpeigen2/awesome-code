package lee.pkg20211217;

public class Solution {
    public int numWaterBottles(int numBottles, int numExchange) {
        int ans = numBottles;
        while (numBottles >= numExchange) {
            int cnt = numBottles / numExchange;
            int left = numBottles % numExchange;
            numBottles = cnt + left;
            ans += cnt;
        }
        return ans;
    }

    public int numWaterBottles1(int n, int m) {
        int cnt = n / (m - 1);
        return n % (m - 1) == 0 ? n + cnt - 1 : n + cnt;
    }

    public static void main(String[] args) {
        var app = new Solution();
        var ans = app.numWaterBottles(9, 3);
        System.out.println(ans);
    }
}
