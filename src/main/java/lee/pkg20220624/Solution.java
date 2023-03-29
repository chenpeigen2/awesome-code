package lee.pkg20220624;

import lee.adds.TreeNode;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class Solution {
    //    https://leetcode.cn/problems/find-largest-value-in-each-tree-row/
    public List<Integer> largestValues(TreeNode root) {
        List<Integer> ans = new ArrayList<>();
        if (root == null) return ans;
        Deque<TreeNode> q = new ArrayDeque<>();
        q.addLast(root);
        while (!q.isEmpty()) {
            int size = q.size();
            int max = q.peek().val;
            for (int i = 0; i < size; i++) {
                TreeNode node = q.removeFirst();
                max = Math.max(max, node.val);
                if (node.left != null) q.addLast(node.left);
                if (node.right != null) q.addLast(node.right);
            }
            ans.add(max);
        }
        return ans;
    }
}
