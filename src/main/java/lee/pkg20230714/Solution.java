package lee.pkg20230714;

import lee.common.TreeNode;

public class Solution {
    //    https://leetcode.cn/problems/distribute-coins-in-binary-tree/

    int move = 0;

    public int distributeCoins(TreeNode root) {
        dfs(root);
        return move;
    }

    private int dfs(TreeNode root) {
        if (root == null) return 0;
        int leftExceed = dfs(root.left);
        int rightExceed = dfs(root.right);
        // 把左右节点都做到只有1个金币， 统计当前节点与其子节点之间的移动金币的次数， 子节点平衡需要执行多少次操作
        move += Math.abs(leftExceed) + Math.abs(rightExceed);
        // 返回其需要从父节点 拿走的金币数量 （可能为负数）
        return root.val - 1 + leftExceed + rightExceed;
    }
}
