package lee.pkg20220524;

import lee.adds.TreeNode;

public class Solution {
//    https://leetcode.cn/problems/univalued-binary-tree/
    private TreeNode root;

    public boolean isUnivalTree(TreeNode root) {
        if (root == null) return true;
        if (this.root == null) this.root = root;
        if (root.val != this.root.val) return false;
        boolean l = isUnivalTree(root.left);
        boolean r = isUnivalTree(root.right);

        return l & r;
    }
}
