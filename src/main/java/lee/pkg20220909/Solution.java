package lee.pkg20220909;

public class Solution {
    //    https://leetcode.cn/problems/crawler-log-folder/
    public int minOperations(String[] logs) {
        int depth = 0;
        for (String log : logs) {
            if ("../".equals(log)) {
                depth = Math.max(0, depth - 1);
                continue;
            }
            if ("./".equals(log)) continue;
            depth++;
        }

        return depth;
    }
}
