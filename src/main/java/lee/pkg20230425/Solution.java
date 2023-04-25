package lee.pkg20230425;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Solution {

    //    https://leetcode.cn/problems/sort-the-people/
    public String[] sortPeople(String[] names, int[] heights) {
        Map<Integer, Integer> idxMap = new HashMap<>();
        for (int i = 0; i < heights.length; i++) {
            idxMap.put(heights[i], i);
        }
        Arrays.sort(heights);
        String[] res = new String[names.length];
        for (int i = heights.length - 1, j = 0; i >= 0; i--, j++) {
            res[j] = names[idxMap.get(heights[i])];
        }
        return res;
    }

    public String[] sortPeople1(String[] names, int[] heights) {
        int n = names.length;
        Integer[] indices = new Integer[n];
        for (int i = 0; i < n; i++) {
            indices[i] = i;
        }
        Arrays.sort(indices, new Comparator<Integer>() {
            @Override
            public int compare(Integer a, Integer b) {
                return heights[b] - heights[a];
            }
        });
        String[] res = new String[n];
        for (int i = 0; i < n; i++) {
            res[i] = names[indices[i]];
        }
        return res;
    }
}
