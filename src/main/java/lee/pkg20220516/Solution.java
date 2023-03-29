package lee.pkg20220516;

public class Solution {

//    https://leetcode.cn/problems/successor-lcci/

    /**
     * 二叉搜索树的性质：
     * <p>
     * 左子树都比当前节点的值小；
     * 右子树都比当前节点的值大。
     *
     * @param root
     * @param p
     * @return
     */

    public TreeNode inorderSuccessor(TreeNode root, TreeNode p) {
        if (root == null) return null;
        if (root.val <= p.val) return inorderSuccessor(root.right, p);
        TreeNode left = inorderSuccessor(root.left, p);
        return left == null ? root : left;
    }

    //    就是找出第一个比p值大的那个点
//    如果root.val比p.val大，那可能这个点就是找的那个点，先赋值，在进行遍历
//    如果root.val比p.val小于或等于在可能比它大的点在它的右子树上面
    public TreeNode inorderSuccessor1(TreeNode root, TreeNode p) {
        if (root == null) return null;
        TreeNode ans = null;
        while (root != null) {
            if (root.val > p.val) {
                ans = root;
                root = root.left;
            } else {
                root = root.right;
            }

        }
        return ans;
    }

    boolean flag = false;


    /**
     * bad attemp , use return-value error
     *
     * @param root
     * @param p
     * @return
     */
    public TreeNode inorderSuccessor2(TreeNode root, TreeNode p) {
        if (root == null) return null;
        TreeNode left = inorderSuccessor2(root.left, p);

        if (left == null && flag) {
            return root;
        }

        if (root.val == p.val) {
            flag = true;
            return inorderSuccessor2(root.right, p);
        } else {
            inorderSuccessor2(root.right, p);
        }
        return null;
    }

    public static void main(String[] args) {
        TreeNode n = new TreeNode(2);
        n.right = new TreeNode(3);

        var app = new Solution();

        var ans = app.inorderSuccessor(n, new TreeNode(2));

        System.out.println(ans.val);
    }
}
