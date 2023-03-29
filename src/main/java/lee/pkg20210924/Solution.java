package lee.pkg20210924;

public class Solution {


    Node h = null;
    Node tail = null;


    public Node flatten(Node head) {
        if (head == null) {
            return null;
        }

        backtrack(null, head);
        return h;
    }

    private void backtrack(Node pre, Node current) {
        if (current == null) {
            return;
        }

        if (h == null) {
            Node tmp = new Node();
            tmp.val = current.val;
            tmp.next = null;
            tmp.prev = null;
            tmp.child = null;

            h = tail = tmp;
        } else {
            Node tmp = new Node();
            tmp.val = current.val;
            tmp.prev = tail;
            tmp.next = null;
            tmp.child = null;

            tail.next = tmp;
            tail = tmp;
        }

        Node ppre = pre;

        if (current.child != null) {
            pre = current;
            current = current.child;

            backtrack(pre, current);

            current = pre;
            pre = ppre;
        }

        pre = current;
        current = current.next;
        backtrack(pre, current);
    }

    public static void main(String[] args) {
    }
}
