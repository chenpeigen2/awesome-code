package lee.pkg20211130;

public class Solution {
    public int findNthDigit(int n) {
        int cur = 1;
        int base = 9;
        while (n > cur * base) {
            n -= cur * base;
            cur++;
            base *= 10;
            if (Integer.MAX_VALUE / base < cur)
                break;
        }
        // find the index
        n--;
        int cnt = n / cur;
        int num = (int) (Math.pow(10, cur - 1) + cnt);
        // 从 0开始的第几位 0
        int idx = n % cur;
        return (int) (num / Math.pow(10, cur - 1 - idx) % 10);
    }

    public static void main(String[] args) {
        var app = new Solution();
        var result = app.findNthDigit(11);
        System.out.println(result);
    }
}
