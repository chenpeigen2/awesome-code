package lee.pkg20230815;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Solution {
    //    https://leetcode.cn/problems/find-and-replace-in-string/
    public String findReplaceString(String s, int[] indices, String[] sources, String[] targets) {
        int n = s.length();
        var replaceStr = new String[n];
        var replaceLen = new int[n];
        Arrays.fill(replaceLen, 1);

        for (int i = 0; i < indices.length; i++) {
            int idx = indices[i];
            if (s.startsWith(sources[i], idx)) {
                replaceStr[idx] = targets[i];
                replaceLen[idx] = sources[i].length();
            }
        }

        var ans = new StringBuilder();
        for (int i = 0; i < n; i += replaceLen[i]) {
            if (replaceStr[i] == null) {
                ans.append(s.charAt(i));
            } else {
                ans.append(replaceStr[i]);
            }
        }
        return ans.toString();
    }
}
