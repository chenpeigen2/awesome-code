package lee.pkg20221208;

public class Solution {
    //    https://leetcode.cn/problems/determine-color-of-a-chessboard-square/
    public boolean squareIsWhite(String coordinates) {
        char a = coordinates.charAt(0);
        char b = coordinates.charAt(1);
        boolean f1 = (a - 'a') % 2 == 0;
        boolean f2 = Character.getNumericValue(b) % 2 == 0;
        return !(f1 == !f2);
    }

    public static void main(String[] args) {
        var a = new Solution();
        a.squareIsWhite("a1");
    }
}
