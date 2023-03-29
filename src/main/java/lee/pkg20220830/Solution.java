package lee.pkg20220830;

import lee.adds.TreeNode;

public class Solution {
    //    https://leetcode.cn/problems/maximum-binary-tree-ii/
    public TreeNode insertIntoMaxTree(TreeNode root, int val) {
        TreeNode n = new TreeNode(val);
        TreeNode prev = null, cur = root;
        while (cur != null && cur.val > val) {
            prev = cur;
            cur = cur.right;
        }
        if (prev == null) {
            // case 1
            n.left = cur;
            return n;
        } else {
            // case 3
            prev.right = n;
            n.left = cur;
            return root;
        }
    }
}
