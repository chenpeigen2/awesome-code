package lee.pkg20211026;

import java.util.*;

public class Solution {
    public int[] nextGreaterElement(int[] nums1, int[] nums2) {
        Map<Integer, Integer> s = new HashMap<>();
        for (int i = 0; i < nums1.length; i++) {
            s.put(nums1[i], i);
        }
        int[] arr = new int[nums1.length];
        for (int i = 0; i < nums2.length; i++) {
            if (s.containsKey(nums2[i])) {
                int j = i + 1;
                for (; j < nums2.length && nums2[j] < nums2[i]; j++) ;
                arr[s.get(nums2[i])] = j >= nums2.length ? -1 : nums2[j];
            }
        }
        return arr;
    }


//    Comparison of Stack and Deque methods
//    Stack Method	Equivalent Deque Method
//    push(e)	addFirst(e)
//    pop()	removeFirst()
//    peek()	getFirst()

    public int[] nextGreaterElement1(int[] nums1, int[] nums2) {
        int n = nums1.length, m = nums2.length;
        Deque<Integer> d = new ArrayDeque<>();
        Map<Integer, Integer> map = new HashMap<>();

        for (int i = m - 1; i >= 0; i--) {
            int x = nums2[i];
            while (!d.isEmpty() && d.peek() <= x) {
                d.pop();
            }
            map.put(x, d.isEmpty() ? -1 : d.peek());
            d.push(x);
        }
        int[] ans = new int[n];
        for (int i = 0; i < n; i++) {
            ans[i] = map.get(nums1[i]);
        }
        return ans;
    }

    public static void main(String[] args) {
        var app = new Solution();
        var result = app.nextGreaterElement(new int[]{4, 1, 2}, new int[]{1, 3, 4, 2});
        Arrays.stream(result).forEach(System.out::println);
    }
}
