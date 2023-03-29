package lee.pkg20211118;

public class Solution {

    int ans = 0;

    public int findTilt(TreeNode root) {
        dfs(root);
        return ans;
    }
    // 有头 且 有尾 这才是 带放回值 的递归的定义
    int dfs(TreeNode root) {
        if (root == null) return 0;
        int leftSum = dfs(root.left);
        int rightSum = dfs(root.right);
        ans += Math.abs(leftSum - rightSum);
        return leftSum + rightSum + root.val;
    }
}
