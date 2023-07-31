package lee.pkg20230731;

import lee.pkg20210922.ListNode;

public class Solution {
    //    https://leetcode.cn/problems/reorder-list/
    public void reorderList(ListNode head) {
        if (head == null) return;
        ListNode l1 = head;
        ListNode middle = middleNode(head);
        ListNode l2 = middle.next;
        middle.next = null;
        l2 = reverseNode(l2);
        mergeNode(l1, l2);
    }

    // 3 6
    // 4 7
    public ListNode middleNode(ListNode head) {
        ListNode slow = head;
        ListNode fast = head;
        while (fast.next != null && fast.next.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        return slow;
    }

    public ListNode reverseNode(ListNode head) {
        ListNode real_head = new ListNode();
        while (head != null) {
            ListNode tmp = head.next;
            head.next = real_head.next;
            real_head.next = head;
            head = tmp;
        }
        return real_head.next;
    }

    public void mergeNode(ListNode l1, ListNode l2) {
        while (l1 != null && l2 != null) {
            ListNode l1_tmp = l1.next;
            ListNode l2_tmp = l2.next;

            l1.next = l2;
            l1 = l1_tmp;

            l2.next = l1;
            l2 = l2_tmp;
        }
    }
}
