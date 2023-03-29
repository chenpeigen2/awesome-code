package lee.pkg20220806;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    //    https://leetcode.cn/problems/string-matching-in-an-array/
    public List<String> stringMatching(String[] words) {
        List<String> ans = new ArrayList<>();
        int len = words.length;
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < len; j++) {
                if (i != j && words[j].contains(words[i])) {
                    ans.add(words[i]);
                    break;
                }
            }
        }
        return ans;
    }
}
