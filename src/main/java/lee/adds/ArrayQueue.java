package lee.adds;

public class ArrayQueue {
    private int maxSize;
    private int front;
    private int rear;
    private int[] arr;

    public ArrayQueue() {

    }

    public ArrayQueue(int maxSize) {
        this.maxSize = maxSize;
        arr = new int[maxSize];
        front = -1;
        rear = -1;
    }

    public boolean queueIsFull() {
        return rear == maxSize - 1;
    }

    public boolean queueIsEmpty() {
        return rear == front;
    }

    public void enQueue(int n) {
        if (queueIsFull()) {
            throw new RuntimeException("queue is full");
        }
        rear++;
        arr[rear] = n;
    }

    public int deQueue() {
        if (queueIsEmpty()) {
            throw new RuntimeException("queue is empty");
        }
        front++;
        return arr[front];
    }

    public int size() {
        return rear - front;
    }

    public int peek() {
        if (queueIsEmpty()) {
            throw new RuntimeException("queue is empty");
        }
        return arr[front + 1];
    }
}
