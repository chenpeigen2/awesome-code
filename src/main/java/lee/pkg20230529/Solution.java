package lee.pkg20230529;

public class Solution {
    //    https://leetcode.cn/problems/average-value-of-even-numbers-that-are-divisible-by-three/
    public int averageValue(int[] nums) {
        int sum = 0;
        int cnt = 0;
        for (int num : nums) {
            if (num % 6 == 0) {
                sum += num;
                cnt++;
            }
        }
        if (cnt == 0) return sum;
        return sum / cnt;
    }

    public static void main(String[] args) {
        var app = new Solution();
        app.averageValue(new int[]{1, 3, 6, 10, 12, 15});
    }
}
