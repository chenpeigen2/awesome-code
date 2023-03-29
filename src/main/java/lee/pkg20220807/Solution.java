package lee.pkg20220807;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class Solution {
    //    https://leetcode.cn/problems/exclusive-time-of-functions/

    //    [] 闭区间
    public int[] exclusiveTime(int n, List<String> logs) {
        int[] ans = new int[n];
        int cur = -1;
        Deque<Integer> q = new ArrayDeque<>();
        for (String log : logs) {
            String[] splits = log.split(":");
            int idx = Integer.parseInt(splits[0]), stamp = Integer.parseInt(splits[2]);
            if (splits[1].equals("start")) {
                if (!q.isEmpty()) ans[q.peekFirst()] += stamp - cur;
                q.offerFirst(idx);
                cur = stamp;
            } else {
                ans[q.pollFirst()] += stamp - cur + 1;
                cur = stamp + 1;
            }
        }
        return ans;
    }
}
