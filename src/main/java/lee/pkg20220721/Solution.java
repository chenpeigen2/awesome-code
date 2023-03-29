package lee.pkg20220721;

import lee.adds.TreeNode;

//https://leetcode.cn/problems/binary-tree-pruning/
public class Solution {
    public TreeNode pruneTree(TreeNode root) {
        if (dfs(root)) return null;
        return root;
    }

    public boolean dfs(TreeNode root) {
        if (root == null) return true;
        boolean l = dfs(root.left);
        boolean r = dfs(root.right);
        if (l) {
            root.left = null;
        }
        if (r) {
            root.right = null;
        }
        return l && r && root.val == 0;
    }


    public TreeNode pruneTree1(TreeNode root) {
        if (root == null) return null;
        root.left = pruneTree(root.left);
        root.right = pruneTree(root.right);
        if (root.left != null || root.right != null) return root;
        return root.val == 0 ? null : root;
    }
}
