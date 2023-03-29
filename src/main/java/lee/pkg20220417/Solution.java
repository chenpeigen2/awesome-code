package lee.pkg20220417;

import java.util.*;

public class Solution {

    //    https://leetcode-cn.com/problems/most-common-word/
    public String mostCommonWord(String paragraph, String[] banned) {
        String[] l = paragraph.split("[!?',;. ]+");
        Map<String, Integer> m = new HashMap<>();
        String ans = null;
        int cnt = 0;

        Set<String> s = new HashSet<>(Arrays.asList(banned));

        for (String word : l) {
            word = word.toLowerCase();
            if (s.contains(word)) continue;
            int c = m.getOrDefault(word, 0);
            if (c + 1 > cnt) {
                ans = word;
                cnt = c + 1;
            }
            m.put(word, c + 1);
        }
        return ans;
    }

    public String mostCommonWord1(String paragraph, String[] banned) {
        var frq = new HashMap<String, Integer>();
        var ban_ = new HashSet<String>();
        Arrays.stream(banned)
                .map(String::toLowerCase)
                .forEach(ban_::add);
        Arrays.stream(paragraph.split("[^a-zA-Z]+"))
                .map(String::toLowerCase)
                .filter(s -> !ban_.contains(s))
                .forEach(s -> frq.put(s, frq.getOrDefault(s, 0) + 1));
        var first = frq.entrySet().stream().max(Comparator.comparingInt(Map.Entry::getValue));
        return first.get().getKey();
    }

    public static void main(String[] args) {
        var app = new Solution();
        var ans = app.mostCommonWord("Bob. hIt, baLl", new String[]{"bob", "hit"});
        System.out.println(ans);
    }
}
