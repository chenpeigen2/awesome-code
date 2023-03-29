package lee.pkg20210909;

import java.util.ArrayList;
import java.util.List;

public class Solution {

//    当前行是最后一行：单词左对齐，且单词之间应只有一个空格，在行末填充剩余空格；
//    当前行不是最后一行，且只有一个单词：该单词左对齐，在行末填充空格；
//    当前行不是最后一行，且不只一个单词：设当前行单词数为 \textit{numWords}numWords，空格数为 \textit{numSpaces}numSpaces，
//    我们需要将空格均匀分配在单词之间，则单词之间应至少有
//\textit{avgSpaces}=\Big\lfloor\dfrac{\textit{numSpaces}}{\textit{numWords}-1}\Big\rfloor
//    avgSpaces=⌊
//    numWords−1
//    numSpaces
//​
//        ⌋
//
//    个空格，对于多出来的
//\textit{extraSpaces}=\textit{numSpaces}\bmod(\textit{numWords}-1)
//    extraSpaces=numSpacesmod(numWords−1)
//
//    个空格，应填在前 \textit{extraSpaces}extraSpaces 个单词之间。因此，前 \textit{extraSpaces}extraSpaces
//    个单词之间填充 \textit{avgSpaces}+1avgSpaces+1 个空格，其余单词之间填充 \textit{avgSpaces}avgSpaces 个空格。


    public List<String> fullJustify(String[] words, int maxWidth) {
        List<String> ans = new ArrayList<>();
        int n = words.length;
        List<String> list = new ArrayList<>();
        for (int i = 0; i < n; ) {
            // list 装载当前行的所有 word
            list.clear();

            list.add(words[i]);
            int cur = words[i].length();

            for (i += 1; i < n; i++) {
                cur += 1 + words[i].length();
                if (cur <= maxWidth) {
                    list.add(words[i]);
                } else {
                    cur -= 1 + words[i].length();
                    break;
                }
            }
            // i 停到了 那个最新的那个数字

            // 当前行为最后一行，特殊处理为左对齐
            if (i == n) {
                StringBuilder sb = new StringBuilder();
                sb.append(list.get(0));
                for (int j = 1; j < list.size(); j++) {
                    sb.append(" ").append(list.get(j));
                }
                for (; sb.length() < maxWidth; ) {
                    sb.append(" ");
                }
                ans.add(new String(sb));
                break;
            }

            // 如果当前行只有一个 word，特殊处理为左对齐
            int cnt = list.size();
            if (cnt == 1) {
                StringBuilder sb = new StringBuilder();
                sb.append(list.get(0));
                for (; sb.length() < maxWidth; ) {
                    sb.append(" ");
                }
                ans.add(new String(sb));
                continue;
            }

            /**
             * 其余为一般情况
             * wordWidth : 当前行单词总长度;
             * spaceWidth : 当前行空格总长度;
             * avgSpace : 往下取整后的单位空格长度
             */
            int wordWidth = cur - (cnt - 1);
            int spaceWidth = maxWidth - wordWidth;
            int avgSpace = spaceWidth / (cnt - 1);
            StringBuilder spaceItem = new StringBuilder();

            for (int j = 0; j < avgSpace; j++) {
                spaceItem.append(" ");
            }

            int remains = spaceWidth - avgSpace * (cnt - 1);
            StringBuilder sb = new StringBuilder();
            sb.append(list.get(0));

            for (int j = 1; j < list.size(); j++) {
                if (remains > 0) {
                    remains--;
                    sb.append(" ");
                }
                sb.append(spaceItem).append(list.get(j));
            }
            ans.add(new String(sb));
        }
        return ans;
    }
}