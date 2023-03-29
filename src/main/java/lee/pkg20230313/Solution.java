package lee.pkg20230313;

public class Solution {
    //    https://leetcode.cn/problems/minimum-hours-of-training-to-win-a-competition/
    public int minNumberOfHours(int initialEnergy, int initialExperience, int[] energy, int[] experience) {
        int result = 0;
        for (int ene : energy) {
            if (initialEnergy > ene) {
                initialEnergy -= ene;
            } else {
                result += (ene + 1) - initialEnergy;
                initialEnergy = 1;
            }
        }
        for (int exp : experience) {
            if (initialExperience > exp) {
                initialExperience += exp;
            } else {
                result += (exp + 1) - initialExperience;
                initialExperience = exp + exp + 1;
            }
        }
        return result;
    }
}
