package lee.pkg20211224;

import java.util.Comparator;
import java.util.PriorityQueue;

public class Solution {
    public int eatenApples(int[] apples, int[] days) {
        PriorityQueue<int[]> q = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        int n = apples.length, time = 0, ans = 0;
        while (time < n || !q.isEmpty()) {
            // add the new apples
            if (time < n && apples[time] > 0) q.add(new int[]{time + days[time] - 1, apples[time]});
            // remove the rotten apples for prev
            while (!q.isEmpty() && q.peek()[0] < time) q.poll();

            if (!q.isEmpty()) {
                int[] cur = q.poll();
                if (--cur[1] > 0 && cur[0] > time) q.add(cur);
                ans++;
            }

            time++;
        }
        return ans;
    }
}
