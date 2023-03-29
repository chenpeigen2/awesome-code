package lee.pkg20220827;

import lee.adds.TreeNode;

import java.util.HashMap;
import java.util.Map;

public class Solution {

//    https://leetcode.cn/problems/maximum-width-of-binary-tree/

    Map<Integer, Integer> m = new HashMap<>();

    int ans;


    public int widthOfBinaryTree(TreeNode root) {


        dfs(root, 1, 0);

        return ans;

    }

//    一个朴素的想法是：我们在 DFS过程中使用两个哈希表分别记录每层深度中的最小节点编号和最大节点编号，
//    两者距离即是当前层的宽度，最终所有层数中的最大宽度即是答案。


//    而实现上，我们可以利用先 DFS 左节点，再 DFS 右节点的性质可知，
//    每层的最左节点必然是最先被遍历到，因此我们只需要记录当前层最先被遍历到点编号
//    （即当前层最小节点编号），并在 DFS 过程中计算宽度，更新答案即可。

    private void dfs(TreeNode root, int u, int depth) {
        if (root == null) return;
        if (!m.containsKey(depth)) m.put(depth, u);
        ans = Math.max(ans, u - m.get(depth) + 1);
        dfs(root.left, u << 1, depth + 1);
        dfs(root.right, u << 1 | 1, depth + 1);
    }
}
