package hot100;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class 二叉树 {

    public static class TreeNode {
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

    //    https://leetcode.cn/problems/binary-tree-inorder-traversal/?envType=study-plan-v2&envId=top-100-liked
    public List<Integer> inorderTraversal(TreeNode root) {
        List<Integer> ans = new ArrayList<>();
        middleOrder(root, ans);
        return ans;
    }

    void middleOrder(TreeNode node, List<Integer> ans) {
        if (node == null) return;
        middleOrder(node.left, ans);
        ans.add(node.val);
        middleOrder(node.right, ans);
    }

    //    https://leetcode.cn/problems/invert-binary-tree/description/?envType=study-plan-v2&envId=top-100-liked
    public TreeNode invertTree(TreeNode root) {
        if (root == null) return null;

        TreeNode left = root.left;
        TreeNode right = root.right;
        root.left = right;
        root.right = left;
        invertTree(root.left);
        invertTree(root.right);
        return root;
    }

    //    https://leetcode.cn/problems/maximum-depth-of-binary-tree/?envType=study-plan-v2&envId=top-100-liked
    public int maxDepth(TreeNode root) {
        if (root == null) return 0;
        return Math.max(maxDepth(root.left), maxDepth(root.right)) + 1;
    }

    //    https://leetcode.cn/problems/symmetric-tree/description/?envType=study-plan-v2&envId=top-100-liked
    public boolean isSymmetric(TreeNode root) {
        if (root == null) return true;
        return dfsIsSymmetric(root.left, root.right);
    }

    private boolean dfsIsSymmetric(TreeNode left, TreeNode right) {
        if (left == null && right == null) return true;
        if (left == null || right == null) return false;
        if (left.val != right.val) return false;
        return dfsIsSymmetric(left.right, right.left) && dfsIsSymmetric(left.left, right.right);
    }

    //    https://leetcode.cn/problems/diameter-of-binary-tree/?envType=study-plan-v2&envId=top-100-liked

    int ans = 1;

    public int diameterOfBinaryTree(TreeNode root) {
        if (root == null) return 0;
        dfs(root);
        return ans - 1;
    }

    public int dfs(TreeNode root) {
        if (root == null) return 0; // 访问到空节点了，返回0
        int l = dfs(root.left); // 左儿子为根的子树的深度
        int r = dfs(root.right); // 右儿子为根的子树的深度
        ans = Math.max(ans, l + r + 1);
        return Math.max(l, r) + 1; // 返回该节点为根的子树的深度
    }

    //    https://leetcode.cn/problems/binary-tree-level-order-traversal/?envType=study-plan-v2&envId=top-100-liked
    public List<List<Integer>> levelOrder(TreeNode root) {
        Queue<TreeNode> queue = new ArrayDeque<>();
        List<List<Integer>> ans = new ArrayList<>();
        if (root == null) return ans;
        queue.offer(root);
        while (!queue.isEmpty()) {
            int len = queue.size();
            List<Integer> l = new ArrayList<>();
            for (int i = 0; i < len; i++) {
                TreeNode node = queue.poll();
                l.add(node.val);
                if (node.left != null) {
                    queue.offer(node.left);
                }
                if (node.right != null) {
                    queue.offer(node.right);
                }
            }
            ans.add(l);
        }
        return ans;
    }

    //    https://leetcode.cn/problems/convert-sorted-array-to-binary-search-tree/?envType=study-plan-v2&envId=top-100-liked
    public TreeNode sortedArrayToBST(int[] nums) {
        return buildTree(0, nums.length - 1, nums);
    }

    TreeNode buildTree(int l, int r, int[] nums) {
        if (l > r) return null;
        int mid = (l + r) / 2;
        TreeNode node = new TreeNode(nums[mid]);
        node.left = buildTree(l, mid - 1, nums);
        node.right = buildTree(mid + 1, r, nums);
        return node;
    }

    //    https://leetcode.cn/problems/validate-binary-search-tree/description/?envType=study-plan-v2&envId=top-100-liked
    long pre = Long.MIN_VALUE;

    public boolean isValidBST(TreeNode root) {
        if (root == null) return true;
        boolean left = isValidBST(root.left);
        boolean flag = root.val > pre;
        pre = root.val;
        boolean right = isValidBST(root.right);
        return left && flag && right;
    }

    int k;
    int result;

    //    https://leetcode.cn/problems/kth-smallest-element-in-a-bst/?envType=study-plan-v2&envId=top-100-liked
    public int kthSmallest(TreeNode root, int k) {
        this.k = k;
        middleOrder(root);
        return result;
    }

    void middleOrder(TreeNode n) {
        if (n == null) return;
        middleOrder(n.left);
        // get the root
        k--;
        if (k == 0) {
            result = n.val;
            return;
        }
        middleOrder(n.right);
    }

    //    https://leetcode.cn/problems/binary-tree-right-side-view/description/?envType=study-plan-v2&envId=top-100-liked
    public List<Integer> rightSideView(TreeNode root) {
        Queue<TreeNode> queue = new ArrayDeque<>();
        List<Integer> ret = new ArrayList<>();
        if (root == null) return ret;
        queue.offer(root);
        while (!queue.isEmpty()) {
            int sz = queue.size();
            double avg = 0;
            for (int i = 0; i < sz; i++) {

                TreeNode node = queue.poll();
                if (i == sz - 1) {
                    ret.add(node.val);
                }
                if (node.left != null) queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
            }
        }
        return ret;
    }

    //    https://leetcode.cn/problems/flatten-binary-tree-to-linked-list/?envType=study-plan-v2&envId=top-100-liked
    public void flatten(TreeNode root) {
        while (root != null) {
            if (root.left == null) root = root.right;
            else {
                TreeNode pre = root.left;
                while (pre.right != null) pre = pre.right;
                pre.right = root.right; // 把右子树 cp过来

                root.right = root.left; // 把之前左子树赋值到右子树
                root.left = null; // 左子树置为null
                root = root.right; // 查找下一个右子树
            }

        }
    }
}
