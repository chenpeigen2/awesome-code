package lee.pkg20230418;

import lee.adds.TreeNode;

public class Solution {

    //    https://leetcode.cn/problems/maximum-difference-between-node-and-ancestor/
    public int maxAncestorDiff(TreeNode root) {
        return dfs(root, root.val, root.val);
    }

    private int dfs(TreeNode root, int min, int max) {
        if (root == null) return 0;
        int diff = Math.max(Math.abs(root.val - min), Math.abs(root.val - max));
        min = Math.min(min, root.val);
        max = Math.max(max, root.val);

        return Math.max(diff, Math.max(dfs(root.left, min, max), dfs(root.right, min, max)));
    }
}
