package lee.pkg20240601;

public class Solution {
    //    https://leetcode.cn/problems/distribute-candies-among-children-i/?envType=daily-question&envId=2024-06-01
    public int distributeCandies(int n, int limit) {
        int ans = 0;
        for (int i = 0; i <= limit; i++) {
            for (int j = 0; j <= limit; j++) {
                if (i + j > n) break;
                if (n - i - j >= 0 && n - i - j <= limit) {
                    ans++;
                }
            }
        }
        return ans;
    }
}
