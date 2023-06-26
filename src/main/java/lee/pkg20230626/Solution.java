package lee.pkg20230626;

public class Solution {

    //    https://leetcode.cn/problems/find-the-pivot-integer/
    public int pivotInteger(int n) {
        int sum = (n + 1) * n / 2;
        int pre = 0;
        for (int i = 1; i <= n; i++) {
            if (pre + i == sum - pre) {
                return i;
            }
            pre += i;
        }
        return -1;
    }
}
