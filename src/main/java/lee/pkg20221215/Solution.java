package lee.pkg20221215;

public class Solution {
    public int getLucky(String s, int k) {
        int ans = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int num = c - 'a' + 1;
            while (num > 0) {
                ans += (num % 10);
                num /= 10;
            }
        }
        for (int i = 1; i < k; i++) {
            int oldans = ans;
            ans = 0;
            while (oldans > 0) {
                ans += (oldans % 10);
                oldans /= 10;
            }
        }
        return ans;
    }
}
