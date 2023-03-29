package lee.pkg20210907;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    public int balancedStringSplit(String s) {
        char[] arr = s.toCharArray();
        int count = 0;
        int rCount = 0;
        int lCount = 0;

        for (char c : arr) {
            if (c == 'R') {
                rCount++;
            } else {
                lCount++;
            }

            if ((rCount != 0 && lCount != 0) && rCount == lCount) {
                count++;
                lCount = 0;
                rCount = 0;
            }
        }
        return count;
    }

    public static void main(String[] args) {
        var app = new Solution();
        var result = app.balancedStringSplit("RLRRRLLRLL");
        System.out.println(result);
    }

    public List<String> fullJustify(String[] words, int maxWidth) {
        List<String> ans = new ArrayList<>();
        int n = words.length;
        List<String> list = new ArrayList<>();

        for (int i = 0; i < n; ) {
            list.clear();
            list.add(words[i]);
            int sum = words[i].length();
            for (i += 1; i < n && sum <= maxWidth; i++) {
                sum = sum + 1 + words[i].length();
                if (sum <= maxWidth) {
                    list.add(words[i]);
                } else {
                    break;
                }
            }

            // 当前行为最后一行，特殊处理为左对齐
            if (i == n) {
                StringBuilder sb = new StringBuilder(list.get(0));
                for (int k = 1; k < list.size(); k++) {
                    sb.append(" ").append(list.get(k));
                }
                while (sb.length() < maxWidth) sb.append(" ");
                ans.add(sb.toString());
                break;
            }

            // 如果当前行只有一个 word，特殊处理为左对齐
            int cnt = list.size();
            if (cnt == 1) {
                String str = list.get(0);
                while (str.length() != maxWidth) str += " ";
                ans.add(str);
                continue;
            }

            /**
             * 其余为一般情况
             * wordWidth : 当前行单词总长度;
             * spaceWidth : 当前行空格总长度;
             * spaceItem : 往下取整后的单位空格长度
             */
            int wordWidth = sum - (cnt - 1);
            int spaceWidth = maxWidth - wordWidth;
            int spaceItemWidth = spaceWidth / (cnt - 1);
            String spaceItem = "";
            for (int k = 0; k < spaceItemWidth; k++) spaceItem += " ";
            StringBuilder sb = new StringBuilder();
            for (int k = 0, sum1 = 0; k < cnt; k++) {
                String item = list.get(k);
                sb.append(item);
                if (k == cnt - 1) break;
                sb.append(spaceItem);
                sum1 += spaceItemWidth;
                // 剩余的间隙数量（可填入空格的次数）
                int remain = cnt - k - 1 - 1;
                // 剩余间隙数量 * 最小单位空格长度 + 当前空格长度 < 单词总长度，则在当前间隙多补充一个空格
                if (remain * spaceItemWidth + sum1 < spaceWidth) {
                    sb.append(" ");
                    sum1++;
                }
            }
            ans.add(sb.toString());
        }
        return ans;

    }
}
