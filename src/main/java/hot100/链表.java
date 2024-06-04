package hot100;

import java.util.HashSet;
import java.util.Set;

public class 链表 {

    public static class ListNode {
        int val;
        ListNode next;

        ListNode(int x) {
            val = x;
            next = null;
        }
    }

    //    https://leetcode.cn/problems/intersection-of-two-linked-lists/description/?envType=study-plan-v2&envId=top-100-liked
    public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
        ListNode node = headA;
        Set<ListNode> set = new HashSet<>();
        while (node != null) {
            set.add(node);
            node = node.next;
        }
        node = headB;
        while (node != null) {
            if (set.contains(node)) return node;
            node = node.next;
        }
        return null;
    }

    //    https://leetcode.cn/problems/reverse-linked-list/?envType=study-plan-v2&envId=top-100-liked
    public ListNode reverseList(ListNode head) {
        ListNode prefix = new ListNode(-1);
        ListNode node = head;
        while (node != null) {
            ListNode next = node.next;
            node.next = prefix.next;
            prefix.next = node;
            node = next;
        }
        return prefix.next;
    }

    //    https://leetcode.cn/problems/palindrome-linked-list/?envType=study-plan-v2&envId=top-100-liked
    public boolean isPalindrome(ListNode head) {
        StringBuilder sb = new StringBuilder();
        ListNode node = head;
        while (node != null) {
            sb.append(node.val);
            node = node.next;
        }
        return sb.toString().contentEquals(sb.reverse());
    }

}
