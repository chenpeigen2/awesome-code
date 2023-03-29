package lee.pkg20220506;

import java.util.ArrayDeque;
import java.util.Queue;

//https://leetcode-cn.com/problems/number-of-recent-calls/
public class RecentCounter {
    Queue<Integer> q;

    public RecentCounter() {
        q = new ArrayDeque<>();
    }

    public int ping(int t) {
        while (!q.isEmpty() && t - q.peek() > 3000) q.poll();
        q.offer(t);
        return q.size();
    }

    public static void main(String[] args) {

    }
}
