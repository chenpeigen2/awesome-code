package lee.pkg;

import java.util.PriorityQueue;

public class MedianFinder {

    PriorityQueue<Integer> queMin;
    PriorityQueue<Integer> queMax;

    /**
     * initialize your data structure here.
     */
    public MedianFinder() {
        // 4 3 2 1
        queMin = new PriorityQueue<Integer>((a, b) -> (b - a));
        // 5 6 7 8
        queMax = new PriorityQueue<Integer>((a, b) -> (a - b));
    }

    public void addNum(int num) {
        if (queMin.isEmpty() || num <= queMin.peek()) {
            queMin.offer(num);
            // 1 2 3 4 5 -> 1 1 2 3 4 5   -->
            // 1 2 3 4  or 1 2 3 4 5
            if (queMin.size() > queMax.size() + 1) {
                queMax.offer(queMin.poll());
            }
        } else {
            // 1 2 3 4  or 1 2 3 4 5
            queMax.offer(num);
            if (queMax.size() > queMin.size()) {
                queMin.offer(queMax.poll());
            }
        }
    }

    public double findMedian() {
        if (queMin.size() > queMax.size()) {
            return queMin.peek();
        }
        return (queMin.peek() + queMax.peek()) / 2.0;
    }

    private static final String TAG = MedianFinder.class.getName();


    public static void main(String[] args) {
        System.out.println(TAG);
    }
}

