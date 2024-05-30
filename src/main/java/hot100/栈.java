package hot100;

import java.util.ArrayDeque;
import java.util.Deque;

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
}
