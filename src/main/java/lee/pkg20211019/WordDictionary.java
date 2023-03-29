package lee.pkg20211019;

public class WordDictionary {

    static class Node {
        boolean isWord;
        Node[] next = new Node[26];
    }

    Node root;

    public WordDictionary() {
        root = new Node();
    }

    public void addWord(String word) {
        Node tmp = root;
        for (int i = 0; i < word.length(); i++) {
            int result = word.charAt(i) - 'a';
            if (tmp.next[result] == null) {
                tmp.next[result] = new Node();
            }
            tmp = tmp.next[result];
        }
        tmp.isWord = true;
    }

    public boolean search(String word) {
        return dfs(word, root, 0);
    }

    boolean dfs(String s, Node p, int sIndex) {
        int n = s.length();
        // here the end
        if (n == sIndex) return p.isWord;

        // start from 0
        char c = s.charAt(sIndex);

        if (c == '.') {
            for (int i = 0; i < 26; i++) {
                // .at 失败了 就是对于刚才那个第一次搜索找出错误的道路就直接返回了，这个是错误的
                if (p.next[i] != null && dfs(s, p.next[i], sIndex + 1)) {
                    return true;
                }
            }
            return false;
        } else {
            int u = c - 'a';
            if (p.next[u] == null) return false;
            // if this way can go, then we can step up
            return dfs(s, p.next[u], sIndex + 1);
        }
    }


    //    ["WordDictionary","addWord","addWord","addWord","addWord","search","search","addWord","search","search","search","search","search","search"]
//            [[],["at"],["and"],["an"],["add"],["a"],[".at"],["bat"],[".at"],["an."],["a.d."],["b."],["a.d"],["."]]

    //    [null,null,null,null,null,false,false,null,true,true,false,false,true,false]
    public static void main(String[] args) {
        WordDictionary w = new WordDictionary();
        w.addWord("bat");
        System.out.println(w.search(".at"));
    }
}
