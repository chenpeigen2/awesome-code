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

    public static void main(String[] args) {
        var preorder = "9,#,92,#,#";
        var solution = new Solution();
        solution.isValidSerialization(preorder);
    }
}
