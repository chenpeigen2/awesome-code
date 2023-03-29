package lee.pkg20230201;

import java.util.HashMap;
import java.util.Map;

public class Solution {
    //    https://leetcode.cn/problems/decode-the-message/
    public String decodeMessage(String key, String message) {
        char[] chs = new char[26];

        char c = 'a';
        for (int i = 0; i < key.length(); i++) {
            char ch = key.charAt(i);
            if (ch == ' ') continue;
            if (chs[ch - 'a'] != 0) continue;
            chs[ch - 'a'] = c++;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < message.length(); i++) {
            char ch = message.charAt(i);
            if (ch == ' ') sb.append(ch);
            else {
                sb.append(chs[ch - 'a']);
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        var app = new Solution();
        var ans = app.decodeMessage("the quick brown fox jumps over the lazy dog", "vkbs bs t suepuv");
        System.out.println(ans);
    }
}
