package lee.pkg20220716;

import java.util.ArrayDeque;
import java.util.Queue;

//https://leetcode.cn/problems/qIsx9U/
public class MovingAverage {

    Queue<Integer> q;
    int size;

    double sum;

    public MovingAverage(int size) {
        q = new ArrayDeque<>();
        this.size = size;
        sum = 0;
    }

    public double next(int val) {
        if (size == q.size()) {
            sum -= q.remove();
        }
        q.offer(val);
        sum += val;
        return sum / q.size();
    }

    public static void main(String[] args) {
        Queue<Integer> q = new ArrayDeque<>();
        q.offer(1);
        q.offer(2);
        System.out.println(q.peek());
        System.out.println(q.element());
    }
}
