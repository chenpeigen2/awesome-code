package lee.pkg20220707;

import java.util.List;

//https://leetcode.cn/problems/replace-words/
public class Solution {
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

    public String replaceWords(List<String> dictionary, String sentence) {
        for (String s : dictionary) add(s);
        StringBuilder sb = new StringBuilder();
        for (String s : sentence.split(" ")) sb.append(query(s)).append(" ");
        return sb.substring(0, sb.length() - 1);
    }
}
