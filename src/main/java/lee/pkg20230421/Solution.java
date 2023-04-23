package lee.pkg20230421;

public class Solution {

    //    https://leetcode.cn/problems/smallest-even-multiple/
    public int smallestEvenMultiple(int n) {
        return n % 2 == 0 ? n : 2 * n;
    }
}
