package lee.pkg20220116;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Solution {

//    由于链表长度只有 10^410
//            4
//            ，因此可以在初始化时遍历整条链表，将所有的链表值预处理到一个数组内。
//
//    在查询时随机一个下标，并将数组中对应下标内容返回出去。


    List<Integer> l = new ArrayList<>();

    Random r = new Random();

    public Solution(ListNode head) {
        while (head != null) {
            l.add(head.val);
            head = head.next;
        }
    }

    public int getRandom() {
        return l.get(r.nextInt(l.size()));
    }
}

//class Solution {
//    ListNode head;
//    Random random;
//
//    public Solution(ListNode head) {
//        this.head = head;
//        random = new Random();
//    }
//
//    public int getRandom() {
//        int i = 1, ans = 0;
//        for (ListNode node = head; node != null; node = node.next) {
//            if (random.nextInt(i) == 0) { // 1/i 的概率选中（替换为答案）
//                ans = node.val;
//            }
//            ++i;
//        }
//        return ans;
//    }
//}
