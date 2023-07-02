package lee.pkg20230702;

import lee.pkg20210922.ListNode;

public class Solution {
    //    https://leetcode.cn/problems/add-two-numbers/
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode root = new ListNode();
        root.next = null;

        ListNode tmp = root;

        int left = 0;

        while (l1 != null || l2 != null || left > 0) {
            int v1 = l1 != null ? l1.val : 0;
            int v2 = l2 != null ? l2.val : 0;
            int v = v1 + v2 + left;
            int added = v % 10;
            left = v / 10;

            ListNode next = new ListNode();
            next.next = null;
            next.val = added;

            tmp.next = next;
            tmp = next;

            if (l1 != null) l1 = l1.next;
            if (l2 != null) l2 = l2.next;

        }


        return root.next;
    }
}
