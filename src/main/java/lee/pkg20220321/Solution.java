package lee.pkg20220321;

import java.util.*;

public class Solution {
    //    https://leetcode-cn.com/problems/two-sum-iv-input-is-a-bst/
    Set<Integer> s = new HashSet<>();

    public boolean findTarget(TreeNode root, int k) {
        if (root == null) return false;
        if (s.contains(k - root.val)) return true;
        s.add(root.val);
        return findTarget(root.left, k) || findTarget(root.right, k);
    }
}