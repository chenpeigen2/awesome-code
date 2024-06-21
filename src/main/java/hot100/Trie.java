package hot100;

//https://leetcode.cn/problems/implement-trie-prefix-tree/?envType=study-plan-v2&envId=top-100-liked
public class Trie {

    Trie[] nodes;

    boolean isEnd;


    public Trie() {
        nodes = new Trie[26];
        isEnd = false;
    }

    public void insert(String word) {
        Trie node = this;
        for (char ch : word.toCharArray()) {
            int idx = ch - 'a';
            if (node.nodes[idx] == null) node.nodes[idx] = new Trie();
            node = node.nodes[idx];
        }
        node.isEnd = true;
    }

    public boolean search(String word) {
        Trie node = this;
        for (char ch : word.toCharArray()) {
            int idx = ch - 'a';
            if (node.nodes[idx] == null) return false;
            node = node.nodes[idx];
        }
        return node.isEnd;
    }

    public boolean startsWith(String prefix) {
        Trie node = this;
        for (char ch : prefix.toCharArray()) {
            int idx = ch - 'a';
            if (node.nodes[idx] == null) return false;
            node = node.nodes[idx];
        }
        return true;
    }
}
