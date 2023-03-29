package lee.pkg20230203;

import lee.adds.TreeNode;

public class Solution {
//    https://leetcode.cn/problems/binary-tree-coloring-game/solution/mei-you-si-lu-yi-zhang-tu-miao-dong-pyth-btav/

    private int x, lsz, rsz;

    public boolean btreeGameWinningMove(TreeNode root, int n, int x) {
        this.x = x;
        dfs(root);
        return Math.max(Math.max(lsz, rsz), n - 1 - lsz - rsz) * 2 > n;
    }

    private int dfs(TreeNode node) {
        if (node == null) return 0;
        int ls = dfs(node.left);
        int rs = dfs(node.right);
        if (node.val == x) {
            lsz = ls;
            rsz = rs;
        }
        return ls + rs + 1;
    }
}
