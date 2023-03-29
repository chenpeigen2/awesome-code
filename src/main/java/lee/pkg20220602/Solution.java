
package lee.pkg20220602;

import lee.adds.TreeNode;

class Solution {
    //    https://leetcode.cn/problems/delete-node-in-a-bst/
    public TreeNode deleteNode(TreeNode root, int key) {
        if (root == null) return null;
        if (root.val == key) {
            if (root.left == null) return root.right;
            if (root.right == null) return root.left;
            TreeNode t = root.right;
            while (t.left != null) t = t.left;
            t.left = root.left;
            return root.right;
        } else if (root.val > key) {
            root.left = deleteNode(root.left, key);
        } else {
            root.right = deleteNode(root.right, key);
        }
        return root;
    }

//    public TreeNode deleteNode(TreeNode root, int key) {
//        if (root.val == key) return null;
//        dfs(root, key);
//        return root;
//    }

//    TreeNode node;
//
//    TreeNode pre;
//
//    void dfs(TreeNode root, int key) {
//        if (root == null) return;
//
//        if (root.val == key) {
//            node = root;
//            pre = node;
//            batch(root.right);
//        }
//
//        if (root.val > key) {
//            dfs(root.left, key);
//        } else {
//            dfs(root.right, key);
//        }
//    }
//
//    void batch(TreeNode root) {
//        if (root == null) return;
//
//        if (root.right != null) {
//            pre = root;
//            batch(root.right);
//        }
//        if (root.left != null) {
//            pre = root;
//            batch(root.left);
//        }
//        node.val = root.val;
//
//        if (pre.left == root) {
//            pre.left = null;
//        }
//        if (pre.right == root) {
//            pre.right = null;
//        }
//    }
}
