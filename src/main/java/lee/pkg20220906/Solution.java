package lee.pkg20220906;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Solution {
//    https://leetcode.cn/problems/count-unique-characters-of-all-substrings-of-a-given-string/solution/by-muse-77-v7cs/

    //    https://pic.leetcode-cn.com/1662435865-GjAPIS-1.png
    public int uniqueLetterString(String s) {
        Map<Character, List<Integer>> index = new HashMap<>();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!index.containsKey(c)) {
                index.put(c, new ArrayList<>());
                index.get(c).add(-1);
            }
            index.get(c).add(i);
        }

        int res = 0;
        for (var entry : index.entrySet()) {
            List<Integer> arr = entry.getValue();
            arr.add(s.length()); // ???

            // 2 -1
            for (int i = 1; i < arr.size() - 1; i++) {
                // 0 - (-1)  * (2 - 0)
                // 1 - (-1) * (3 - 1) == 2 * 2 = 4
                res += (arr.get(i) - arr.get(i - 1)) * (arr.get(i + 1) - arr.get(i));
            }

        }
        return res;
    }
}
