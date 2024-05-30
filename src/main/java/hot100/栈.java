package hot100;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class 栈 {
    //    https://leetcode.cn/problems/valid-parentheses/?envType=study-plan-v2&envId=top-100-liked
    public boolean isValid(String s) {
        Deque<Character> stack = new ArrayDeque<>();
        for (char ch : s.toCharArray()) {
            if (ch == '{' || ch == '[' || ch == '(') {
                stack.push(ch);
            } else {
                if (stack.isEmpty()) return false;
                if (ch == ')' && stack.peek() != '(') return false;
                if (ch == '}' && stack.peek() != '{') return false;
                if (ch == ']' && stack.peek() != '[') return false;
                stack.pop();
            }
        }
        return stack.isEmpty();
    }

    //    https://leetcode.cn/problems/min-stack/?envType=study-plan-v2&envId=top-100-liked
    class MinStack {

        Deque<Integer> stack = new ArrayDeque<>();
        PriorityQueue<Integer> queue = new PriorityQueue<>();

        Deque<Integer> minStack;

        public MinStack() {
            minStack = new LinkedList<Integer>();
            minStack.push(Integer.MAX_VALUE);
        }

        public void push(int val) {
            stack.push(val);
            queue.offer(val);
            // 维护一个单调最小的数值
            minStack.push(Math.min(minStack.peek(), val));
        }

        public void pop() {
            int value = stack.pop();
            queue.remove(value);
            minStack.pop();
        }

        public int top() {
            return stack.peek();
        }

        public int getMin() {
//            return minStack.peek()
            return queue.peek();
        }
    }

}
