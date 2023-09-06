package lee.pkg20230906;


import kotlin.Pair;
import lee.adds.TreeNode;

import java.util.ArrayDeque;
import java.util.Queue;

public class Solution {
    private TreeNode ans;
    private int maxDepth = -1; // 全局最大深度

    //    https://leetcode.cn/problems/lowest-common-ancestor-of-deepest-leaves/description/?envType=daily-question&envId=2023-09-06
    public TreeNode lcaDeepestLeaves(TreeNode root) {
        dfs(root, 0);
        return ans;
    }

    private int dfs(TreeNode node, int depth) {
        if (node == null) {
            maxDepth = Math.max(maxDepth, depth);
            return depth;
        }
        int leftMaxDepth = dfs(node.left, depth + 1);
        int rightMaxDepth = dfs(node.right, depth + 1); // 获取右子树最深叶节点的深度
        if (leftMaxDepth == rightMaxDepth && leftMaxDepth == maxDepth) {
            ans = node;
        }
        return Math.max(leftMaxDepth, rightMaxDepth);
    }
}
