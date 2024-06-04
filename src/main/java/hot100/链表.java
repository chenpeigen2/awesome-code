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

        ListNode(int val, ListNode next) {
            this.val = val;
            this.next = next;
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

    //    https://leetcode.cn/problems/merge-two-sorted-lists/description/?envType=study-plan-v2&envId=top-100-liked
    public ListNode mergeTwoLists(ListNode list1, ListNode list2) {
        ListNode a = list1;
        ListNode b = list2;
        ListNode head = new ListNode(-1);
        ListNode cur = head;
        while (a != null || b != null) {
            if (a == null) {
                cur.next = b;
                break;
            }
            if (b == null) {
                cur.next = a;
                break;
            }
            if (a.val < b.val) {
                cur.next = a;
                a = a.next;
            } else {
                cur.next = b;
                b = b.next;
            }
            cur = cur.next;
        }
        return head.next;
    }

    public ListNode mergeTwoLists(ListNode list1, ListNode list2, int place) {
        if (list1 == null) return list2;
        else if (list2 == null) return list1;
        else if (list1.val < list2.val) {
            list1.next = mergeTwoLists(list1.next, list2);
            return list1;
        } else {
            list2.next = mergeTwoLists(list1, list2.next, place);
            return list2;
        }
    }

    //    https://leetcode.cn/problems/add-two-numbers/?envType=study-plan-v2&envId=top-100-liked
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode head = new ListNode(-1);
        ListNode cur = head;

        int left = 0;
        while (l1 != null || l2 != null || left != 0) {
            int v1 = 0;
            if (l1 != null) {
                v1 = l1.val;
                l1 = l1.next;
            }
            int v2 = 0;
            if (l2 != null) {
                v2 = l2.val;
                l2 = l2.next;
            }
            int value = v1 + v2 + left;
            int c = value % 10;
            left = value / 10;
            ListNode tmp = new ListNode(c);
            cur.next = tmp;
            cur = cur.next;
        }

        return head.next;
    }

    //    https://leetcode.cn/problems/remove-nth-node-from-end-of-list/?envType=study-plan-v2&envId=top-100-liked
    public ListNode removeNthFromEnd(ListNode head, int n) {
        // 这个还是需要创建一个 dummy 的application
        ListNode dummpy = new ListNode(0, head);
        ListNode slow = dummpy;
        ListNode fast = head;
        for (int i = 0; i < n; i++) {
            fast = fast.next;
        }
        while (fast != null) {
            slow = slow.next;
            fast = fast.next;
        }
        slow.next = slow.next.next;
        return dummpy.next;
    }

}
