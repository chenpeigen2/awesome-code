package 面试题目;

import java.util.*;

public class 二叉树 {

    public class TreeNode {
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

    // 二叉树的中序遍历，非递归实现
    public List<Integer> inorderTraversal(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) {
            return result;
        }

        Stack<TreeNode> stack = new Stack<>();
        TreeNode current = root;
        while (current != null || !stack.empty()) {
            while (current != null) {
                stack.push(current);
                current = current.left;
            }
            result.add(stack.peek().val);
            current = stack.pop().right;
        }

        return result;
    }

    void middleOrder(TreeNode node, List<Integer> ans) {
        if (node == null) return;
        middleOrder(node.left, ans);
        ans.add(node.val);
        middleOrder(node.right, ans);
    }

    void preorderTraversal(TreeNode node, List<Integer> result) {
        if (node == null) return;
        result.add(node.val);           // 先访问根节点
        preorderTraversal(node.left, result);   // 再遍历左子树
        preorderTraversal(node.right, result);  // 最后遍历右子树
    }

    /**
     * 二叉树的前序遍历，非递归实现
     * 前序遍历顺序为：根节点 -> 左子树 -> 右子树
     * 使用栈来模拟递归过程，先将根节点入栈，
     * 然后每次从栈中弹出一个节点，将其值加入结果列表，
     * 再依次将该节点的右子节点和左子节点入栈（注意入栈顺序）
     *
     * @param node   二叉树的根节点
     * @param result 存储前序遍历结果的列表
     */
    public void preOrder(TreeNode node, List<TreeNode> result) {
        // 如果根节点为空，直接返回
        if (node == null) {
            return;
        }

        // 创建一个栈用于存储待访问的节点
        Stack<TreeNode> stack = new Stack<>();
        // 将根节点入栈
        stack.push(node);

        // 当栈不为空时，继续遍历
        while (!stack.empty()) {
            // 弹出栈顶节点
            TreeNode val = stack.pop();
            // 将该节点添加到结果列表中
            result.add(val);

            // 先将右子节点入栈（如果存在），这样左子节点会在栈顶，优先被访问
            if (val.right != null) {
                stack.push(val.right);
            }
            // 再将左子节点入栈（如果存在）
            if (val.left != null) {
                stack.push(val.left);
            }
        }
    }

    // root -> left -> right
    // root -> right -> left
    // 双栈实现
    public List<Integer> postorderTraversal(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) return result;

        Stack<TreeNode> s1 = new Stack<>();
        Stack<TreeNode> s2 = new Stack<>();
        s1.push(root);

