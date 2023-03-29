package lee.pkg20230206;

import lee.adds.TreeNode;

public class Solution {
    //    https://leetcode.cn/problems/evaluate-boolean-binary-tree/
    public boolean evaluateTree(TreeNode root) {
        if (root.val == 1) return true;
        if (root.val == 0) return false;
        boolean l = evaluateTree(root.left);
        boolean r = evaluateTree(root.right);
        if (root.val == 2) {
            return l | r;
        }
        if (root.val == 3) {
            return l & r;
        }
        return false;
    }
}
