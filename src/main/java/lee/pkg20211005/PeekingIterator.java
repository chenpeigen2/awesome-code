package lee.pkg20211005;

import java.util.Arrays;
import java.util.Iterator;

public class PeekingIterator implements Iterator<Integer> {
    // 1 2 3
    // 1 to2
    // 2 to2
    // 2 to3
    // 3 to4
    // hasnext = false

    private Iterator<Integer> iterator;
    private Integer nextElement;

    public PeekingIterator(Iterator<Integer> iterator) {
        // initialize any member here.
        this.iterator = iterator;
        nextElement = iterator.next();
    }

    // Returns the next element in the iteration without advancing the iterator.
    public Integer peek() {
        return nextElement;
    }

    // hasNext() and next() should behave the same as in the Iterator interface.
    // Override them if needed.
    @Override
    public Integer next() {
        Integer ret = nextElement;
        nextElement = iterator.hasNext() ? iterator.next() : null;
        return ret;
    }

    @Override
    public boolean hasNext() {
        return nextElement != null;
    }

    public static void main(String[] args) {
        int[] arr = new int[]{1, 2, 3};
        Iterator<Integer> i = Arrays.stream(arr).iterator();
        PeekingIterator peekingIterator = new PeekingIterator(i); // [1,2,3]
    }
}