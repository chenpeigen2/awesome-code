package lee.pkg20220819;

import java.util.Arrays;

public class Solution {
    //    https://leetcode.cn/problems/number-of-students-doing-homework-at-a-given-time/
    public int busyStudent(int[] startTime, int[] endTime, int queryTime) {
        int ans = 0;
        int len = startTime.length;
        for (int i = 0; i < len; i++) {
            if (queryTime >= startTime[i] && queryTime <= endTime[i]) ans++;
        }
        return ans;
    }


    public int busyStudent1(int[] startTime, int[] endTime, int queryTime) {
        int n = startTime.length;
        int maxEndTime = Arrays.stream(endTime).max().getAsInt();
        if (queryTime > maxEndTime) {
            return 0;
        }
        int[] cnt = new int[maxEndTime + 2];
        for (int i = 0; i < n; i++) {
            cnt[startTime[i]]++;
            cnt[endTime[i] + 1]--;
        }
        int ans = 0;
        for (int i = 0; i <= queryTime; i++) {
            ans += cnt[i];
        }
        return ans;
    }
}
