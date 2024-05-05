package hot100;

//https://leetcode.cn/problems/implement-trie-prefix-tree/submissions/529311022/?envType=study-plan-v2&envId=top-interview-150
public class Trie {

    Trie[] nodes;

    boolean isEnd;


    public Trie() {
        nodes = new Trie[26];
        isEnd = false;
    }

    public void insert(String word) {
        Trie node = this;
        for (int i = 0; i < word.length(); i++) {
            int idx = word.charAt(i) - 'a';
            if (node.nodes[idx] == null) node.nodes[idx] = new Trie();
            node = node.nodes[idx];
        }
        node.isEnd = true;
    }

    public boolean search(String word) {
        Trie node = this;
        for (int i = 0; i < word.length(); i++) {
            int idx = word.charAt(i) - 'a';
            if (node.nodes[idx] == null) return false;
            node = node.nodes[idx];
        }
        return node.isEnd;
    }

    public boolean startsWith(String prefix) {
        Trie node = this;
        for (int i = 0; i < prefix.length(); i++) {
            int idx = prefix.charAt(i) - 'a';
            if (node.nodes[idx] == null) return false;
            node = node.nodes[idx];
        }
        return true;
    }
}
