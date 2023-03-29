package lee.pkg20221027;

public class Solution {
    public int arraySign(int[] nums) {
        boolean isPositive = true;
        for (int x : nums) {
            if (x == 0) return 0;
            else if (x < 0) {
                isPositive = !isPositive;
            }
        }
        return isPositive ? 1 : -1;
    }

    public static void main(String[] args) {
        var app = new Solution();
        var ans = app.arraySign(new int[]{1, 2, -3, -4});
        System.out.println(ans);
    }
}
