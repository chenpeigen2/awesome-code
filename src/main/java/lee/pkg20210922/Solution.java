package lee.pkg20210922;

import java.util.Arrays;

public class Solution {
    public ListNode[] splitListToParts(ListNode head, int k) {
        //数组中元素默认值为null
        ListNode[] result = new ListNode[k];

        int count = calculateCount(head);
        int partition = count / k;

        // very important thing
        ListNode tmp = head;

        if (partition == 0) {
            // that's a good thing
            for (int i = 0; i < count; i++) {
                result[i] = new ListNode(tmp.val, null);
                tmp = tmp.next;
            }
        } else {
            // remains
            int remains = count % k;
            for (int i = 0; i < k; i++) {
                ListNode tail = null;
                // 处理多余的数量
                if (remains > 0) {
                    if (result[i] == null) {
                        ListNode remain = new ListNode(tmp.val);
                        result[i] = tail = remain;
                        remains--;
                        tmp = tmp.next;
                    }
                }

                // 处理众生平等事件
                for (int j = 0; j < partition; j++) {
                    ListNode remain = new ListNode(tmp.val);
                    if (result[i] == null) {
                        result[i] = tail = remain;
                    } else {
                        tail.next = remain;
                        tail = remain;
                    }
                    tmp = tmp.next;
                }

            }
        }

        return result;
    }

    private int calculateCount(ListNode head) {
        int count = 0;
        while (head != null) {
            count++;
            head = head.next;
        }
        return count;
    }

    public static void main(String[] args) {
        int k = 3;
        ListNode h = null;
        ListNode tail = null;

        for (int i = 0; i < 10; i++) {

            ListNode tmp = new ListNode(i + 1);

            if (i == 0) {
                h = tail = tmp;
            } else {
                tail.next = tmp;
                tail = tmp;
            }
        }

//        while (h != null) {
//            System.out.println(h.val);
//            h = h.next;
//        }

        var app = new Solution();
        var result = app.splitListToParts(h, k);

        Arrays.stream(result).forEach(x -> {
            while (x != null) {
                System.out.println(x.val);
                x = x.next;
            }

            System.out.println("=============================");
        });
    }
}
