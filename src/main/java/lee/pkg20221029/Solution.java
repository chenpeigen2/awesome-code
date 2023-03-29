package lee.pkg20221029;

import java.util.HashMap;
import java.util.List;

public class Solution {
    //    https://leetcode.cn/problems/count-items-matching-a-rule/
    public int countMatches(List<List<String>> items, String k, String v) {
        int ans = 0, idx = k.charAt(0) == 't' ? 0 : k.charAt(0) == 'c' ? 1 : 2;
        for (List<String> item : items) {
            if (item.get(idx).equals(v)) ans++;
        }
        return ans;


//        int index = new HashMap<String, Integer>() {{
//            put("type", 0);
//            put("color", 1);
//            put("name", 2);
//        }}.get(k);
//        int res = 0;
//        for (List<String> item : items) {
//            if (item.get(index).equals(v)) {
//                res++;
//            }
//        }
//        return res;
    }
}
