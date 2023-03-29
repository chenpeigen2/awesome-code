package lee.pkg20220805;

import lee.adds.TreeNode;

import java.util.ArrayDeque;
import java.util.Queue;

public class Solution {
    //    https://leetcode.cn/problems/add-one-row-to-tree/
    public TreeNode addOneRow(TreeNode root, int val, int depth) {
        if (depth == 1) {
            TreeNode ans = new TreeNode(val);
            ans.left = root;
            return ans;
        }
        Queue<TreeNode> d = new ArrayDeque<>();
        d.offer(root);
        int cur = 1;
        while (!d.isEmpty()) {
            int size = d.size();
            while (size-- > 0) {
                TreeNode node = d.poll();
                if (cur == depth - 1) {
                    TreeNode a = new TreeNode(val), b = new TreeNode(val);
                    a.left = node.left;
                    b.right = node.right;
                    node.left = a;
                    node.right = b;
                } else {
                    if (node.left != null) d.offer(node.left);
                    if (node.right != null) d.offer(node.right);
                }
            }
            cur++;
        }
        return root;
    }
}
