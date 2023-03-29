package featuretest;

public class Trie {
    class Node {
        boolean isEnd;
        Node[] tns = new Node[26];
    }

    Node root = new Node();

    void add(String s) {
        Node p = root;
        for (int i = 0; i < s.length(); i++) {
            int u = s.charAt(i) - 'a';
            if (p.tns[u] == null) p.tns[u] = new Node();
            p = p.tns[u];
        }
        p.isEnd = true;
    }

    String query(String s) {
        Node p = root;
        for (int i = 0; i < s.length(); i++) {
            int u = s.charAt(i) - 'a';
            if (p.tns[u] == null) break;
            if (p.tns[u].isEnd) return s.substring(0, i + 1);
            p = p.tns[u];
        }
        return s;
    }
}
