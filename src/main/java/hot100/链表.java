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

    //    https://leetcode.cn/problems/linked-list-cycle/?envType=study-plan-v2&envId=top-100-liked
    public boolean hasCycle(ListNode head) {
        if (head == null) return false;
        ListNode slow = head,
                fast = head.next;
        while (fast != null) {
            if (slow == fast) return true;
            fast = fast.next;
            if (fast != null) fast = fast.next;
            slow = slow.next;
        }
        return false;
    }

    //    https://leetcode.cn/problems/linked-list-cycle-ii/submissions/537107398/?envType=study-plan-v2&envId=top-100-liked
    public ListNode detectCycle(ListNode head) {
        if (head == null) return null;
        ListNode slow = head,
                fast = head;
        while (fast != null) {
            slow = slow.next;
            if (fast.next != null) {
                fast = fast.next.next;
            } else {
                return null;
            }
            if (slow == fast) {
                ListNode ptr = head;
                while (ptr != slow) {
                    ptr = ptr.next;
                    slow = slow.next;
                }
                return ptr;
            }
        }
        return null;
    }

    public ListNode detectCycle(ListNode head, int pa) {
        ListNode pos = head;
        Set<ListNode> visited = new HashSet<ListNode>();
        while (pos != null) {
            if (visited.contains(pos)) {
                return pos;
            } else {
                visited.add(pos);
            }
            pos = pos.next;
        }
        return null;
    }

}
