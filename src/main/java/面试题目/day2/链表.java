package 面试题目.day2;

import 面试题目.DoubleCheck;

import java.util.*;

public class 链表 {

    public class ListNode {
        int val;
        ListNode next;

        ListNode(int x) {
            val = x;
            next = null;
        }
    }

    /**
     * 找到两个单链表的相交节点
     * <p>
     * 算法思路：双指针技巧
     * 1. 如果两个链表相交，那么从相交点开始，后续的所有节点都是公共的
     * 2. 使用两个指针分别遍历两个链表，当到达末尾时跳转到另一个链表的头部
     * 3. 这样两个指针走过的总距离相同，如果有交点则会在交点相遇
     * 4. 如果没有交点，则两个指针最终都会为null，循环结束
     * <p>
     * 时间复杂度：O(m + n)，其中m和n分别是两个链表的长度
     * 空间复杂度：O(1)
     */
    // https://leetcode.cn/problems/intersection-of-two-linked-lists/solutions/811625/xiang-jiao-lian-biao-by-leetcode-solutio-a8jn/?envType=study-plan-v2&envId=top-100-liked
    public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
        // 初始化两个指针，分别指向两个链表的头节点
        ListNode hA = headA;
        ListNode hB = headB;
        // 结果节点，初始化为null
        ListNode res = null;
        // 循环条件：只要有一个指针不为null就继续遍历
        while (hA != null || hB != null) {
            // 如果两个指针指向同一个节点，说明找到了相交节点
            if (hA == hB) {
                res = hA;
                break; // 找到相交节点，退出循环
            }
            // 移动指针：如果当前指针不为null，则移动到下一个节点；
            // 如果当前指针为null，则跳转到另一个链表的头部
            hA = (hA != null) ? hA.next : headB;
            hB = (hB != null) ? hB.next : headA;
        }
        // 返回相交节点，如果没有相交节点则返回null
        return res;
    }

    /**
     * 反转单链表
     * <p>
     * 算法思路：迭代法，使用虚拟头节点的方式逐个插入节点
     * 1. 创建一个虚拟头节点，用于简化链表操作
     * 2. 遍历原链表，将每个节点插入到虚拟头节点之后
     * 3. 这样实现了链表的反转效果
     * <p>
     * 时间复杂度：O(n)，其中n是链表的长度
     * 空间 complexity：O(1)
     */
    // https://leetcode.cn/problems/reverse-linked-list/?envType=study-plan-v2&envId=top-100-liked
    public ListNode reverseList(ListNode head) {
        // 使用虚拟头节点方法进行链表反转
        ListNode dummy = new ListNode(-1); // 创建虚拟头节点
        ListNode cur = head;               // 当前处理的节点
        while (cur != null) {
            ListNode next = cur.next;      // 保存下一个节点
            // 将当前节点插入到虚拟头节点之后（头插法）
            cur.next = dummy.next;         // 当前节点指向虚拟头节点原来的下一个节点
            dummy.next = cur;              // 虚拟头节点指向当前节点
            cur = next;                    // 移动到下一个待处理节点
        }
        return dummy.next;                 // 返回反转后的链表头节点
    }


    /**
     * 判断链表是否为回文链表
     * <p>
     * 算法思路：方法1 - 使用StringBuilder存储值，然后比较字符串与其反转
     * 1. 遍历链表，将所有节点值拼接成字符串
     * 2. 比较原字符串与反转后的字符串是否相等
     * <p>
     * 时间复杂度：O(n)，其中n是链表的长度
     * 空间复杂度：O(n)，需要额外的字符串存储空间
     */
    public boolean isPalindrome(ListNode head) {
        // 方法1: 使用StringBuilder存储值，然后比较字符串与其反转
        // 时间复杂度: O(n), 空间复杂度: O(n)
//        StringBuilder sb = new StringBuilder();
//        ListNode current = head;
//        while (current != null) {
//            sb.append(current.val);
//            current = current.next;
//        }
//        String original = sb.toString();
//        String reversed = sb.reverse().toString();
//        return original.equals(reversed);

        // 方法2: 更优解法 - 快慢指针找中点，反转后半部分，然后比较
        // 步骤 1: 使用快慢指针找到链表的中间节点
        // slow 每次走一步，fast 每次走两步
        // 当 fast 到达链表末尾时，slow 正好位于链表中点（或中点的前一个节点，取决于链表长度奇偶）
        ListNode slow = head;
        ListNode fast = head;
        while (fast != null && fast.next != null && fast.next.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        // 步骤 2: 反转链表的后半部分
        // slow.next 是后半部分的头节点，调用 reverseList 进行反转
        ListNode secondHalf = reverseList(slow.next);
        // 步骤 3: 比较前半部分和反转后的后半部分是否相同
        // c1 指向链表头部（前半部分开始），c2 指向反转后的后半部分头部
        ListNode c1 = head;
        ListNode c2 = secondHalf;
        while (c2 != null) {
            // 如果对应位置的值不相等，则不是回文链表
            if (c1.val != c2.val) {
                return false;
            }
            // 移动两个指针继续比较下一个节点
            c1 = c1.next;
            c2 = c2.next;
        }
        // 步骤 4: 所有节点都比较完毕且相等，说明是回文链表
        return true;
    }


    /**
     * 检测链表中是否存在环
     * <p>
     * 算法思路：快慢指针法（Floyd判圈算法）
     * 1. 使用两个指针，慢指针每次移动一步，快指针每次移动两步
     * 2. 如果链表中存在环，快慢指针终将相遇
     * 3. 如果链表无环，快指针将先到达链表末尾
     * <p>
     * 时间复杂度：O(n)，其中n是链表的长度
     * 空间复杂度：O(1)
     */
    public boolean hasCycle(ListNode head) {
        // 初始化快慢指针
        ListNode slow = head;       // 慢指针，每次移动一步
        ListNode fast = head;  // 快指针，每次移动两步

        // 循环检测环的存在
        while (fast != null && fast.next != null) {

            // 移动指针：慢指针前进一步，快指针前进两步
            slow = slow.next;
            fast = fast.next.next;

            // 如果快慢指针相遇，说明存在环
            if (slow == fast) {
                return true;
            }
        }

        // 如果遍历完整个链表都没有相遇，说明无环
        return false;
    }

    /**
     * 检测链表中是否存在环，并返回环的入口节点
     * <p>
     * 算法思路：快慢指针法（Floyd判圈算法）
     * 1. 使用两个指针，慢指针每次移动一步，快指针每次移动两步
     * 2. 如果链表中存在环，快慢指针终将相遇
     * 3. 如果链表无环，快指针将先到达链表末尾
     * <p>
     * 时间复杂度：O(n)，其中n是链表的长度
     * 空间复杂度：O(1)
     */
// https://leetcode.cn/problems/linked-list-cycle-ii/?envType=study-plan-v2&envId=top-100-liked

    /**
     * 检测链表中是否存在环，并返回环的入口节点
     * <p>
     * 算法思路：快慢指针法（Floyd 判圈算法）
     * 1. 第一阶段：判断是否有环
     * - 使用两个指针，慢指针 (slow) 每次移动一步，快指针 (fast) 每次移动两步
     * - 如果链表中存在环，快慢指针终将相遇；如果无环，快指针将先到达链表末尾 (null)
     * 2. 第二阶段：寻找环的入口
     * - 当快慢指针第一次相遇时，说明存在环
     * - 此时将慢指针重新指向头节点 (head)，快指针保持在相遇点
     * - 两个指针都改为每次移动一步，继续向前遍历
     * - 当它们再次相遇时，相遇点即为环的入口节点
     * <p>
     * 数学原理简述：
     * 假设头节点到环入口的距离为 a，环入口到第一次相遇点的距离为 b，
     * 相遇点再回到环入口的距离为 c。
     * 慢指针走的距离：a + b
     * 快指针走的距离：a + b + n(b + c) （n 为快指针在环内多走的圈数）
     * 因为快指针速度是慢指针的 2 倍，所以：2(a + b) = a + b + n(b + c)
     * 化简得：a = (n - 1)(b + c) + c
     * 这意味着从头节点走 a 步，和从相遇点走 c 步（加上 n-1 圈），最终都会到达环入口。
     * <p>
     * 时间复杂度：O(n)，其中 n 是链表的长度
     * 空间复杂度：O(1)，只使用了常数级别的额外空间
     *
     * @param head 链表的头节点
     * @return 环的入口节点，如果不存在环则返回 null
     */
    public ListNode detectCycle(ListNode head) {
        // 初始化快慢指针，都指向头节点
        ListNode slow = head;
        ListNode fast = head;

        // 第一阶段：检测是否存在环
        // 循环条件：快指针及其下一个节点不为空，防止空指针异常
        while (fast != null && fast.next != null) {
            // 慢指针每次移动一步
            slow = slow.next;
            // 快指针每次移动两步
            fast = fast.next.next;

            // 如果快慢指针相遇，说明链表中存在环
            if (slow == fast) {
                // 第二阶段：寻找环的入口
                // 将慢指针重置为头节点
                slow = head;
                // 快指针保持在相遇点，两个指针现在都每次移动一步
                while (slow != fast) {
                    slow = slow.next;
                    fast = fast.next;
                }
                // 当两个指针再次相遇时，该节点即为环的入口
                return slow;
            }
        }

        // 如果遍历完整个链表都没有相遇，说明链表无环，返回 null
        return null;
    }

    /**
     * 合并两个有序链表
     * <p>
     * 算法思路：使用虚拟头节点和双指针技巧
     * 1. 创建一个虚拟头节点，用于简化链表合并操作
     * 2. 使用两个指针分别指向两个链表的当前节点
     * 3. 比较两个节点的值，将较小的节点接到结果链表后面
     * 4. 移动对应的指针继续比较，直到其中一个链表遍历完
     * 5. 将剩余的链表直接接到结果链表后面
     * <p>
     * 时间复杂度：O(m + n)，其中m和n分别是两个链表的长度
     * 空间复杂度：O(1)，只使用了常数级别的额外空间
     */
    public ListNode mergeTwoLists(ListNode list1, ListNode list2) {
        ListNode dummy = new ListNode(0); // 创建虚拟头节点，简化边界处理
        ListNode current = dummy;         // 当前节点指针，用于构建新链表

        // 同时遍历两个链表，比较节点值大小，将较小的节点接入结果链表
        while (list1 != null && list2 != null) {
            if (list1.val < list2.val) {
                current.next = list1;     // 将list1的当前节点接入结果链表
                list1 = list1.next;       // 移动list1指针到下一个节点
            } else {
                current.next = list2;     // 将list2的当前节点接入结果链表
                list2 = list2.next;       // 移动list2指针到下一个节点
            }
            current = current.next;       // 移动当前节点指针到下一个位置
        }

        // 处理剩余节点：将未遍历完的链表直接接入结果链表
        if (list1 != null) {
            current.next = list1;         // 如果list1还有剩余节点，全部接入
        } else {
            current.next = list2;         // 如果list2还有剩余节点，全部接入
        }

        return dummy.next;                // 返回合并后的链表头节点（跳过虚拟头节点）
    }

    /**
     * 两数相加
     * <p>
     * 算法思路：模拟加法运算过程，使用虚拟头节点构建结果链表
     * 1. 使用虚拟头节点简化链表构建操作
     * 2. 维护进位变量carry，初始为0
     * 3. 同时遍历两个链表，计算对应位置数字之和加上进位
     * 4. 将和的个位数作为新节点加入结果链表，十位数作为新的进位
     * 5. 继续直到两个链表都遍历完且无进位
     * <p>
     * 时间复杂度：O(max(m, n))，其中m和n分别是两个链表的长度
     * 空间复杂度：O(max(m, n))，用于存储结果链表
     */
// https://leetcode.cn/problems/add-two-numbers/?envType=study-plan-v2&envId=top-100-liked
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode dummy = new ListNode(-1); // 创建虚拟头节点，简化链表操作

        ListNode cur = dummy; // 当前节点指针，用于构建结果链表

        int carry = 0; // 进位值，初始为0

        // 循环条件：只要其中一个链表还有节点或者还有进位，就需要继续计算
        while (l1 != null || l2 != null || carry != 0) {
            int v1 = 0, v2 = 0; // 初始化当前位置的数值

            // 获取l1当前节点的值，并移动到下一个节点
            if (l1 != null) {
                v1 = l1.val;
                l1 = l1.next;
            }
            // 获取l2当前节点的值，并移动到下一个节点
            if (l2 != null) {
                v2 = l2.val;
                l2 = l2.next;
            }

            int sum = v1 + v2 + carry; // 计算当前位置的总和（包括进位）
            carry = sum / 10;          // 计算新的进位值
            sum %= 10;                 // 获取当前位的值（0-9）

            cur.next = new ListNode(sum); // 创建新节点存储当前位的值
            cur = cur.next;               // 移动当前指针到新节点
        }

        // 返回结果链表的头节点（跳过虚拟头节点）
        return dummy.next;
    }

    /**
     * 删除链表的倒数第n个节点
     * <p>
     * 算法思路：双指针技巧，使用虚拟头节点处理边界情况
     * 1. 创建虚拟头节点，指向原链表头节点，简化删除头节点的情况
     * 2. 使用快慢指针，快指针先向前移动n+1步，这样快慢指针之间间隔n个节点
     * 3. 然后快慢指针同时向后移动，直到快指针到达链表末尾
     * 4. 此时慢指针正好位于要删除节点的前一个节点
     * 5. 修改慢指针的next指针，跳过要删除的节点
     * <p>
     * 时间复杂度：O(L)，其中L是链表的长度，只需要遍历一次链表
     * 空间复杂度：O(1)，只使用了常数级别的额外空间
     */
    public ListNode removeNthFromEnd(ListNode head, int n) {
        ListNode dummy = new ListNode(0); // 创建虚拟头节点，简化边界处理
        dummy.next = head;                // 虚拟头节点指向原链表头节点

        ListNode slow = dummy;           // 慢指针，初始指向虚拟头节点
        ListNode fast = dummy;           // 快指针，初始指向虚拟头节点

        // 快指针先向前移动n+1步，确保快慢指针之间间隔n个节点
        for (int i = 0; i <= n; i++) {
            fast = fast.next;
        }

        // 快慢指针同时向后移动，直到快指针到达链表末尾
        while (fast != null) {
            slow = slow.next;  // 慢指针向前移动一步
            fast = fast.next;  // 快指针向前移动一步
        }

        // 此时slow指针指向要删除节点的前一个节点
        // 修改指针跳过要删除的节点
        slow.next = slow.next.next;

        return dummy.next; // 返回新的链表头节点（跳过虚拟头节点）
    }

    // https://leetcode.cn/problems/swap-nodes-in-pairs/submissions/696339023/?envType=study-plan-v2&envId=top-100-liked
    @DoubleCheck
    public ListNode swapPairs(ListNode head) {
        // 递归终止条件：如果链表为空或只有一个节点，无需交换，直接返回
        if (head == null || head.next == null) {
            return head;
        }

        // 获取第二个节点，它将成为交换后的新头节点
        ListNode next = head.next;

        // 递归处理从第三个节点开始的剩余链表，并将结果连接到第一个节点后面
        head.next = swapPairs(next.next); // **** 注意一下

        // 将第二个节点指向第一个节点，完成相邻两个节点的交换
        next.next = head;

        // 返回新的头节点（即原来的第二个节点）
        return next;
    }


    class Node {
        int val;
        Node next;
        Node random;

        public Node(int val) {
            this.val = val;
            this.next = null;
            this.random = null;
        }
    }


    /**
     * 复制带随机指针的链表
     * <p>
     * 算法思路：递归 + 哈希表缓存
     * 1. 使用哈希表记录原节点与新节点的映射关系，避免重复创建节点
     * 2. 递归处理每个节点，确保 next 和 random 指针都指向正确的新节点
     * 3. 递归终止条件：遇到空节点直接返回
     * <p>
     * 时间复杂度：O(n)，其中n是链表节点数量
     * 空间复杂度：O(n)，哈希表存储所有节点的映射关系
     */
    // 缓存映射：存储原节点到复制节点的映射关系，避免重复创建和处理循环引用
    private final Map<Node, Node> nodeCache = new HashMap<>();

    /**
     * 复制带随机指针的链表
     * <p>
     * 算法思路：递归 + 哈希表缓存（记忆化搜索）
     * 1. 使用哈希表 (nodeCache) 记录原节点与新创建的复制节点之间的映射关系。
     *    这有两个作用：
     *    a) 避免为同一个原节点创建多个复制节点。
     *    b) 处理 random 指针可能指向任意节点（包括尚未创建的节点或形成环）的情况。
     * 2. 递归终止条件：如果当前节点 head 为 null，直接返回 null。
     * 3. 检查缓存：如果当前节点已经在缓存中存在，说明已经创建过对应的复制节点，直接返回该节点。
     * 4. 创建新节点：如果缓存中不存在，则创建一个值相同的新节点 copyHead。
     * 5. 存入缓存：在递归处理 next 和 random 指针之前，先将 (head, copyHead) 放入缓存。
     *    这一步至关重要，可以防止因 random 指针指向自身或前方节点而导致的无限递归。
     * 6. 递归构建：
     *    - 递归调用 copyRandomList(head.next) 构建 next 指针指向的复制节点。
     *    - 递归调用 copyRandomList(head.random) 构建 random 指针指向的复制节点。
     * 7. 返回结果：返回当前创建的复制节点 copyHead。
     * <p>
     * 时间复杂度：O(n)，其中 n 是链表节点数量。每个节点只会被创建和访问一次。
     * 空间复杂度：O(n)，哈希表存储所有节点的映射关系，以及递归调用栈的深度最坏情况下为 O(n)。
     *
     * @param head 原链表的头节点
     * @return 复制后的新链表的头节点
     */
    public Node copyRandomList(Node head) {
        // 基本情况：如果节点为空，返回 null
        if (head == null) {
            return null;
        }

        Node copyHead;
        // 检查当前节点是否已经被复制过
        if (nodeCache.containsKey(head)) {
            // 如果已存在，直接从缓存中获取并返回
            copyHead = nodeCache.get(head);
        } else {
            // 如果不存在，创建一个新的复制节点
            copyHead = new Node(head.val);
            // 关键步骤：在递归调用之前先将映射关系存入缓存
            // 这样可以防止 random 指针形成环或指向前方节点时导致的无限递归
            nodeCache.put(head, copyHead);
            
            // 递归构建 next 指针指向的节点
            copyHead.next = copyRandomList(head.next);
            // 递归构建 random 指针指向的节点
            copyHead.random = copyRandomList(head.random);
        }
        
        // 返回当前节点对应的复制节点
        return copyHead;
    }

    /**
     * 对链表进行排序（使用辅助数组的方法）
     * <p>
     * 算法思路：
     * 1. 将链表中的所有节点放入一个列表中
     * 2. 对列表中的节点按照值进行排序
     * 3. 重新连接节点形成排序后的链表
     * <p>
     * 时间复杂度：O(n log n)，主要是排序的时间复杂度
     * 空间复杂度：O(n)，需要额外的列表存储节点
     *
     * @param head 链表的头节点
     * @return 排序后的链表头节点
     */
    public ListNode sortList(ListNode head) {
        // 边界条件检查：如果链表为空或只有一个节点，直接返回
        if (head == null || head.next == null) {
            return head;
        }
        // 创建列表存储所有节点
        List<ListNode> list = new ArrayList<>();
        // 遍历链表，将所有节点添加到列表中
        ListNode current = head;
        while (current != null) {
            list.add(current);
            current = current.next;
        }
        // 根据节点值对列表进行排序
        list.sort((a, b) -> Integer.compare(a.val, b.val));
        // 重新连接节点形成排序后的链表
        for (int i = 0; i < list.size() - 1; i++) {
            list.get(i).next = list.get(i + 1);
        }
        // 最后一个节点的next设为null，避免形成环
        list.get(list.size() - 1).next = null;
        // 返回排序后链表的头节点
        return list.get(0);
    }

