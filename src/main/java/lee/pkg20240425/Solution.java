package lee.pkg20240425;

public class Solution {
//    https://leetcode.cn/problems/total-distance-traveled/?envType=daily-question&envId=2024-04-25
    public int distanceTraveled(int mainTank, int additionalTank) {
        int ans = 0;

        while (mainTank >= 5) {
            mainTank -= 5;
            ans += 50;
            if (additionalTank > 0) {
                additionalTank--;
                mainTank++;
            }
        }

        return ans + mainTank*10;

    }
}
