package lee.pkg20220503;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    //    https://leetcode-cn.com/problems/reorder-data-in-log-files/
    public String[] reorderLogFiles(String[] logs) {
        int n = logs.length;
        List<Log> list = new ArrayList<>();
        for (int i = 0; i < n; i++) list.add(new Log(logs[i], i)); // init
        list.sort((a, b) -> {
            if (a.type != b.type) return a.type - b.type; // type降序排序
            if (a.type == 1) return a.idx - b.idx; // idx生序排序
            // 字母日志 在内容不同时，忽略标识符后，按内容字母顺序排序；
            // 在内容相同时，按标识符排序。latter
            return !a.content.equals(b.content) ? a.content.compareTo(b.content) : a.sign.compareTo(b.sign);
        });
        String[] ans = new String[n];
        for (int i = 0; i < n; i++) ans[i] = list.get(i).ori;
        return ans;
    }

    static class Log {

        /**
         * type : 1 (digit) 0 (character)
         * idx : idx we are using
         */
        int type, idx;

        /**
         * ori : original content
         * sign : the sign we are using
         * content : the content real
         */
        String ori, sign, content;

        Log(String s, int _idx) {
            idx = _idx; // idx of the sight
            int n = s.length(), i = 0;
            while (i < n && s.charAt(i) != ' ') i++;
            sign = s.substring(0, i); // create the sign
            content = s.substring(i + 1);
            ori = s;
            type = Character.isDigit(content.charAt(0)) ? 1 : 0;
        }
    }
}