// https://leetcode.cn/problems/merge-k-sorted-lists/submissions/696882938/?envType=study-plan-v2&envId=top-100-liked

    /**
     * 合并k个升序链表
     * <p>
     * 算法思路：分治法，逐一合并链表
     * 1. 使用一个结果链表ans初始化为null
     * 2. 遍历所有输入链表数组
     * 3. 将每个链表与当前结果链表进行合并（使用已实现的mergeTwoLists方法）
     * 4. 最终得到合并后的有序链表
     * <p>
     * 时间复杂度：O(N*k)，其中N是所有链表节点总数，k是链表个数
     * 空间复杂度：O(1)，只使用常数级别的额外空间
     *
     * @param lists 包含k个升序链表的数组
     * @return 合并后的升序链表头节点
     */
    public ListNode mergeKLists(ListNode[] lists) {
//        if (lists.length == 0) return null;
//        List<ListNode> list = new ArrayList<>();
//        for (ListNode listNode : lists) {
//            while (listNode != null) {
//                list.add(listNode);
//                listNode = listNode.next;
//            }
//        }
//        if (list.isEmpty()) return null;
//        list.sort((a, b) -> Integer.compare(a.val, b.val));
//        for (int i = 0; i < list.size() - 1; i++) {
//            list.get(i).next = list.get(i + 1);
//        }
//        list.get(list.size() - 1).next = null;
//        return list.get(0);

        // 初始化结果链表为null
        ListNode ans = null;

        // 遍历所有链表，逐一与当前结果链表合并
        for (ListNode list : lists) {
            ans = mergeTwoLists(ans, list);
        }

        // 返回合并后的链表
        return ans;
    }

    class LRUCache extends LinkedHashMap<Integer, Integer> {

        private int capacity;

        public LRUCache(int capacity) {
            this.capacity = capacity;
            super(capacity, 0.75f, true);
        }

        public int get(int key) {
            return super.getOrDefault(key, -1);
        }

        public void put(int key, int value) {
            super.put(key, value);
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
            return size() > capacity;
        }
    }

    class LRUCache2 {
        public @interface Important {

        }

        class DLinkedNode {
            int key;
            int value;
            DLinkedNode prev;
            DLinkedNode next;

            public DLinkedNode() {
            }

            public DLinkedNode(int key, int value) {
                this.key = key;
                this.value = value;
            }
        }

        private int capacity;
        private int size;
        private Map<Integer, DLinkedNode> cache = new HashMap<>();
        private DLinkedNode head, tail;

        public LRUCache2(int capacity) {
            this.capacity = capacity;
            size = 0;
            head = new DLinkedNode();
            tail = new DLinkedNode();
            head.next = tail;
            tail.prev = head;
        }

        public int get(int key) {
            DLinkedNode node = cache.get(key);
            if (node == null) {
                return -1;
            }
            moveToHead(node);
            return node.value;
        }

        public void put(int key, int value) {

            DLinkedNode node = cache.get(key);
            if (node == null) {
                DLinkedNode newNode = new DLinkedNode(key, value);
                cache.put(key, newNode);
                addToHead(newNode);
                ++size;
                if (size > capacity) {
                    DLinkedNode tail = removeTail();
                    cache.remove(tail.key);
                    --size;
                }
            } else {
                // 更新节点的值
                node.value = value;
                moveToHead(node);
            }
        }

        @Important
        private void addToHead(DLinkedNode node) {
            node.prev = head;
            node.next = head.next;
            head.next.prev = node;
            head.next = node;
        }

        @Important
        private void removeNode(DLinkedNode node) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }

        /**
         * 将节点移动到链表头部
         * <p>
         * 先删除节点，再添加到头部
         *
         * @param node 需要移动的节点
         */
        private void moveToHead(DLinkedNode node) {
            removeNode(node);
            addToHead(node);
        }


        /**
         * 移除链表尾部的节点
         * <p>
         * 先返回最新的tail节点，再调用删除方法
         *
         * @return 被移除的节点
         */
        private DLinkedNode removeTail() {
            DLinkedNode res = tail.prev;
            removeNode(res);
            return res;
        }

    }

    public static void main(String[] args) {
        链表 solution = new 链表();

        // 测试 getIntersectionNode
        System.out.println("=== 测试 getIntersectionNode ===");
        ListNode common = solution.new ListNode(8);
        common.next = solution.new ListNode(4);
        common.next.next = solution.new ListNode(5);

        ListNode headA = solution.new ListNode(4);
        headA.next = solution.new ListNode(1);
        headA.next.next = common;

        ListNode headB = solution.new ListNode(5);
        headB.next = solution.new ListNode(6);
        headB.next.next = solution.new ListNode(1);
        headB.next.next.next = common;

        ListNode intersection = solution.getIntersectionNode(headA, headB);
        System.out.println(intersection != null ? "相交节点值: " + intersection.val : "无相交节点");

        // 测试 reverseList
        System.out.println("\n=== 测试 reverseList ===");
        ListNode originalList = solution.new ListNode(1);
        originalList.next = solution.new ListNode(2);
        originalList.next.next = solution.new ListNode(3);
        originalList.next.next.next = solution.new ListNode(4);
        originalList.next.next.next.next = solution.new ListNode(5);

        ListNode reversedList = solution.reverseList(originalList);
        System.out.print("反转后的链表: ");
        printList(reversedList);

        // 测试 isPalindrome
        System.out.println("\n=== 测试 isPalindrome ===");
        ListNode palindromeList = solution.new ListNode(1);
        palindromeList.next = solution.new ListNode(2);
        palindromeList.next.next = solution.new ListNode(2);
        palindromeList.next.next.next = solution.new ListNode(1);

        System.out.println("是否为回文链表: " + solution.isPalindrome(palindromeList));

        // 测试 hasCycle
        System.out.println("\n=== 测试 hasCycle ===");
        ListNode cycleList = solution.new ListNode(3);
        ListNode node2 = solution.new ListNode(2);
        ListNode node0 = solution.new ListNode(0);
        ListNode node4 = solution.new ListNode(-4);
        cycleList.next = node2;
        node2.next = node0;
        node0.next = node4;
        node4.next = node2; // 形成环

        System.out.println("链表是否有环: " + solution.hasCycle(cycleList));

        // 测试 detectCycle
        System.out.println("\n=== 测试 detectCycle ===");
        ListNode cycleStart = solution.detectCycle(cycleList);
        System.out.println(cycleStart != null ? "环的入口节点值: " + cycleStart.val : "无环");

        // 测试 mergeTwoLists
        System.out.println("\n=== 测试 mergeTwoLists ===");
        ListNode list1 = solution.new ListNode(1);
        list1.next = solution.new ListNode(2);
        list1.next.next = solution.new ListNode(4);

        ListNode list2 = solution.new ListNode(1);
        list2.next = solution.new ListNode(3);
        list2.next.next = solution.new ListNode(4);

        ListNode mergedList = solution.mergeTwoLists(list1, list2);
        System.out.print("合并后的链表: ");
        printList(mergedList);

        // 测试 addTwoNumbers
        System.out.println("\n=== 测试 addTwoNumbers ===");
        ListNode num1 = solution.new ListNode(2);
        num1.next = solution.new ListNode(4);
        num1.next.next = solution.new ListNode(3);

        ListNode num2 = solution.new ListNode(5);
        num2.next = solution.new ListNode(6);
        num2.next.next = solution.new ListNode(4);

        ListNode sumResult = solution.addTwoNumbers(num1, num2);
        System.out.print("两数相加结果: ");
        printList(sumResult);

        // 测试 removeNthFromEnd
        System.out.println("\n=== 测试 removeNthFromEnd ===");
        ListNode toRemove = solution.new ListNode(1);
        toRemove.next = solution.new ListNode(2);
        toRemove.next.next = solution.new ListNode(3);
        toRemove.next.next.next = solution.new ListNode(4);
        toRemove.next.next.next.next = solution.new ListNode(5);

        ListNode removedList = solution.removeNthFromEnd(toRemove, 2);
        System.out.print("删除倒数第2个节点后: ");
        printList(removedList);

        // 测试 swapPairs
        System.out.println("\n=== 测试 swapPairs ===");
        ListNode pairList = solution.new ListNode(1);
        pairList.next = solution.new ListNode(2);
        pairList.next.next = solution.new ListNode(3);
        pairList.next.next.next = solution.new ListNode(4);

        ListNode swappedList = solution.swapPairs(pairList);
        System.out.print("交换相邻节点后: ");
        printList(swappedList);

        // 测试 copyRandomList
        System.out.println("\n=== 测试 copyRandomList ===");
        Node randomHead = solution.new Node(7);
        Node randomNode1 = solution.new Node(13);
        Node randomNode2 = solution.new Node(11);
        Node randomNode3 = solution.new Node(10);
        Node randomNode4 = solution.new Node(1);

        randomHead.next = randomNode1;
        randomNode1.next = randomNode2;
        randomNode2.next = randomNode3;
        randomNode3.next = randomNode4;

        randomHead.random = null;
        randomNode1.random = randomHead;
        randomNode2.random = randomNode4;
        randomNode3.random = randomNode2;
        randomNode4.random = randomHead;

        Node copiedRandomList = solution.copyRandomList(randomHead);
        System.out.println("复制带随机指针的链表成功");

        // 测试 sortList
        System.out.println("\n=== 测试 sortList ===");
        ListNode unsortedList = solution.new ListNode(4);
        unsortedList.next = solution.new ListNode(2);
        unsortedList.next.next = solution.new ListNode(1);
        unsortedList.next.next.next = solution.new ListNode(3);

        ListNode sortedList = solution.sortList(unsortedList);
        System.out.print("排序后的链表: ");
        printList(sortedList);

        // 测试 mergeKLists
        System.out.println("\n=== 测试 mergeKLists ===");
        ListNode klist1 = solution.new ListNode(1);
        klist1.next = solution.new ListNode(4);
        klist1.next.next = solution.new ListNode(5);

        ListNode klist2 = solution.new ListNode(1);
        klist2.next = solution.new ListNode(3);
        klist2.next.next = solution.new ListNode(4);

        ListNode klist3 = solution.new ListNode(2);
        klist3.next = solution.new ListNode(6);

        ListNode[] klists = {klist1, klist2, klist3};
        ListNode mergedKList = solution.mergeKLists(klists);
        System.out.print("合并k个有序链表: ");
        printList(mergedKList);
    }

    private static void printList(ListNode head) {
        ListNode current = head;
        while (current != null) {
            System.out.print(current.val);
            if (current.next != null) {
                System.out.print(" -> ");
            }
            current = current.next;
        }
        System.out.println();
    }

}
