package lee.pkg20220530;

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

//https://leetcode.cn/problems/sum-of-root-to-leaf-binary-numbers/
public class Solution {

    int ans = 0;

    StringBuilder sb = new StringBuilder();

    public int sumRootToLeaf(TreeNode root) {
        dfs(root);
        return ans;
    }

    void dfs(TreeNode n) {
        sb.append(n.val);
        if (n.left == null && n.right == null) {
            ans += Integer.parseInt(sb.toString(), 2);
        }
        if (n.left != null) {
            dfs(n.left);
            sb.delete(sb.length() - 1, sb.length());
        }
        if (n.right != null) {
            dfs(n.right);
            sb.delete(sb.length() - 1, sb.length());
        }
    }

    void dfs1(TreeNode root, int cur) {
        int ncur = (cur << 1) + root.val;
        if (root.left != null) dfs1(root.left, ncur);
        if (root.right != null) dfs1(root.right, ncur);
        if (root.left == null && root.right == null) ans += ncur;
    }
}
