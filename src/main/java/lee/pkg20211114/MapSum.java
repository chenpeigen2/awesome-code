package lee.pkg20211114;

public class MapSum {
    private Node root;

    public MapSum() {
        this.root = new Node();
    }

    public void insert(String key, int val) {
        Node node = this.root;
        for (int i = 0; i < key.length(); i++) {
            int index = key.charAt(i) - 'a';
            if (node.children[index] == null) {
                node.children[index] = new Node();
            }
            node = node.children[index];
        }
        // the final char update the val ,
        node.val = val;
    }

    public int sum(String prefix) {
        Node node = this.root;
        for (int i = 0; i < prefix.length(); i++) {
            int index = prefix.charAt(i) - 'a';
            node = node.children[index];
            if (node == null) return 0;
        }
        return dfs(node);
    }

    private int dfs(Node node) {
        if (node == null) return 0;
        int ans = 0;
        if (node.val > 0) {
            ans = node.val;
        }
        for (int i = 0; i < 26; i++) {
            ans += dfs(node.children[i]);
        }
        return ans;
    }

    class Node {
        int val = 0;
        Node[] children = new Node[26];
    }

    public static void main(String[] args) {
        var app = new MapSum();
        app.insert("apple", 3);
        var r1 = app.sum("ap");
        System.out.println(r1);
        app.insert("app", 2);
        app.insert("apple", 2);
        var r2 = app.sum("ap");
        System.out.println(r2);
    }
}

/**
 * Your MapSum object will be instantiated and called as such:
 * MapSum obj = new MapSum();
 * obj.insert(key,val);
 * int param_2 = obj.sum(prefix);
 */
