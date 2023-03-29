package lee.pkg20220504;

import java.util.ArrayDeque;
import java.util.Queue;

public class Solution {
    //    https://leetcode-cn.com/problems/find-the-winner-of-the-circular-game/
    // Summary of Queue methods
    // add remove element
    // offer poll peek

    // simulation
    public int findTheWinner(int n, int k) {
        Queue<Integer> queue = new ArrayDeque<>();
        for (int i = 1; i <= n; i++) {
            queue.offer(i);
        }
        while (queue.size() > 1) {
            for (int i = 1; i < k; i++) {
                queue.offer(queue.poll());
            }
            queue.poll();
        }
        return queue.peek();
    }

    // 0 1 2 3 %4 = 0 1 2 3
    // 1 2 3 4 %4 = 1 2 3 0
    public int findTheWinner1(int n, int k) {
        if (n <= 1) return 1;
        int ans = (findTheWinner1(n - 1, k) + k) % n;
        return ans == 0 ? n : ans;
    }


    public static void main(String[] args) {
        Queue<Integer> queue = new ArrayDeque<>();
        for (int i = 0; i < 10; i++) {
            queue.offer(i);
        }

        System.out.println(queue.peek());

        System.out.println(queue.poll());

        System.out.println(queue.remove());

        System.out.println(queue.peek());
    }
}
