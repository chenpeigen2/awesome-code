package lee.pkg20220207;

import java.util.PriorityQueue;

public class Solution {
    public String longestDiverseString(int a, int b, int c) {
        StringBuilder sb = new StringBuilder();
        PriorityQueue<int[]> q = new PriorityQueue<>((x, y) -> y[1] - x[1]);
        if (a > 0) q.add(new int[]{0, a});
        if (b > 0) q.add(new int[]{1, b});
        if (c > 0) q.add(new int[]{2, c});
        while (!q.isEmpty()) {
            int[] tmp = q.poll();
            int n = sb.length();
            if (n >= 2 && sb.charAt(n - 1) - 'a' == tmp[0] && sb.charAt(n - 2) - 'a' == tmp[0]) {

                if (q.isEmpty()) break;
                int[] next = q.poll();
                sb.append((char) ('a' + next[0]));
                if (--next[1] != 0) q.add(next);
                q.add(tmp);

            } else {
                sb.append((char) ('a' + tmp[0]));
                if (--tmp[1] != 0) q.add(tmp);
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        var app = new Solution();
        var ans = app.longestDiverseString(7, 2, 1);
        System.out.println(ans);
    }
}
