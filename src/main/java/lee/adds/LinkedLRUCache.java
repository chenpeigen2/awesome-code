package lee.adds;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class LinkedLRUCache<K, V> {

    private int capacity;

    private Map<K, LRUNode> map;

    private LRUNode head;

    private LRUNode tail;

    LinkedLRUCache(int capacity) {
        this.capacity = capacity;
        this.map = new HashMap<>();
    }

    public synchronized void put(K k, V v) {
        LRUNode node = map.get(k);

        // 存在该 key，将节点的设置为 head
        if (node != null) {
            node.value = v;

            remove(node, false);
        } else {
            /**
             * 该节点不存在
             * 1、将该节点加入缓存
             * 2、设置该节点为 head
             * 3、判断是否超出容量
             */
            node = new LRUNode(k, v);

            if (map.size() >= capacity) {
                //删除 tail 节点
                remove(tail, true);
            }

            map.put(k, node);

            setHead(node);
        }

        // 设置当前节点为首节点
        setHead(node);
    }

    public Object get(String key) {
        LRUNode node = map.get(key);
        if (node != null) {
            // 将刚操作的元素放到head
            remove(node, false);
            setHead(node);
            return node.value;
        }
        return null;
    }

    /**
     * 设置头结点
     *
     * @param node
     */
    private void setHead(LRUNode node) {
        if (head != null) {
            node.next = head;
            head.prev = node;
        }

        head = node;

        if (tail == null) {
            tail = node;
        }
    }

    /**
     * 从链表中删除此Node
     *
     * @param node
     * @param flag 为 true 就删除该节点的 key
     */
    private void remove(LRUNode node, boolean flag) {
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }
        node.next = null;
        node.prev = null;
        if (flag) {
            map.remove(node.key);
        }
    }

    private Iterator iterator() {
        return map.keySet().iterator();
    }

    private class LRUNode<K, V> {

        /**
         * cache 的 key
         */
        private K key;

        /**
         * cache 的 value
         */
        private V value;

        private LRUNode next;

        private LRUNode prev;

        LRUNode(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
}
