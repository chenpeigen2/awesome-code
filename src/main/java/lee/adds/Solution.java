package lee.adds;

import lee.pkg20210922.ListNode;
import org.openjdk.nashorn.api.tree.Tree;

import java.lang.annotation.Native;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Solution {
    //    https://leetcode-cn.com/problems/3sum/
    public List<List<Integer>> threeSum(int[] nums) {
        // -4 -1 -1 -1 0 1 2
        // -1 -1 2    -1 0 1
        List<List<Integer>> l = new ArrayList<>();
        int n = nums.length;
        if (n < 3) return l;
        Arrays.sort(nums);

        for (int i = 0; i < n; i++) {
            // 0 0 0
            if (nums[i] > 0) break;
            if (i > 0 && nums[i] == nums[i - 1]) continue;
            int j = i + 1, k = n - 1;
            while (j < k) {
                if (nums[i] + nums[j] + nums[k] == 0) {
                    l.add(Arrays.asList(nums[i], nums[j], nums[k]));
                    while (j < k && nums[j] == nums[j + 1]) j++;
                    while (j < k && nums[k] == nums[k - 1]) k--;
                    j++;
                    k--;
                } else if (nums[i] + nums[j] + nums[k] > 0) {
                    k--;
                } else {
                    j++;
                }
            }
        }
        return l;
    }

    //    https://leetcode-cn.com/problems/roman-to-integer/
//    s should be a valid roman integer
    //    若存在小的数字在大的数字的左边的情况，根据规则需要减去小的数字。对于这种情况，我们也可以将每个字符视作一个单独的值，若一个数字右侧的数字比它大，则将该数字的符号取反。
    public int romanToInt(String s) {
        char[] arr = s.toCharArray();
        int ans = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == 'I') {
                if (i + 1 < arr.length && arr[i + 1] == 'V') {
                    i++;
                    ans += 4;
                } else if (i + 1 < arr.length && arr[i + 1] == 'X') {
                    i++;
                    ans += 9;
                } else {
                    ans += 1;
                }
            } else if (arr[i] == 'X') {
                if (i + 1 < arr.length && arr[i + 1] == 'L') {
                    i++;
                    ans += 40;
                } else if (i + 1 < arr.length && arr[i + 1] == 'C') {
                    i++;
                    ans += 90;
                } else {
                    ans += 10;
                }
            } else if (arr[i] == 'C') {
                if (i + 1 < arr.length && arr[i + 1] == 'D') {
                    i++;
                    ans += 400;
                } else if (i + 1 < arr.length && arr[i + 1] == 'M') {
                    i++;
                    ans += 900;
                } else {
                    ans += 100;
                }
            } else if (arr[i] == 'V') {
                ans += 5;
            } else if (arr[i] == 'L') {
                ans += 50;
            } else if (arr[i] == 'D') {
                ans += 500;
            } else if (arr[i] == 'M') {
                ans += 1000;
            }

        }
        return ans;
    }

    // you need to treat n as an unsigned value
