package lee.pkg20220507;

import java.util.*;

public class Solution {
    //    https://leetcode-cn.com/problems/minimum-genetic-mutation/

    static char[] items = new char[]{'A', 'C', 'G', 'T'};

    public int minMutation(String start, String end, String[] bank) {

        Set<String> s = new HashSet<>();
        Collections.addAll(s, bank);

        Queue<String> q = new ArrayDeque<>();
        Map<String, Integer> m = new HashMap<>();

        q.offer(start);
        m.put(start, 0);

        while (!q.isEmpty()) {
            String polled = q.poll();
            for (int i = 0; i < polled.length(); i++) {
                // 一个一个去试验 各种去变形
                for (char item : items) {
                    if (polled.charAt(i) == item) continue;
                    char[] tmp = polled.toCharArray();
                    tmp[i] = item;
                    String tmpStr = String.valueOf(tmp);
                    if (!s.contains(tmpStr)) continue; // 变形的东西不在bank中，忽略
                    if (m.containsKey(tmpStr)) continue; //变形的已经是变形的了，忽略
                    if (end.equals(tmpStr)) return m.get(polled) + 1; //变形的东西已经是成品了，return
                    q.offer(tmpStr); //更新 queue
                    m.put(tmpStr, m.get(polled) + 1);  // 更新map
                }
            }
        }
        return -1;
    }
}
