package lee.pkg20220802;

//https://leetcode.cn/problems/design-circular-queue/

// 多一个capacity加入游戏 使游戏更好玩

/**
 * 因为需要把full 和empty状态拆分，所以需要多一个hold
 *
 * 0 3 full
 * 0 0 empty
 */
public class MyCircularQueue {
    private int front;
    private int rear;
    private int capacity;
    private int[] elements;

    public MyCircularQueue(int k) {
        capacity = k;
        elements = new int[capacity];
        rear = front = 0;
    }

    public boolean enQueue(int value) {
        if (isFull()) return false;
        elements[rear] = value;
        rear = (rear + 1) % capacity;
        return true;
    }

    public boolean deQueue() {
        if (isEmpty()) return false;
        front = (front + 1) % capacity;
        return true;
    }

    public int Front() {
        if (isEmpty()) return -1;
        return elements[front];
    }

    public int Rear() {
        if (isEmpty()) return -1;
        return elements[(rear - 1 + capacity) % capacity];
    }

    public boolean isEmpty() {
        return rear == front;
    }

    public boolean isFull() {
        return (rear + 1) % capacity == front;
    }
}
