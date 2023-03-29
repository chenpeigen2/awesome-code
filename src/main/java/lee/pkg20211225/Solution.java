package lee.pkg20211225;


import java.util.ArrayDeque;
import java.util.Deque;

class TreeNode {
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

public class Solution {
    public boolean isEvenOddTree(TreeNode root) {
        Deque<TreeNode> deque = new ArrayDeque<>();
        int level = 0;
        deque.addLast(root);
        while (!deque.isEmpty()) {
            int len = deque.size();
            int compare = level % 2 == 0 ? Integer.MIN_VALUE : Integer.MAX_VALUE;
            for (int i = 0; i < len; i++) {
                TreeNode treeNode = deque.removeFirst();
                int value = treeNode.val;
                // case01
                if (level % 2 == value % 2) {
                    return false;
                }
                // 严格递增，不可以等于
                if (!(level % 2 == 0 ? (value > compare) : (value < compare))) {
                    return false;
                }

                compare = value;
                if (treeNode.left != null) {
                    deque.addLast(treeNode.left);
                }
                if (treeNode.right != null) {
                    deque.addLast(treeNode.right);
                }
            }
            level++;
        }

        return true;
    }
}
