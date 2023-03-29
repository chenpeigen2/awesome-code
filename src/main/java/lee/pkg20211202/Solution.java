package lee.pkg20211202;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Solution {
    public String[] findRelativeRanks(int[] score) {
        int len = score.length;
        Map<Integer, Integer> m = new HashMap<>();
        for (int i = 0; i < len; i++) {
            m.put(score[i], i);
        }
        Arrays.sort(score);
        String[] ans = new String[len];
        for (int i = len - 1; i >= 0; i--) {
            if (i == len - 1) {
                ans[m.get(score[i])] = "Gold Medal";
            } else if (i == len - 2) {
                ans[m.get(score[i])] = "Silver Medal";
            } else if (i == len - 3) {
                ans[m.get(score[i])] = "Bronze Medal";
            } else {
                ans[m.get(score[i])] = "" + (len - i);
            }
        }
        return ans;
    }


    String[] ss = new String[]{"Gold Medal", "Silver Medal", "Bronze Medal"};

    public String[] findRelativeRanks1(int[] score) {
        int n = score.length;
        String[] ans = new String[n];
        int[] clone = score.clone();
        Arrays.sort(clone);
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = n - 1; i >= 0; i--) map.put(clone[i], n - 1 - i);
        for (int i = 0; i < n; i++) {
            int rank = map.get(score[i]);
            ans[i] = rank < 3 ? ss[rank] : String.valueOf(rank + 1);
        }
        return ans;
    }

    public static void main(String[] args) {
        var app = new Solution();
        var result = app.findRelativeRanks(new int[]{10, 3, 8, 9, 4});
        for (String s : result) {
            System.out.println(s);
        }
    }
}
