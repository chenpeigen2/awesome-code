package lee.pkg20240402;

import hot100.TreeNode;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    //    https://leetcode.cn/problems/all-possible-full-binary-trees/?envType=daily-question&envId=2024-04-02
    public List<TreeNode> allPossibleFBT(int n) {
        List<TreeNode> ret = new ArrayList<>();
        List<TreeNode> tails = new ArrayList<>(); // record the tail
        TreeNode newNode = new TreeNode(0);
        ret.add(newNode);
        tails.add(newNode);
        for (int i = 3; i <= n; i += 2) {
            TreeNode left = new TreeNode(0);
            TreeNode right = new TreeNode(0);
            List<TreeNode> tmp = new ArrayList<>();
            List<TreeNode> tmpTails = new ArrayList<>();
            for (int k = 0; k < tails.size(); k++) {
                TreeNode tail = tails.get(k);

                tail.left = left;
                tail.right = right;

                TreeNode head = ret.get(k);
                TreeNode newHead = buildTree(head, tmpTails);
                tmp.add(newHead);

                tail.left = null;
                tail.right = null;
            }
            ret = tmp;
            tails = tmpTails;
        }

        return ret;
    }

    public TreeNode buildTree(TreeNode head, List<TreeNode> tails) {
        if (head == null) return null;
        TreeNode tmpHead = new TreeNode(head.val);
        tmpHead.left = buildTree(head.left, tails);
        tmpHead.left = buildTree(head.right, tails);
        if (tmpHead.left == null && tmpHead.right == null) {
            tails.add(tmpHead);
        }
        return tmpHead;
    }
}
