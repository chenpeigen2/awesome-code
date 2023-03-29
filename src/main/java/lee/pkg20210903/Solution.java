package lee.pkg20210903;

import java.util.Random;

public class Solution {
//    public int[] smallestK(int[] arr, int k) {
//        Arrays.sort(arr);
//
//        int[] result = new int[k];
//
//        for (int i = 0; i < k; i++) {
//            result[i] = arr[i];
//        }
//
//        return result;
//    }

//    public int[] smallestK(int[] arr, int k) {
//        PriorityQueue<Integer> q = new PriorityQueue<>();
//        Arrays.stream(arr).forEach(i -> {
//            q.offer(i);
//        });
//        int[] result = new int[k];
//        for (int i = 0; i < k; i++) {
//            result[i] = q.poll();
//        }
//        return result;
//    }

//    堆内元素为 kk 个：根据 arr[i]arr[i] 与堆顶元素的大小关系分情况讨论：
//    arr[i] >= heapToparr[i]>=heapTop：arr[i]arr[i] 不可能属于第 kk 小数（已有 kk 个元素在堆中），直接丢弃 arr[i]arr[i]；
//    arr[i] < heapToparr[i]<heapTop：arr[i]arr[i] 可能属于第 kk 小数，弹出堆顶元素，并放入 arr[i]arr[i]。


//    public int[] smallestK(int[] arr, int k) {
//        PriorityQueue<Integer> q = new PriorityQueue<>((a, b) -> b - a);
//        int[] ans = new int[k];
//        if (k == 0) return ans;
//        for (int i : arr) {
//            if (q.size() == k) {
//                int num = q.peek();
//                if (num <= i) {
//                    continue;
//                }
//                q.poll();
//            }
//            q.add(i);
//        }
//        for (int i = k - 1; i >= 0; i--) ans[i] = q.poll();
//        return ans;
//    }


//    idx<k：基准点左侧不足 kk 个，递归处理右边，让基准点下标右移；
//    idx > kidx>k：基准点左侧超过 kk 个，递归处理左边，让基准点下标左移；
//    idx = kidx=k：基准点左侧恰好 kk 个，输出基准点左侧元素。

    int k;

    public int[] smallestK(int[] arr, int _k) {
        k = _k;
        int n = arr.length;
        int[] ans = new int[k];
        if (k == 0) return ans;
        qsort(arr, 0, n - 1);
        for (int i = 0; i < k; i++) ans[i] = arr[i];
        return ans;
    }

    void qsort(int[] arr, int left, int right) {
        if (left >= right) return;
        int i = left;
        int j = right;

        int ridx = new Random().nextInt(right - left + 1) + left;
        swap(arr, ridx, left);

        int x = arr[left];
        while (i < j) {
            while (i < j && arr[j] >= x) j--;
            while (i < j && arr[i] <= x) i++;
            swap(arr, i, j);
        }
        swap(arr, left, i);

        if (i > k) {
            qsort(arr, left, i - 1);
        }
        if (i < k) {
            qsort(arr, i + 1, right);
        }
    }

    void swap(int[] arr, int left, int right) {
        int tmp = arr[left];
        arr[left] = arr[right];
        arr[right] = tmp;
    }

    public static void main(String[] args) {
        new Solution().smallestK(new int[]{1, 2, 3}, 4);
    }
}
