package lee.pkg20221009;

import java.util.ArrayDeque;
import java.util.Deque;

public class Solution {
    //    https://leetcode.cn/problems/score-of-parentheses/
    public int scoreOfParentheses(String s) {
        Deque<Integer> d = new ArrayDeque<>();
        d.push(0);
        // ()()
        // (())
        // 0 0 0 -> 0 1 -> 0 2
        for (char c : s.toCharArray()) {
            if (c == '(') d.push(0);
            else {
                int cur = d.pop();
                d.push(d.pop() + Math.max(cur * 2, 1));
            }
        }
        return d.peek();
    }
}
