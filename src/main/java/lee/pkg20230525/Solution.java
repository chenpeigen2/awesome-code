package lee.pkg20230525;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Solution {

    //    https://leetcode.cn/problems/odd-string-difference/
    public String oddString(String[] words) {
        var d = new HashMap<String, List<String>>();
        int len = words[0].length();
        for (String word : words) {
            char[] cs = new char[len - 1];
            for (int i = 1; i < len; i++) {
                cs[i - 1] = (char) (word.charAt(i) - word.charAt(i - 1));
            }
            d.compute(Arrays.toString(cs), (k, v) -> {
                if (v == null) {
                    v = new ArrayList<>();
                }
                v.add(word);
                return v;
            });
        }
        for (var ss : d.values()) {
            if (ss.size() == 1) {
                return ss.get(0);
            }
        }
        return "";
    }
}
