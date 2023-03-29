package lee.pkg20220713;

import java.util.ArrayDeque;
import java.util.Deque;

public class Solution {
    //    https://leetcode.cn/problems/asteroid-collision/
    public int[] asteroidCollision(int[] asteroids) {
        Deque<Integer> stack = new ArrayDeque<>();
        for (int asteroid : asteroids) {
            if (asteroid > 0) {
                stack.push(asteroid);
            } else {
                boolean alive = true;
                while (alive && !stack.isEmpty() && stack.peek() > 0) {
                    alive = stack.peek() < Math.abs(asteroid);
                    if (stack.peek() <= Math.abs(asteroid)) {
                        stack.pop();
                    }
                }
                if (alive) {
                    stack.push(asteroid);
                }
            }
        }
        int[] ans = new int[stack.size()];
        int i = 0;
        while (!stack.isEmpty()) {
            ans[i++] = stack.pollLast();
        }
        return ans;
    }
}
