package lee.pkg20230617;

public class Solution {
    //    https://leetcode.cn/problems/minimum-cuts-to-divide-a-circle/
    public int numberOfCuts(int n) {
        if (n == 1) return 0;
        if (n % 2 == 0) return n / 2;
        return n;
    }
}