//    https://leetcode-cn.com/problems/number-of-1-bits/
    public int hammingWeight(int n) {
        /**
         * A constant holding the maximum value an {@code int} can
         * have, 2<sup>31</sup>-1.
         */
//        @Native public static final int   MAX_VALUE = 0x7fffffff;
//        Integer.MAX_VALUE;
        int ans = 0;
        while (n != 0) {
            n -= (n & -n);
            ans++;
        }
        return ans;
    }

    //    https://leetcode-cn.com/problems/shu-zu-zhong-de-ni-xu-dui-lcof/
    public int reversePairs(int[] nums) {
        int len = nums.length;
        if (len < 2) return 0;

        int[] copy = new int[len];

        for (int i = 0; i < len; i++) {
            copy[i] = nums[i];
        }
        int[] temp = new int[len];
        return reversePairs(copy, 0, len - 1, temp);
    }

    private int reversePairs(int[] nums, int left, int right, int[] temp) {
        if (left == right) return 0;
        int mid = left + (right - left) / 2;
        int leftPairs = reversePairs(nums, left, mid, temp);
        int rightPairs = reversePairs(nums, mid + 1, right, temp);
        int crossPairs = mergeAndCount(nums, left, mid, right, temp);

        if (nums[mid] <= nums[mid + 1]) {
            return leftPairs + rightPairs;
        }

        return leftPairs + rightPairs + crossPairs;
    }

    // nums[left...mid] nums[mid+1..right]
    private int mergeAndCount(int[] nums, int left, int mid, int right, int[] temp) {
        for (int i = left; i <= right; i++) {
            temp[i] = nums[i];
        }
        int i = left;
        int j = mid + 1;
        int count = 0;
        for (int k = left; k <= right; k++) {

            if (i == mid + 1) {
                nums[k] = temp[j];
                j++;
            } else if (j == right + 1) {
                nums[k] = temp[i];
                i++;
            } else if (temp[i] <= temp[j]) {
                nums[k] = temp[i];
                i++;
            } else {
                nums[k] = temp[j];
                j++;
                count += (mid - i + 1);
            }
        }
        return count;
    }

    //    https://leetcode-cn.com/problems/er-cha-shu-de-zui-jin-gong-gong-zu-xian-lcof/
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null || root == p || root == q) return root;
        TreeNode l = lowestCommonAncestor(root.left, p, q);
        TreeNode r = lowestCommonAncestor(root.right, p, q);
        if (l == null) return r;
        if (r == null) return l;
        return root;
    }

    //    https://leetcode.cn/problems/shu-de-zi-jie-gou-lcof/
    boolean res = false;

    public boolean isSubStructure(TreeNode A, TreeNode B) {
        if (A == B) return true;
        if (A == null || B == null) return false;
        dfs(A, B); // dfs 的时候 A B已经不是null
        return res;
    }

    public void dfs(TreeNode a, TreeNode b) {
        if (res) return;
        if (a == null) return;
        if (check(a, b)) res = true;
        dfs(a.left, b);
        dfs(a.right, b);
    }

    public boolean check(TreeNode a, TreeNode b) {
        if (b == null) return true;
        if (a == null) return false;
        if (a.val != b.val) return false;
        boolean b1 = check(a.left, b.left);
        boolean b2 = check(a.right, b.right);
        return b1 & b2;
    }

    //    https://leetcode.cn/problems/merge-two-sorted-lists/
    public ListNode mergeTwoLists(ListNode list1, ListNode list2) {
        ListNode root = new ListNode();
        ListNode node = root;
        while (list1 != null && list2 != null) {
            if (list1.val < list2.val) {
                node.next = list1;
                list1 = list1.next;
            } else {
                node.next = list2;
                list2 = list2.next;
            }
            node = node.next;
        }
        node.next = (list1 == null) ? list2 : list1;

        return root.next;
    }

    public ListNode mergeTwoLists1(ListNode list1, ListNode list2) {
        if (list1 == null) return list2;
        if (list2 == null) return list1;
        if (list1.val < list2.val) {
            list1.next = mergeTwoLists1(list1.next, list2);
            return list1;
        } else {
            list2.next = mergeTwoLists1(list1, list2.next);
            return list2;
        }
    }

    //    https://leetcode.cn/problems/same-tree/
    public boolean isSameTree(TreeNode p, TreeNode q) {
        if (p == null || q == null) {
            if (p == null && q == null) return true;
            return false;
        }
        if (p.val != q.val) return false;
        boolean l = isSameTree(p.left, q.left);
        boolean r = isSameTree(p.right, q.right);
        return l & r;
    }

    //    https://leetcode.cn/problems/single-number/
    public int singleNumber(int[] nums) {
        int ans = 0;
        for (int num : nums) {
            ans ^= num;
        }
        return ans;
    }

    //    https://leetcode.cn/problems/remove-duplicates-from-sorted-list/
    public ListNode deleteDuplicates(ListNode head) {
        ListNode node = head;
        while (node != null) {
            ListNode next = node.next;
            while (next != null && node.val == next.val) {
                next = next.next;
            }
            node.next = next;
            node = node.next;
        }
        return head;
    }

    List<Integer> l = new ArrayList<>();

    //    https://leetcode.cn/problems/binary-tree-preorder-traversal/
    public List<Integer> preorderTraversal(TreeNode root) {
        dfs(root);
        return l;
    }

    void dfs(TreeNode node) {
        if (node == null) return;
        l.add(node.val);
        dfs(node.left);
        dfs(node.right);
    }

    //    https://leetcode.cn/problems/reverse-linked-list/
    public ListNode reverseList(ListNode head) {
        ListNode result = new ListNode();
        ListNode cur = head;
        while (cur != null) {
            ListNode tmp = cur;
            cur = cur.next;
            tmp.next = result.next;
            result.next = tmp;
        }
        return result.next;
    }

    public static void main(String[] args) {
        var app = new Solution();
        int[] arr = new int[]{-1, 0, 1, 2, -1, -4};
//        var ans = app.threeSum(arr);
        var ans = app.romanToInt("IV");
        System.out.println(ans);
    }
}
