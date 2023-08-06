package lee.pkg20230806;

import lee.pkg20210922.ListNode;

import java.util.List;

public class Solution {

//    https://leetcode.cn/problems/swap-nodes-in-pairs/

    public ListNode swapPairs1(ListNode head) {
        if (head == null || head.next == null) return head;
        ListNode next = head.next;
        head.next = swapPairs1(next.next);
        next.next = head;
        return next;
    }

    public ListNode swapPairs(ListNode head) {
        ListNode pre = new ListNode(0);

        pre.next = head;

        ListNode temp = pre;

        while (temp.next != null && temp.next.next != null) {
            ListNode start = temp.next;
            ListNode end = temp.next.next;

            temp.next = end;

            start.next = end.next;
            end.next = start;

            temp = start;

        }

        return pre.next;

    }
}
