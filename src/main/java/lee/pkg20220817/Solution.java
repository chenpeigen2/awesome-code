package lee.pkg20220817;

import lee.adds.TreeNode;

import java.util.ArrayDeque;
import java.util.Queue;

public class Solution {
    //    https://leetcode.cn/problems/deepest-leaves-sum/
    public int deepestLeavesSum(TreeNode root) {
        Queue<TreeNode> q = new ArrayDeque<>();
        q.offer(root);
        int sum = 0;
        while (!q.isEmpty()) {
            int size = q.size();
            sum = 0;
            for (int i = 0; i < size; i++) {
                TreeNode n = q.poll();
                sum += n.val;
                if (n.left != null) q.offer(n.left);
                if (n.right != null) q.offer(n.right);
            }
        }
        return sum;
    }


    int res = 0;
    int maxLevel = 0;

    public int deepestLeavesSum1(TreeNode root) {
        dfs(root, 0);
        return res;
    }

    public void dfs(TreeNode root, int level) {
        if (root == null)
            return;
        if (level > maxLevel) {
            maxLevel = level;
            res = 0;
        }
        if (level == maxLevel) {
            res += root.val;
        }
        dfs(root.left, level + 1);
        dfs(root.right, level + 1);
    }
}
