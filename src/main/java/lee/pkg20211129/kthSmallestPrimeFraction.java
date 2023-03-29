package lee.pkg20211129;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class kthSmallestPrimeFraction {
    public int[] kthSmallestPrimeFraction(int[] arr, int k) {
        int n = arr.length;
        List<int[]> frac = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                frac.add(new int[]{arr[i], arr[j]});
            }
        }

        frac.sort((x, y) -> x[0] * y[1] - y[0] * x[1]);

        return frac.get(k - 1);
    }

    public int[] kthSmallestPrimeFraction1(int[] arr, int k) {
        int n = arr.length;
        PriorityQueue<int[]> q = new PriorityQueue<>((x, y) -> y[0] * x[1] - x[0] * y[1]);
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                double t = arr[i] * 1.0 / arr[j];
                if (q.size() < k || q.peek()[0] * 1.0 / q.peek()[1] > t) {
                    if (q.size() == k) q.poll();
                    q.add(new int[]{arr[i], arr[j]});
                }
            }
        }
        return q.poll();
    }

    public int[] kthSmallestPrimeFraction2(int[] arr, int k) {
        int n = arr.length;
        // ascend sequence
        PriorityQueue<int[]> pq = new PriorityQueue<>((x, y) -> arr[x[0]] * arr[y[1]] - arr[y[0]] * arr[x[1]]);

        for (int j = 1; j < n; ++j) {
            pq.offer(new int[]{0, j});
        }

        for (int i = 1; i < k; i++) {
            int[] frac = pq.poll();
            int x = frac[0];
            int y = frac[1];
            if (x + 1 < y) {
                pq.offer(new int[]{x + 1, y});
            }
        }
        return new int[]{arr[pq.peek()[0]], arr[pq.peek()[1]]};
    }
}
