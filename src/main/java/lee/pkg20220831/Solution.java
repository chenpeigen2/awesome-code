package lee.pkg20220831;

import java.util.ArrayDeque;
import java.util.Deque;

public class Solution {
    //    https://leetcode.cn/problems/validate-stack-sequences/
    public boolean validateStackSequences(int[] pushed, int[] popped) {
        Deque<Integer> d = new ArrayDeque<>();
        for (int i = 0, j = 0; i < pushed.length; i++) {
            d.push(pushed[i]);
            while (!d.isEmpty() && d.peek() == popped[j]) {
                j++;
                d.pop();
            }
        }
        return d.isEmpty();
    }
}
