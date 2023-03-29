package lee.pkg20220308;

import java.util.Arrays;
import java.util.NavigableSet;
import java.util.TreeSet;

public class Solution {
    //    https://leetcode-cn.com/problems/plates-between-candles/
    public int[] platesBetweenCandles(String s, int[][] queries) {
        int len = queries.length;
        int[] ans = new int[len];
        char[] arr = s.toCharArray();
        for (int i = 0; i < len; i++) {
            int[] query = queries[i];
            int l = query[0];
            int r = query[1];
            while (arr[l] != '|') l++;
            while (arr[r] != '|') r--;
            for (int j = l; j <= r; j++) {
                if (arr[j] == '*') {
                    ans[i]++;
                }
            }

        }
        return ans;
    }

    public int[] platesBetweenCandles1(String s, int[][] queries) {
        char[] cs = s.toCharArray();
        int[] l = new int[cs.length];
        int[] r = new int[cs.length];
        int[] sum = new int[cs.length];
        int[] ans = new int[queries.length];
        for (int i = 0, f = 0; i < cs.length; i++) {
            if (cs[i] == '*') {
                f++;
            }
            sum[i] = f;
        }
        for (int i = 0, f = -1; i < cs.length; i++) {
            if (cs[i] == '|') {
                f = i;
            }
            l[i] = f;
        }

        for (int i = cs.length - 1, f = -1; i >= 0; i--) {
            if (cs[i] == '|') {
                f = i;
            }
            r[i] = f;
        }

        for (int i = 0; i < queries.length; i++) {
            int x = r[queries[i][0]];
            int y = l[queries[i][1]];
            ans[i] = (x != -1 && y != -1 && x < y) ? sum[y] - sum[x] : 0;
        }
        return ans;
    }

    public int[] platesBetweenCandles2(String s, int[][] queries) {
        NavigableSet<Integer> set = new TreeSet<>();
        int[] preSum = new int[s.length()];
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch == '|') {
                set.add(i);
                preSum[i] = (i > 0) ? preSum[i - 1] : 0;
            } else {
                preSum[i] = (i > 0) ? preSum[i - 1] + 1 : 1;
            }
        }
        int[] ans = new int[queries.length];
        for (int i = 0; i < queries.length; i++) {
            Integer left = set.ceiling(queries[i][0]);
            Integer right = set.floor(queries[i][1]);
            if (left != null && right != null && left < right) {
                ans[i] = preSum[right] - preSum[left];
            }
        }

        return ans;
    }

    public static void main(String[] args) {
        var str = "***|**|*****|**||**|*";
        var arr = new int[][]{{1, 17}, {4, 5}, {14, 17}, {5, 11}, {15, 16}};
        var app = new Solution();
        var ans = app.platesBetweenCandles(str, arr);
        Arrays.stream(ans).forEach(System.out::println);
    }
}
