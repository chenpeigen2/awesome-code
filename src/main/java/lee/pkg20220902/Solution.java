package lee.pkg20220902;

import lee.adds.TreeNode;

public class Solution {
    //    https://leetcode.cn/problems/longest-univalue-path/
    int max = 0;

    public int longestUnivaluePath(TreeNode root) {
        dfs(root);
        return max;
    }

    public int dfs(TreeNode root) {
        if (root == null) return 0;
        int left = dfs(root.left);
        int right = dfs(root.right);
        int left1 = 0;
        int right1 = 0;
        if (root.left != null && root.left.val == root.val) left1 = left + 1;
        if (root.right != null && root.right.val == root.val) right1 = right + 1;
        max = Math.max(max, left1 + right1);
        // 如果是相加的话，线就是分叉了
        return Math.max(left1, right1);
    }
}
