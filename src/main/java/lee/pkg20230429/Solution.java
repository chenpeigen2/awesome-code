package lee.pkg20230429;

import java.util.*;

public class Solution {

    //    https://leetcode.cn/problems/remove-letter-to-equalize-frequency/
    public boolean equalFrequency(String word) {
        Map<Character, Integer> m = new HashMap<>();
        for (char ch : word.toCharArray()) {
            m.put(ch, m.getOrDefault(ch, 0) + 1);
        }
        List<Integer> l = new ArrayList<>();
        Set<Integer> set = new HashSet<>();
        for (var a : m.entrySet()) {
            set.add(a.getValue());
            l.add(a.getValue());
        }
        // 确保不会出现 a bb ccc
        if (set.size() > 2) return false;
        Collections.sort(l);
        int len = l.size();
        // aaa
        // a b c
        if (len == 1 || l.get(len - 1) == 1) return true;
        // a b cc
        if (l.get(len - 1) - l.get(len - 2) == 1) return true;
        // a bb cc dd ee
        if (l.get(len - 1) == l.get(1) && l.get(0) == 1) return true;
        return false;
    }

    public static void main(String[] args) {
        var app = new Solution();
        // abbcc
        var ans = app.equalFrequency("abbccddd");
        System.out.println(ans);
    }
}
