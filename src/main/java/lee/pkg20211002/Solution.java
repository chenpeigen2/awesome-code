package lee.pkg20211002;

public class Solution {
    public String toHex(int _num) {
        if (_num == 0) {
            return "0";
        }

        StringBuilder sb = new StringBuilder();
        char[] b = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        long num = _num;
        if (num < 0) {
            num = (long) (Math.pow(2, 32) + num);
        }
        // if not long then 7ffffff
        while (num != 0) {
            long u = num % 16;
            sb.append(b[(int) u]);
            num /= 16;
        }
        // 123 % 10 == 3 here
        // 123 /10 = 12
        // 12 % 10 = 2 here 12/10 = 1
        // 1%10 = 1 here 1/10 = 0
        return sb.reverse().toString();
    }
}
