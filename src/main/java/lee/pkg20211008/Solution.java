package lee.pkg20211008;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Solution {
    public List<String> findRepeatedDnaSequences(String s) {
        Map<String, Integer> m = new HashMap<>();

        List<String> l = new ArrayList<>();

        int len = s.length();

        for (int i = 0; i + 10 <= len; i++) {
            String tmp = s.substring(i, i + 10);
            int cnt = m.getOrDefault(tmp, 0);
            if (cnt == 1) l.add(tmp);
            m.put(tmp, cnt + 1);
        }

        return l;
    }
}
