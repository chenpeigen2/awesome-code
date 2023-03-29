package lee.pkg20220403;

import java.util.Arrays;

public class Solution {
    //    https://leetcode-cn.com/problems/find-smallest-letter-greater-than-target/
    public char nextGreatestLetter(char[] letters, char target) {
//        Arrays.sort(letters);
        for (char letter : letters) {
            if (target < letter) {
                return letter;
            }
        }
        return letters[0];
    }

    public static void main(String[] args) {
        var app = new Solution();
        var ans = app.nextGreatestLetter(new char[]{'c', 'f', 'j'}, 'c');
        System.out.println(ans);
    }
}
