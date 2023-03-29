package lee.pkg20220915;

public class Solution {
    //    https://leetcode.cn/problems/bulb-switcher-ii/solution/dengp-by-capital-worker-51rb/
    public int flipLights(int n, int presses) {
        if (presses == 0) return 1;
        if (n == 1) return 2;
        else if (n == 2) return presses == 1 ? 3 : 4;
        else return presses == 1 ? 4 : presses == 2 ? 7 : 8;
    }
}
