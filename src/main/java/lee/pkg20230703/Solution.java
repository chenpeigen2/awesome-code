package lee.pkg20230703;

import lee.pkg20210922.ListNode;

import java.util.ArrayDeque;
import java.util.Deque;

public class Solution {

    //    https://leetcode.cn/problems/add-two-numbers-ii/
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        Deque<Integer> stack1 = new ArrayDeque<>();
        Deque<Integer> stack2 = new ArrayDeque<>();

        while (l1 != null) {
            stack1.push(l1.val);
            l1 = l1.next;
        }
        while (l2 != null) {
            stack2.push(l2.val);
            l2 = l2.next;
        }
        int left = 0;
        ListNode ans = new ListNode();

        while (!stack1.isEmpty() || !stack2.isEmpty() || left > 0) {
            int v1 = stack1.isEmpty() ? 0 : stack1.pop();
            int v2 = stack2.isEmpty() ? 0 : stack2.pop();
            int v = v1 + v2 + left;
            int added = v % 10;
            left = v / 10;

            ListNode tmp = new ListNode();
            tmp.val = added;
            tmp.next = ans.next;

            ans.next = tmp;
        }


        return ans.next;

    }
}
