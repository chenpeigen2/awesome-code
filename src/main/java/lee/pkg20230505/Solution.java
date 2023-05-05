package lee.pkg20230505;


public class Solution {

    //    https://leetcode.cn/problems/the-employee-that-worked-on-the-longest-task/
    public int hardestWorker(int n, int[][] logs) {
        int ret = -1;
        int max = -1;

        for (int i = 0; i < logs.length; i++) {
            if (i == 0) {
                ret = logs[i][0];
                max = logs[i][1];
                continue;
            }
            int expectedMax = logs[i][1] - logs[i - 1][1];
            if (expectedMax > max) {
                max = expectedMax;
                ret = logs[i][0];
            } else if (expectedMax == max) {
                ret = Math.min(logs[i][0], ret);
            }
        }

        return ret;
    }
}
