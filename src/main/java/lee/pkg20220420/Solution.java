package lee.pkg20220420;

import java.util.HashMap;
import java.util.Map;

public class Solution {
    //    https://leetcode-cn.com/problems/longest-absolute-file-path/
    public int lengthLongestPath(String s) {
        Map<Integer, String> map = new HashMap<>();
        int n = s.length();
        String ans = null;

        for (int i = 0; i < n; ) {
            int level = 0;
            while (i < n && s.charAt(i) == '\t' && ++level >= 0) i++; // row it up
            int j = i;
            boolean isDir = true;
            while (j < n && s.charAt(j) != '\n') {
                if (s.charAt(j++) == '.') isDir = false;
            }

            String cur = s.substring(i, j); // a file or a dir
            String prev = map.getOrDefault(level - 1, null);
            String path = prev == null ? cur : prev + "/" + cur;
            if (isDir) map.put(level, path);
            else if (ans == null || path.length() > ans.length()) ans = path;
            i = j + 1;
        }

        return ans == null ? 0 : ans.length();
    }


    public int lengthLongestPath1(String s) {
        int ans = 0;
        Map<Integer, Integer> m = new HashMap<>();
        m.put(-1, 0);
        for (String line : s.split("\n")) {
            int level = 0;
            while (level < line.length()) {
                if (line.charAt(level) != '\t') break;
                level++;
            }
            m.put(level, m.get(level - 1) + line.length() - level);
            if (line.contains("."))
                ans = Math.max(ans, m.get(level) + level);
        }

        return ans;
    }

    public static void main(String[] args) {
        var str = "dir\n\tsubdir1\n\tsubdir2\n\t\tfile.ext";
        var app = new Solution();
        var ans = app.lengthLongestPath1(str);

        System.out.println(ans);
    }
}
