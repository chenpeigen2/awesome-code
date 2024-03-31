package lee.pkg20240331;

import java.util.ArrayDeque;
import java.util.Deque;

public class Solution {
    public boolean isValidSerialization(String preorder) {
        int n = preorder.length();
        int i = 0;
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(1);

        while (i < n) {
            if (stack.isEmpty()) return false;
            if (preorder.charAt(i) == ',') {
                i++;
            } else if (preorder.charAt(i) == '#') {
                int top = stack.pop() - 1;
                if (top > 0) {
                    stack.push(top);
                }
                i++;
            } else {
                while (i < n && preorder.charAt(i) != ',') {
                    i++;
                }
                int top = stack.pop() - 1;
                if (top > 0) {
                    stack.push(top);
                }
                stack.push(2);
            }
        }
        return stack.isEmpty();
    }

    public static void main(String[] args) {
        var preorder = "9,#,92,#,#";
        var solution = new Solution();
        solution.isValidSerialization(preorder);
    }
}
