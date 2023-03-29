package lee.pkg20220410;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class Solution {

    String[] word2s = new String[]{".-", "-...", "-.-.", "-..", ".", "..-.", "--.", "....", "..", ".---", "-.-", ".-..", "--", "-.", "---",
            ".--.", "--.-", ".-.", "...", "-", "..-", "...-", ".--", "-..-", "-.--", "--.."};

    //    https://leetcode-cn.com/problems/unique-morse-code-words/
    public int uniqueMorseRepresentations(String[] words) {
//        LinkedHashSet
        Set<String> s = new HashSet<>();
        for (String word : words) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < word.length(); i++) {
                sb.append(word2s[word.charAt(i) - 'a']);
            }
            s.add(sb.toString());
        }
        return s.size();
    }

    public static void main(String[] args) {
        var app = new Solution();
        String[] s = new String[]{
                "gin", "zen", "gig", "msg"
        };
        var ans = app.uniqueMorseRepresentations(s);
        System.out.println(ans);
//        System.out.println(app.words.length);
    }
}
