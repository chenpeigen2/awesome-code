package lee.pkg20211022;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Solution {
    public List<Integer> majorityElement(int[] nums) {
        int len = nums.length;
        int t = len / 3 + 1;
        List<Integer> l = new ArrayList<>();
        Map<Integer, Integer> m = new HashMap<>();
        for (int x : nums) {
            int cnt = m.getOrDefault(x, 0);
            if (cnt + 1 == t) {
                l.add(x);
            }
            m.put(x, cnt + 1);
        }
        return l;
    }

    public static void main(String[] args) {
        int[] arr = new int[]{1, 1, 1, 3, 3, 2, 2, 2};
        var app = new Solution();
        var list = app.majorityElement(arr);
        list.forEach(System.out::println);
    }
}
