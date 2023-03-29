package lee.pkg20220317;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Solution {
    //    https://leetcode-cn.com/problems/longest-word-in-dictionary/
    public String longestWord(String[] words) {
        String ans = "";
        Set<String> set = new HashSet<>();
        Collections.addAll(set, words);
        for (String s : set) {
            int n = s.length(), m = ans.length();
            if (n < m) continue;
            if (n == m && s.compareTo(ans) > 0) continue;
            boolean ok = true;
            for (int i = 1; i <= n && ok; i++) {
                String sub = s.substring(0, i);
                if (!set.contains(sub)) ok = false;
            }
            if (ok) ans = s;
        }
        return ans;
    }


    public String longestWord1(String[] words) {
        Arrays.sort(words, (a, b) -> {
            if (a.length() != b.length()) {
                return a.length() - b.length();
            } else {
                return b.compareTo(a);
            }
        });
        String longest = "";
        Set<String> candidates = new HashSet<String>();
        candidates.add("");
        int n = words.length;
        for (int i = 0; i < n; i++) {
            String word = words[i];
            if (candidates.contains(word.substring(0, word.length() - 1))) {
                candidates.add(word);
                longest = word;
            }
        }
        return longest;
    }
}
