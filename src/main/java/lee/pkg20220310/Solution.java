package lee.pkg20220310;

import java.util.ArrayList;
import java.util.List;

public class Solution {

    List<Integer> l = new ArrayList<>();

    public List<Integer> preorder(Node root) {
        preOrder(root);
        return l;
    }

    void preOrder(Node root) {
        if (root == null) return;
        l.add(root.val);
        root.children.forEach(this::preOrder);
    }
}