        while (!s1.isEmpty()) {

            // 核心的算法点
            TreeNode node = s1.pop();
            s2.push(node);
            // end

            if (node.left != null) {
                s1.push(node.left);
            }
            if (node.right != null) {
                s1.push(node.right);
            }
        }
        while (!s2.isEmpty()) {
            result.add(s2.pop().val);
        }
        return result;
    }

    void postorderTraversal(TreeNode node, List<Integer> result) {
        if (node == null) return;
        postorderTraversal(node.left, result);   // 先遍历左子树
        postorderTraversal(node.right, result);  // 再遍历右子树
        result.add(node.val);                    // 最后访问根节点
    }

    /**
     * 二叉树的最大深度
     *
     * @param root 二叉树的根节点
     * @return 二叉树的最大深度
     */
    // https://leetcode.cn/problems/maximum-depth-of-binary-tree/solutions/349250/er-cha-shu-de-zui-da-shen-du-by-leetcode-solution/?envType=study-plan-v2&envId=top-100-liked
    public int maxDepth(TreeNode root) {
        if (root == null) return 0;
        // 递归, 先求左右子树的最大深度
        int left = maxDepth(root.left);
        // 递归, 求根节点的深度
        int right = maxDepth(root.right);
        return Math.max(left, right) + 1;
    }

    // https://leetcode.cn/problems/invert-binary-tree/?envType=study-plan-v2&envId=top-100-liked

    /**
     * 翻转二叉树（镜像翻转）
     * 此方法将二叉树中的每个节点的左右子节点进行交换，实现整棵树的镜像翻转
     * 注意：当前实现中使用了inorderTraversal方法，这可能不是标准的翻转实现
     *
     * @param root 二叉树的根节点
     * @return 翻转后的二叉树的根节点
     */
    public TreeNode invertTree(TreeNode root) {
        // 如果当前节点为空，直接返回null
        if (root == null) return null;
        // 保存当前节点的左右子节点
        TreeNode left = root.left;
        TreeNode right = root.right;
        // 交换左右子节点
        root.left = right;
        root.right = left;
        // 对交换后的左右子树进行遍历操作（原实现中是中序遍历）
        if (root.left != null) {
            invertTree(root.left);
        }
        if (root.right != null) {
            invertTree(root.right);
        }
        // 返回翻转后的根节点
        return root;
    }


    /**
     * 判断二叉树是否对称（当前实现有误）
     * 注意：此方法的最后一步递归调用有问题，它只是分别检查左右子树是否对称，
     * 而不是检查左右子树是否互为镜像。
     *
     * @param root 二叉树的根节点
     * @return 如果二叉树对称则返回true，否则返回false
     */
    // https://leetcode.cn/problems/symmetric-tree/description/?envType=study-plan-v2&envId=top-100-liked
    public boolean isSymmetric(TreeNode root) {
        if (root == null) return true;
        return isMirror(root.left, root.right);
    }

    private boolean isMirror(TreeNode left, TreeNode right) {
        // 递归结束条件
        if (left == null && right == null) return true;
        if (left == null || right == null) return false;
        if (left.val != right.val) return false;
        // 递归结束条件end

        // 比较左子树的左子树与右子树的右子树，以及左子树的右子树与右子树的左子树
        return isMirror(left.left, right.right) && isMirror(left.right, right.left);
    }

    int ans = 1;

    public int depth(TreeNode root) {
        if (root == null) return 0;
        // 递归, 先求左右子树的最大深度
        int left = depth(root.left);
        // 递归, 求根节点的深度
        int right = depth(root.right);
        ans = Math.max(ans, left + right + 1);
        return Math.max(left, right) + 1;
    }

    public int diameterOfBinaryTree(TreeNode root) {
        depth(root);
        return ans - 1; // core
    }

    /**
     * 二叉树的层序遍历（广度优先搜索）
     * 按照从上到下、从左到右的顺序，逐层访问二叉树的所有节点
     *
     * @param root 二叉树的根节点
     * @return 包含每一层节点值的二维列表，每层节点值按从左到右的顺序排列
     */
    public List<List<Integer>> levelOrder(TreeNode root) {
        // 初始化结果列表，用于存储每一层的节点值
        List<List<Integer>> result = new ArrayList<>();
        // 使用队列来实现层序遍历
        Queue<TreeNode> queue = new ArrayDeque<>();
        if (root == null) return result;
        // 将根节点加入队列
        queue.offer(root);
        // 当队列不为空时，继续遍历
        while (!queue.isEmpty()) {
            // 创建当前层的结果列表
            List<Integer> level = new ArrayList<>();
            // 记录当前层的节点数量，确保只处理当前层的节点
            int size = queue.size();
            // 处理当前层的所有节点
            for (int i = 0; i < size; i++) {
                // 从队列中取出一个节点
                TreeNode node = queue.poll();
                // 将节点值加入当前层的结果列表
                level.add(node.val);
                // 将当前节点的左子节点加入队列（如果存在）
                if (node.left != null) {
                    queue.offer(node.left);
                }
                // 将当前节点的右子节点加入队列（如果存在）
                if (node.right != null) {
                    queue.offer(node.right);
                }
            }
            // 将当前层的结果加入总结果列表
            result.add(level);
        }

        return result;
    }

    /**
     * 将有序数组转换为高度平衡的二叉搜索树
     * 通过递归地选择数组中间元素作为根节点，确保左右子树高度差不超过1
     *
     * @param nums 有序数组
     * @return 构建的高度平衡二叉搜索树的根节点
     */
    public TreeNode sortedArrayToBST(int[] nums) {
        return buildTree(0, nums.length - 1, nums);
    }

    TreeNode buildTree(int l,int r, int[] nums) {
        if (l > r) return null;
        int mid = (l + r) / 2;
        TreeNode node = new TreeNode(nums[mid]);
        node.left = buildTree(l, mid - 1, nums);
        node.right = buildTree(mid + 1, r, nums);
        return node;
    }


    long pre = Long.MIN_VALUE;


    /**
     * 验证二叉树是否为有效的二叉搜索树(BST)
     * 使用中序遍历的思想，对于有效的BST，中序遍历序列应该是严格递增的
     *
     * @param root 二叉树的根节点
     * @return 如果是有效的二叉搜索树返回true，否则返回false
     */
    // https://leetcode.cn/problems/validate-binary-search-tree/?envType=study-plan-v2&envId=top-100-liked
    public boolean isValidBST(TreeNode root) {
        // 如果当前节点为空，则为有效BST
        if (root == null) return true;
        // 递归验证左子树是否为有效BST
        boolean left = isValidBST(root.left);
        // 检查当前节点值是否大于前一个访问的节点值，以确保中序遍历序列递增
        boolean isValid = root.val > pre;
        // 更新前一个访问的节点值
        pre = root.val;
        // 递归验证右子树是否为有效BST
        boolean right = isValidBST(root.right);
        // 只有当左子树、当前节点和右子树都满足条件时，才是有效的BST
        return left && isValid && right;
    }

}
