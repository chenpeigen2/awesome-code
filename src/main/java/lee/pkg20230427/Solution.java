package lee.pkg20230427;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Solution {

    //    https://leetcode.cn/problems/longest-string-chain/
    // https://leetcode.cn/problems/longest-string-chain/solution/zi-fu-chuan-geng-chang-shi-de-onmjie-fa-bjk1b/
    public int longestStrChain(String[] words) {
        Arrays.sort(words, (a, b) -> a.length() - b.length());
        Map<String, Integer> cnt = new HashMap<String, Integer>();
        int res = 0;
        for (String word : words) {
            cnt.put(word, 1);
            for (int j = 0; j < word.length(); j++) {
                String w = word.substring(0, j) + word.substring(j + 1);
                if (cnt.containsKey(w)) {
                    cnt.put(word, Math.max(cnt.get(word), cnt.get(w) + 1));
                }
            }
            res = Math.max(res, cnt.get(word));
        }
        return res;
    }
}
