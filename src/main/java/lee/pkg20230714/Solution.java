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
        int moveleft = 0;
        int moveright = 0;
        if (root == null) {
            return 0;
        }
        if (root.left != null) {
            moveleft = dfs(root.left);
        }
        if (root.right != null) {
            moveright = dfs(root.right);
        }
        // 把左右节点都做到只有1个金币
        // 统计当前节点与其子节点之间的移动金币的次数
        move += Math.abs(moveleft) + Math.abs(moveright);
        // 返回其父节点需要从node「拿走」的金币数目
        return moveleft + moveright + root.val - 1;
    }
}
