package hot100;

import java.util.ArrayList;
import java.util.List;

public class 二叉树 {

    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode() {
        }

        TreeNode(int val) {
            this.val = val;
        }

        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }

    //    https://leetcode.cn/problems/binary-tree-inorder-traversal/?envType=study-plan-v2&envId=top-100-liked
    public List<Integer> inorderTraversal(TreeNode root) {
        List<Integer> ans = new ArrayList<>();
        middleOrder(root, ans);
        return ans;
    }

    void middleOrder(TreeNode node, List<Integer> ans) {
        if (node == null) return;
        middleOrder(node.left, ans);
        ans.add(node.val);
        middleOrder(node.right, ans);
    }

}
