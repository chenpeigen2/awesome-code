package lee.pkg20220511;

import java.util.*;

public class Codec {
//    https://leetcode.cn/problems/serialize-and-deserialize-bst/

    // Encodes a tree to a single string.
    public String serialize(TreeNode root) {
        if (root == null) return "#";
        return new StringBuilder().append(root.val).append(",")
                .append(serialize(root.left)).append(",").
                append(serialize(root.right)).toString();
    }

    // Decodes your encoded data to tree.
    public TreeNode deserialize(String data) {
        return deserialize(new LinkedList<>(Arrays.asList(data.split(","))));
    }

    public TreeNode deserialize(List<String> list) {
        if (list.get(0).equals("#")) {
            list.remove(0);
            return null;
        }
        TreeNode n = new TreeNode(Integer.parseInt(list.get(0)));
        list.remove(0);
        n.left = deserialize(list);
        n.right = deserialize(list);
        return n;
    }
}
