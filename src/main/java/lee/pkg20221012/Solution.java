package lee.pkg20221012;


import java.util.HashSet;
import java.util.Set;


class ListNode {
    int val;
    ListNode next;

    ListNode() {
    }

    ListNode(int val) {
        this.val = val;
    }

    ListNode(int val, ListNode next) {
        this.val = val;
        this.next = next;
    }
}

public class Solution {
    //    https://leetcode.cn/problems/linked-list-components/
    public int numComponents(ListNode head, int[] nums) {
        int ans = 0;

        Set<Integer> set = new HashSet<>();
        for (int x : nums) set.add(x);
        while (head != null) {
            if (set.contains(head.val)) {
                while (head != null && set.contains(head.val)) head = head.next;
                ans++;
            } else head = head.next;
        }

        return ans;
    }
}
