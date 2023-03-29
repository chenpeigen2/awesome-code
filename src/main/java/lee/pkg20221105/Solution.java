package lee.pkg20221105;

import java.util.ArrayDeque;
import java.util.Deque;

public class Solution {
    //    https://leetcode.cn/problems/parsing-a-boolean-expression/description/
    public boolean parseBoolExpr(String expression) {
        Deque<Character> stack = new ArrayDeque<>();
        int n = expression.length();
        for (int i = 0; i < n; i++) {
            char c = expression.charAt(i);
            if (c == ',') continue;
            else if (c != ')') stack.push(c);
            else {
                int t = 0, f = 0;
                while (stack.peek() != '(') {
                    if (stack.pop() == 't') t++;
                    else f++;
                }
                stack.pop();
                char operation = stack.pop();
                switch (operation) {
                    case '!':
                        // 括号内只有一个
                        stack.push(f > 0 ? 't' : 'f');
                        break;
                    case '|':
                        stack.push(t > 0 ? 't' : 'f');
                        break;
                    case '&':
                        stack.push(f > 0 ? 'f' : 't');
                        break;
                    default:
                }
            }
        }
        return stack.pop() == 't';
    }
}
