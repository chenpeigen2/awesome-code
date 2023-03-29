package lee.pkg20220319;

public class Solution {

    StringBuilder sb = new StringBuilder();

    public String tree2str(TreeNode root) {
        preOrder(root);
        return sb.toString();
    }

    void preOrder(TreeNode node) {
        if (node == null) return;
        sb.append(node.val);
        if (node.left != null || node.right != null) {
            sb.append("(");
            preOrder(node.left);
            sb.append(")");
        }
        if (node.right != null) {
            sb.append("(");
            preOrder(node.right);
            sb.append(")");
        }
    }
}
