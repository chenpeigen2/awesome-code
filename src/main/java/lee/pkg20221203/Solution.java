package lee.pkg20221203;

public class Solution {
    //    https://leetcode.cn/problems/second-largest-digit-in-a-string/
    public int secondHighest(String s) {
        int[] numbers = new int[10];
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isDigit(c)) {
                numbers[c - '0']++;
            }
        }
        int ans = -1;
        boolean maxed = false;
        for (int i = 9; i >= 0; i--) {
            if (numbers[i] > 0) {
                if (maxed) return i;
                maxed = true;
            }
        }
        return ans;
    }
}
