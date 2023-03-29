package lee.pkg20220812;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Solution {
    public List<List<Integer>> groupThePeople(int[] groupSizes) {
        Map<Integer, List<Integer>> map = new HashMap<>();
        for (int i = 0; i < groupSizes.length; i++) {
            List<Integer> l = map.computeIfAbsent(groupSizes[i], integer -> new ArrayList<>());
            l.add(i);
        }
        List<List<Integer>> ans = new ArrayList<>();

        for (int k : map.keySet()) {
            List<Integer> s = null;
            List<Integer> l = map.get(k);
            for (int i = 0; i < l.size(); i++) {
                if (i % k == 0) {
                    s = new ArrayList<>();
                    ans.add(s);
                }
                s.add(l.get(i));
            }
        }

        return ans;
    }
}
