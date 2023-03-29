package lee.pkg20220314;

import java.util.*;

public class Solution {

    //    https://leetcode-cn.com/problems/minimum-index-sum-of-two-lists/
    public String[] findRestaurant(String[] list1, String[] list2) {
        Map<String, Integer> index = new HashMap<>();

        for (int i = 0; i < list1.length; i++) {
            index.put(list1[i], i);
        }

        List<String> ret = new ArrayList<>();
        int indexSum = Integer.MAX_VALUE;

        for (int i = 0; i < list2.length; i++) {
            if (index.containsKey(list2[i])) {
                int j = index.get(list2[i]);
                if (i + j < indexSum) {
                    ret.clear();
                    ret.add(list2[i]);
                    indexSum = i + j;
                } else if (i + j == indexSum) {
                    ret.add(list2[i]);
                }
            }
        }
        return ret.toArray(new String[ret.size()]);
    }
}
