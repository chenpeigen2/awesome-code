package 面试题目.day3;

import 面试题目.DoubleCheck;
import 面试题目.Important;

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
    // https://leetcode.cn/problems/binary-tree-inorder-traversal/description/?envType=study-plan-v2&envId=top-100-liked
    public List<Integer> inorderTraversal(TreeNode root) {
        List<Integer> ans = new ArrayList<>();

        Deque<TreeNode> stack = new ArrayDeque<>();
        TreeNode current = root;
        while (current != null || !stack.isEmpty()) {
            while (current != null) {
                stack.push(current);
                current = current.left;
            }
            ans.add(stack.peek().val);
            current = stack.pop().right;
        }

        return ans;
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
    // left -> right -> root
    // 双栈实现
    // 好像可以前序遍历之后 翻转过来就行
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
        invertTree(root.left);
        invertTree(root.right);
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
        if (left == null && right == null) return true; // core 关键的一步
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

    // https://leetcode.cn/problems/diameter-of-binary-tree/?envType=study-plan-v2&envId=top-100-liked
    public int diameterOfBinaryTree(TreeNode root) {
        depth(root);
        return ans - 1; // 我们计算的是多少个节点，结果返回的是几条边
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

    TreeNode buildTree(int l, int r, int[] nums) {
        if (l > r) return null; // 递归的退出条件不能忘记
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

    // 用于记录要找的第k小元素的索引
    int k;

    // 用于存储找到的第k小元素的值
    int result;

    /**
     * 找到二叉搜索树中第k小的元素
     * 利用二叉搜索树的中序遍历特性（左子树 < 根节点 < 右子树），
     * 中序遍历的结果是有序的，因此可以通过中序遍历来找到第k小的元素
     *
     * @param root 二叉搜索树的根节点
     * @param k    要查找的第k小元素（从1开始计数）
     * @return 第k小的元素值
     */
    public int kthSmallest(TreeNode root, int k) {
        this.k = k;
        middleOrder(root);
        return result;
    }

    /**
     * 中序遍历二叉搜索树，找到第k小的元素
     * 在中序遍历过程中，每访问一个节点就将k减1，
     * 当k减到0时，当前节点就是第k小的元素
     *
     * @param node 当前遍历的节点
     */
    void middleOrder(TreeNode node) {
        if (node == null) return;
        // 先遍历左子树
        middleOrder(node.left);
        // 访问当前节点，k减1
        k--;
        // 如果k等于0，说明找到了第k小的元素
        if (k == 0) {
            result = node.val;
            return;
        }
        // 继续遍历右子树
        middleOrder(node.right);
    }

    /**
     * 获取二叉树的右视图
     * 使用层序遍历的方式，每层只取最右边的节点值
     *
     * @param root 二叉树的根节点
     * @return 包含右视图节点值的列表，从上到下排列
     */
    public List<Integer> rightSideView(TreeNode root) {
        List<Integer> ans = new ArrayList<>();

        Queue<TreeNode> queue = new ArrayDeque<>();
        if (root == null) return ans;
        queue.offer(root);

        // 层序遍历二叉树
        while (!queue.isEmpty()) {
            int size = queue.size();
            // 遍历当前层的所有节点
            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                // 如果是当前层的最后一个节点，则加入结果列表
                if (i == size - 1) {
                    ans.add(node.val);
                }
                if (node.left != null) {
                    queue.offer(node.left);
                }
                if (node.right != null) {
                    queue.offer(node.right);
                }
            }
        }

        return ans;
    }


    /**
     * 将二叉树原地展开为单链表
     * 展开后的链表遵循先序遍历的顺序，每个节点的左子指针为null，右子指针指向下一个节点
     *
     * @param root 二叉树的根节点
     */
    @Important("Important")
    @DoubleCheck
    // https://leetcode.cn/problems/flatten-binary-tree-to-linked-list/?envType=study-plan-v2&envId=top-100-liked
    public void flatten(TreeNode root) {
        // 迭代处理每个节点，将其展开为右链表
        while (root != null) {
            // 如果当前节点没有左子树，直接移动到右子树
            if (root.left == null) {
                root = root.right;
            } else {
                // 找到左子树中最右边的节点
                TreeNode target = root.left;
                while (target.right != null) {
                    target = target.right;
                }
                // 将当前节点的右子树连接到左子树的最右节点
                target.right = root.right;
                // 将左子树移动到右子树的位置
                root.right = root.left;
                // 将左子树置为空
                root.left = null;
                // 移动到下一个节点
                root = root.right;
            }
        }
    }

    /**
     * 根据前序遍历和中序遍历构建二叉树
     *
     * @param preorder 前序遍历数组
     * @param inorder  中序遍历数组
     * @return 构建好的二叉树根节点
     */
    public TreeNode buildTree(int[] preorder, int[] inorder) {
        // 获取前序遍历数组长度
        int preLen = preorder.length;
        // 获取中序遍历数组长度
        int inLen = inorder.length;
        // 检查两个数组长度是否相等，不相等则抛出异常
        if (preLen != inLen) throw new RuntimeException("Incorrect input data");
        // 调用递归方法构建二叉树，传入完整的数组范围
        return buildTree(preorder, 0, preLen - 1, inorder, 0, inLen - 1);
    }

    /**
     * 递归构建二叉树的核心方法
     *
     * @param preorder 前序遍历数组
     * @param preStart 前序遍历当前处理的起始索引
     * @param preEnd   前序遍历当前处理的结束索引
     * @param inorder  中序遍历数组
     * @param inStart  中序遍历当前处理的起始索引
     * @param inEnd    中序遍历当前处理的结束索引
     * @return 当前子树的根节点
     */
    TreeNode buildTree(int[] preorder, int preStart, int preEnd, int[] inorder, int inStart, int inEnd) {
        // 递归终止条件：当前处理范围无效时返回null
        if (preStart > preEnd || inStart > inEnd) return null;

        // 前序遍历的第一个元素就是当前子树的根节点值
        int rootVal = preorder[preStart];
        // 创建根节点
        TreeNode node = new TreeNode(rootVal);

        // 在中序遍历中找到根节点的位置
        int pivot = inStart;
        while (inorder[pivot] != rootVal) pivot++;

        // instart = 2
        // 2 3 4
        // 4-2  = 2
        // left child has 2
        // 计算左子树的节点数量
        int leftNum = pivot - inStart;

        // 递归构建左子树
        // 前序遍历中左子树范围：preStart+1 到 preStart+leftNum
        // 中序遍历中左子树范围：inStart 到 pivot-1
        node.left = buildTree(preorder, preStart + 1, preStart + leftNum, inorder, inStart, pivot - 1);

        // 递归构建右子树
        // 前序遍历中右子树范围：preStart+leftNum+1 到 preEnd
        // 中序遍历中右子树范围：pivot+1 到 inEnd
        node.right = buildTree(preorder, preStart + leftNum + 1, preEnd, inorder, pivot + 1, inEnd);

        // 返回当前子树的根节点
        return node;
    }


    /**
     * 计算二叉树中路径和等于目标值的路径总数
     * 路径不需要从根节点开始，也不需要在叶子节点结束，但必须是向下的路径
     *
     * @param root      二叉树的根节点
     * @param targetSum 目标路径和
     * @return 路径和等于目标值的路径总数
     */
    // https://leetcode.cn/problems/path-sum-iii/submissions/697091192/?envType=study-plan-v2&envId=top-100-liked
    @Important
    public int pathSum(TreeNode root, int targetSum) {
        // 如果当前节点为空，返回0条路径
        if (root == null) {
            return 0;
        }

        // 递归计算：
        // 1. 以当前节点为起点的路径数量
        // 2. 以左子树任意节点为起点的路径数量
        // 3. 以右子树任意节点为起点的路径数量
        return rootSum(root, targetSum)
                + pathSum(root.left, targetSum)
                + pathSum(root.right, targetSum);
    }

    /**
     * 计算以指定节点为起点，向下延伸的路径中和等于目标值的路径数量
     *
     * @param root      当前节点
     * @param targetSum 剩余需要达到的目标和
     * @return 从当前节点开始向下延伸的符合条件的路径数量
     */
    public int rootSum(TreeNode root, long targetSum) {
        int ret = 0;

        // 如果当前节点为空，返回0条路径
        if (root == null) {
            return ret;
        }

        // 如果当前节点值等于剩余目标和，说明找到一条有效路径
        if (targetSum == root.val) {
            ret++;
        }

        // 递归检查左右子树，目标和减去当前节点值
        ret += rootSum(root.left, targetSum - root.val);
        ret += rootSum(root.right, targetSum - root.val);

        return ret;
    }

    /**
     * 寻找二叉树中两个指定节点的最近公共祖先
     * 最近公共祖先的定义：对于有根树 T 的两个节点 p、q，
     * 最近公共祖先表示为一个节点 x，满足 x 是 p、q 的祖先且 x 的深度尽可能大
     *
     * @param root 二叉树的根节点
     * @param p    目标节点p
     * @param q    目标节点q
     * @return p和q的最近公共祖先节点
     */
    @DoubleCheck
    // https://leetcode.cn/problems/lowest-common-ancestor-of-a-binary-tree/?envType=study-plan-v2&envId=top-100-liked
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        // 递归终止条件：
        // 1. 当前节点为空时返回null
        // 2. 当前节点就是p或q时，直接返回当前节点（找到了目标节点之一）
        if (root == null || root == p || root == q) return root;

        // 递归搜索左子树，寻找p和q
        TreeNode left = lowestCommonAncestor(root.left, p, q);
        // 递归搜索右子树，寻找p和q
        TreeNode right = lowestCommonAncestor(root.right, p, q);

        // 处理递归结果：
        // 如果左子树没有找到p或q，说明都在右子树中，返回右子树的结果
        if (left == null) {
            return right;
        }
        // 如果右子树没有找到p或q，说明都在左子树中，返回左子树的结果
        if (right == null) {
            return left;
        }
        // 如果左右子树都找到了p或q，说明当前节点就是最近公共祖先
        return root;
    }


    // 用于记录二叉树中的最大路径和
    int maxSum = Integer.MIN_VALUE;

    /**
     * 计算二叉树中的最大路径和
     * 路径被定义为一条节点序列，序列中每对相邻节点之间都存在一条边。
     * 同一个节点在一条路径序列中至多出现一次。
     * 该路径至少包含一个节点，且不一定经过根节点。
     *
     * @param root 二叉树的根节点
     * @return 二叉树中的最大路径和
     */
    @Important
    public int maxPathSum(TreeNode root) {
        // 调用辅助函数计算每个节点的最大贡献值，并更新全局最大路径和
        maxGain(root);
        // 返回计算得到的最大路径和
        return maxSum;
    }

    /**
     * 计算以当前节点为起点的最大路径和（贡献值）
     * 这个函数递归地计算每个节点能为父节点提供的最大贡献值
     *
     * @param node 当前处理的节点
     * @return 当前节点能为父节点提供的最大贡献值
     */
    public int maxGain(TreeNode node) {
        // 递归终止条件：如果当前节点为空，贡献值为0
        if (node == null) {
            return 0;
        }

        // 递归计算左子树的最大贡献值，如果为负数则取0（不选择该路径）
        int leftGain = Math.max(maxGain(node.left), 0);
        // 递归计算右子树的最大贡献值，如果为负数则取0（不选择该路径）
        int rightGain = Math.max(maxGain(node.right), 0);

        // 计算以当前节点为最高点的新路径和（可以同时包含左右子树）
        int priceNewPath = node.val + leftGain + rightGain;

        // 更新全局最大路径和
        maxSum = Math.max(maxSum, priceNewPath);

        // 返回当前节点能为父节点提供的最大贡献值（只能选择左右子树中的一条路径）
        return node.val + Math.max(leftGain, rightGain);
    }

}
