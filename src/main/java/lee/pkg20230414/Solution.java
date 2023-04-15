
package lee.pkg20230414;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    //    https://leetcode.cn/problems/camelcase-matching/
    public List<Boolean> camelMatch(String[] queries, String pattern) {
        List<Boolean> res = new ArrayList<>();
        for (String query : queries) {
            boolean matched = true;
            int i = 0;

            for (char ch : query.toCharArray()) {
                if (i < pattern.length() && ch == pattern.charAt(i)) i++;
                else if (Character.isUpperCase(ch)) {
                    matched = false;
                    break;
                }
            }

            if (i < pattern.length()) matched = false;
            res.add(matched);

        }
        return res;
    }
}