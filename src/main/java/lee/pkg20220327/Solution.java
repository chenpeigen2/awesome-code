package lee.pkg20220327;

public class Solution {
    //    https://leetcode-cn.com/problems/find-missing-observations/
    public int[] missingRolls(int[] rolls, int mean, int n) {
        int m = rolls.length;
        int sum = (m + n) * mean;
        for (int roll : rolls) {
            sum -= roll;
        }
        if (6 * n < sum || sum < n) return new int[0];
        int avg = sum / n;
        int left = sum % n;
        int[] ans = new int[n];
        for (int i = 0; i < n; i++) {
            ans[i] = avg + ((i < left) ? 1 : 0);
        }

        return ans;
    }
}
