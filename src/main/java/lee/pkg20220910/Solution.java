package lee.pkg20220910;

import lee.adds.TreeNode;

public class Solution {

    //    https://leetcode.cn/problems/trim-a-binary-search-tree/
    public TreeNode trimBST(TreeNode root, int low, int high) {
        if (root == null) return null;
        if (!shouldDelete(root, low, high)) {
            root.left = trimBST(root.left, low, high);
            root.right = trimBST(root.right, low, high);
        } else {
            // if root.val < low the left-child should be ignored , so need return the right node
            if (root.val < low) {
                return trimBST(root.right, low, high);
            }
            return trimBST(root.left, low, high);
        }
        return root;
    }

    public boolean shouldDelete(TreeNode node, int low, int high) {
        return node.val > high || node.val < low;
    }
}
