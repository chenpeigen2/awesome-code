package lee.pkg20220213;

public class Solution {
    public int maxNumberOfBalloons(String text) {
        // balloon
        // a b oo ll n
        int[] cnts = new int[5];
        for (int i = 0; i < text.length(); i++) {
            char tmp = text.charAt(i);
            if (tmp == 'a') {
                cnts[0]++;
            } else if (tmp == 'b') {
                cnts[1]++;
            } else if (tmp == 'o') {
                cnts[2]++;
            } else if (tmp == 'l') {
                cnts[3]++;
            } else if (tmp == 'n') {
                cnts[4]++;
            }
        }
        int ans = Integer.MAX_VALUE;
        for (int i = 0; i < 5; i++) {
            if (i == 2 || i == 3) cnts[i] /= 2;
            ans = Math.min(ans, cnts[i]);
        }

        return ans;
    }
}
