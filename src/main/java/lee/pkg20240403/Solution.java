package lee.pkg20240403;

import hot100.TreeNode;

public class Solution {

    //    https://leetcode.cn/problems/find-a-corresponding-node-of-a-binary-tree-in-a-clone-of-that-tree/description/?envType=daily-question&envId=2024-04-03
    public final TreeNode getTargetCopy(final TreeNode original, final TreeNode cloned, final TreeNode target) {
        if (original == null || cloned == null) return null;
        if (original == target) {
            return cloned;
        }
        TreeNode leftTarget = getTargetCopy(original.left, cloned.left, target);
        if (leftTarget != null) return leftTarget;
        return getTargetCopy(original.right, cloned.right, target);
    }
}
