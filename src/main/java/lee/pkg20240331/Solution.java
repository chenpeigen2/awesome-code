package lee.pkg20240331;

import java.util.ArrayDeque;
import java.util.Deque;

public class Solution {
    //    https://leetcode.cn/problems/verify-preorder-serialization-of-a-binary-tree/description/?envType=daily-question&envId=2024-03-31
    public boolean isValidSerialization(String preorder) {
        int len = preorder.length();
        int i = 0;
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(1);
        while (i < len) {
            if (stack.isEmpty()) return false;
            if (preorder.charAt(i) == ',') {
                i++;
            } else if (preorder.charAt(i) == '#') {
                int pop = stack.pop() - 1;
                if (pop > 0) {
                    stack.push(pop);
                }
                i++;
            } else {
                while (i < len && preorder.charAt(i) != ',') {
                    i++;
                }
                int pop = stack.pop() - 1;
                if (pop > 0) {
                    stack.push(pop);
                }
                stack.push(2);
            }
        }
        return stack.isEmpty();
    }

    public boolean isValidSerialization1(String preorder) {
        String[] arr = preorder.split(",");
        Deque<String> stack = new ArrayDeque<>();
        for (int i = 0; i < arr.length; i++) {
            stack.push(arr[i]);
//            while len(stack) >= 3 and stack[-1] == stack[-2] == '#' and stack[-3] != '#':
//            stack.pop(), stack.pop(), stack.pop()
//            stack.append('#')
        }

        return stack.size() == 1 && "#".equals(stack.peek());
    }

    public static void main(String[] args) {
        var preorder = "9,#,92,#,#";
        var solution = new Solution();
        solution.isValidSerialization(preorder);
    }
}
