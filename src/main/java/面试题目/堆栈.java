package 面试题目;

import java.util.*;

public class 堆栈 {

    // https://leetcode.cn/problems/valid-parentheses/submissions/697106644/?envType=study-plan-v2&envId=top-100-liked
    public boolean isValid(String s) {
        Deque<Character> stack = new ArrayDeque<>();
        for (char ch : s.toCharArray()) {
            if (ch == '{' || ch == '[' || ch == '(') {
                stack.push(ch);
            } else {
                if (stack.isEmpty()) return false;
                char top = stack.peek();
                if (ch == '}' && top == '{' || ch == ']' && top == '[' || ch == ')' && top == '(') {
                    stack.pop();
                } else {
                    return false;
                }
            }
        }
        return stack.isEmpty();
    }


    // https://leetcode.cn/problems/decode-string/?envType=study-plan-v2&envId=top-100-liked

    /**
     * 解码字符串
     * 将形如 k[encoded_string] 的编码字符串解码为重复k次的encoded_string
     * 支持嵌套编码格式
     *
     * @param s 编码后的字符串，格式为字母、数字和方括号的组合
     * @return 解码后的字符串
     */
    public String decodeString(String s) {
        // 初始化两个栈：数字栈存储重复次数，字符串栈存储历史字符串
        Deque<Integer> numStack = new ArrayDeque<>();
        Deque<StringBuilder> strStack = new ArrayDeque<>();

        // 当前正在构建的字符串和解析的数字
        StringBuilder currentStr = new StringBuilder();
        int num = 0;

        // 按字符遍历输入字符串进行解码处理
        for (char ch : s.toCharArray()) {
            if (Character.isDigit(ch)) {
                // 如果是数字字符，累加到当前数字中
                num = num * 10 + (ch - '0');
            } else if (ch == '[') {
                // 遇到左括号，保存当前状态并重置，准备处理括号内内容
                numStack.push(num);
                strStack.push(new StringBuilder(currentStr));

                // 重置数字和字符串，准备处理括号内的内容
                {
                    num = 0;
                    currentStr = new StringBuilder();
                }

            } else if (ch == ']') {
                // 遇到右括号，执行字符串重复操作并恢复上一层状态
                int repeatTimes = numStack.pop();
                StringBuilder prevStr = strStack.pop();

                // 将当前字符串重复指定次数
                StringBuilder repeatedStr = new StringBuilder();
                for (int i = 0; i < repeatTimes; i++) {
                    repeatedStr.append(currentStr);
                }

                // 将重复后的字符串追加到之前的字符串上
                {
                    num = 0; // 重置数字
                    currentStr = prevStr.append(repeatedStr);
                }

            } else {
                // 处理普通字符，直接添加到当前构建的字符串中
                currentStr.append(ch);
            }
        }

        // 返回最终解码后的字符串
        return currentStr.toString();
    }

    /**
     * 计算每日温度问题
     * 给定一个整数数组 temperatures ，表示每天的温度，
     * 返回一个数组 answer ，其中 answer[i] 是指对于第 i 天，
     * 下一个更高温度出现在几天后。如果气温在这之后都不会升高，
     * 请在该位置用 0 来代替。
     * <p>
     * 使用单调递减栈来解决此问题：
     * - 从右向左遍历温度数组
     * - 栈中存储的是温度的索引
     * - 维护一个单调递减的栈结构
     *
     * @param temperatures 温度数组
     * @return 每日等待更高温度的天数数组
     */
    public int[] dailyTemperatures(int[] temperatures) {
        int n = temperatures.length;
        // 初始化结果数组，默认值为0
        int[] ans = new int[n];
        // 使用双端队列作为栈，存储温度索引
        Deque<Integer> stack = new ArrayDeque<>();

        // 从右向左遍历温度数组
        for (int i = n - 1; i >= 0; i--) {
            // 维护单调递减栈：当前温度大于等于栈顶索引对应的温度时，弹出栈顶元素
            // 这样确保栈中始终保存着比当前温度更高的温度索引
            while (!stack.isEmpty() && temperatures[i] >= temperatures[stack.peek()]) {
                stack.pop();
            }

            // 如果栈为空，说明后面没有更高的温度，ans[i]保持默认值0
            // 否则计算天数差：栈顶索引 - 当前索引
            ans[i] = stack.isEmpty() ? 0 : stack.peek() - i;

            // 将当前索引入栈，为前面的元素提供比较基准
            stack.push(i);
        }

        return ans;
    }


    /**
     * 查找数组中第k大的元素
     * 使用最大堆来实现，时间复杂度为O(nlogn)
     *
     * @param nums 整数数组
     * @param k    第k大的元素（k从1开始计数）
     * @return 数组中第k大的元素
     */
    // https://leetcode.cn/problems/kth-largest-element-in-an-array/description/?envType=study-plan-v2&envId=top-100-liked
    public int findKthLargest(int[] nums, int k) {
        // 创建最大堆，比较器定义为(b - a)实现降序排列
        PriorityQueue<Integer> queMin = new PriorityQueue<>((a, b) -> b - a);

        // 将所有元素加入最大堆
        for (int num : nums) {
            queMin.offer(num);
        }

        // 弹出k-1个最大元素，剩下的堆顶就是第k大的元素
        while (--k > 0) {
            queMin.poll();
        }

        // 返回堆顶元素，即第k大的元素
        return queMin.peek();
    }

    /**
     * 找出数组中出现频率前k高的元素
     * 使用哈希表统计频次，最小堆维护前k个高频元素
     *
     * @param nums 输入的整数数组
     * @param k    需要返回的高频元素个数
     * @return 包含前k个高频元素的数组
     */
    // https://leetcode.cn/problems/top-k-frequent-elements/submissions/697148018/?envType=study-plan-v2&envId=top-100-liked
    public int[] topKFrequent(int[] nums, int k) {
        // 使用HashMap统计每个数字出现的频次
        Map<Integer, Integer> map = new HashMap<>();
        for (int num : nums) {
            // getOrDefault方法获取当前数字的频次，不存在则默认为0，然后加1
            map.put(num, map.getOrDefault(num, 0) + 1);
        }

        // 创建最小堆，按照频次升序排列（堆顶是最小频次元素）
        // 比较器(a,b)->b.getValue()-a.getValue()实现按频次降序排列
        PriorityQueue<Map.Entry<Integer, Integer>> pq = new PriorityQueue<>(
                (a, b) -> b.getValue() - a.getValue()
        );

        // 遍历频次映射，将元素加入堆中
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            pq.offer(entry);  // 将当前元素加入堆
            // 如果堆大小超过k，则移除频次最小的元素（堆顶）
            // 这样保证堆中始终只保留频次最高的k个元素
            if (pq.size() > k) {
                pq.poll();
            }
        }

        // 构造结果数组，从堆中取出前k个高频元素
        int[] res = new int[k];
        // 从后往前填充结果数组，因为堆顶是频次最小的元素
        for (int i = k - 1; i >= 0; i--) {
            res[i] = pq.poll().getKey();  // 取出键值（原数组中的数字）
        }

        return res;  // 返回前k个高频元素数组
    }

}

// https://leetcode.cn/problems/min-stack/?envType=study-plan-v2&envId=top-100-liked
class MinStack {

    Deque<Integer> stack = new ArrayDeque<>();
    Deque<Integer> minStack = new ArrayDeque<>();


    public MinStack() {
        minStack.push(Integer.MAX_VALUE);
    }

    public void push(int val) {
        stack.push(val);
        minStack.push(Math.min(val, minStack.peek()));
    }

    public void pop() {
        minStack.pop();
        stack.pop();
    }

    public int top() {
        return stack.peek();
    }

    public int getMin() {
        return minStack.peek();
    }
}

/**
 * MedianFinder 类用于动态维护数据流的中位数
 * 使用两个优先队列（堆）来实现：
 * - 最大堆 queMin：存储较小的一半元素
 * - 最小堆 queMax：存储较大的一半元素
 * 通过维护两个堆的大小平衡，可以在 O(1) 时间内获取中位数
 */
// 我们用两个优先队列 queMax 和 queMin 分别记录大于中位数的数和小于等于中位数的数。
class MedianFinder {
    // 最大堆，存储较小的一半元素，使用逆序比较器实现
    PriorityQueue<Integer> queMin = new PriorityQueue<>(Collections.reverseOrder());

    // 最小堆，存储较大的一半元素，使用默认升序比较器
    PriorityQueue<Integer> queMax = new PriorityQueue<>();

    /**
     * 构造函数，初始化 MedianFinder 对象
     */
    public MedianFinder() {
        // 初始化时两个堆都为空
    }

    /**
     * 向数据结构中添加一个数字
     * 保持两个堆的大小平衡：
     * - 最大堆的大小最多比最小堆大1
     * - 最小堆的大小不能超过最大堆
     *
     * @param num 要添加的整数
     */
    public void addNum(int num) {
        // 可以比做一个漏斗的形状
        // 如果最大堆为空，或者num小于等于最大堆的堆顶，则加入最大堆
        if (queMin.isEmpty() || num <= queMin.peek()) {
            queMin.offer(num);

            // 6 ==4 ==》 5 == 5
            // 平衡操作：如果最大堆的大小超过最小堆大小+1，
            // 则将最大堆堆顶移到最小堆，保持大小平衡
            if (queMin.size() > queMax.size() + 1) {
                queMax.offer(queMin.poll());
            }
        } else {
            // 否则加入最小堆
            queMax.offer(num);

            // 平衡操作：如果最小堆的大小超过最大堆，
            // 则将最小堆堆顶移到最大堆，保持大小平衡
            // 5 4  ==> 4 5
            if (queMax.size() > queMin.size()) {
                queMin.offer(queMax.poll());
            }
        }
    }

    /**
     * 获取当前数据流的中位数
     *
     * @return 当前数据流的中位数
     */
    public double findMedian() {
        // 如果两个堆大小相等，说明总元素个数为偶数
        // 中位数为两个堆顶元素的平均值
        if (queMin.size() == queMax.size()) {
            return (queMin.peek() + queMax.peek()) / 2.0;
        } else {
            // 如果两个堆大小不等，说明总元素个数为奇数
            // 中位数为较大堆（最大堆）的堆顶元素
            return queMin.peek();
        }
    }

    static void main(String[] args) {
        堆栈 solution = new 堆栈();

        // 测试 isValid 方法
        System.out.println("=== 测试 isValid 方法 ===");
        System.out.println(solution.isValid("()"));       // true
        System.out.println(solution.isValid("()[]{}"));   // true
        System.out.println(solution.isValid("(]"));       // false
        System.out.println(solution.isValid("([)]"));     // false
        System.out.println(solution.isValid("{[]}"));     // true

        // 测试 decodeString 方法
        System.out.println("\n=== 测试 decodeString 方法 ===");
        System.out.println(solution.decodeString("3[a]2[bc]"));     // "aaabcbc"
        System.out.println(solution.decodeString("3[a2[c]]"));      // "accaccacc"
        System.out.println(solution.decodeString("2[abc]3[cd]ef")); // "abcabccdcdcdef"
        System.out.println(solution.decodeString("abc3[cd]xyz"));   // "abccdcdcdxyz"

        // 测试 dailyTemperatures 方法
        System.out.println("\n=== 测试 dailyTemperatures 方法 ===");
        int[] temps1 = {73,74,75,71,69,72,76,73};
        int[] result1 = solution.dailyTemperatures(temps1);
        System.out.println(Arrays.toString(result1)); // [1, 1, 4, 2, 1, 1, 0, 0]

        int[] temps2 = {30,40,50,60};
        int[] result2 = solution.dailyTemperatures(temps2);
        System.out.println(Arrays.toString(result2)); // [1, 1, 1, 0]

        int[] temps3 = {30,60,90};
        int[] result3 = solution.dailyTemperatures(temps3);
        System.out.println(Arrays.toString(result3)); // [1, 1, 0]

        // 测试 findKthLargest 方法
        System.out.println("\n=== 测试 findKthLargest 方法 ===");
        int[] nums1 = {3,2,1,5,6,4};
        System.out.println(solution.findKthLargest(nums1, 2)); // 5

        int[] nums2 = {3,2,3,1,2,4,5,5,6};
        System.out.println(solution.findKthLargest(nums2, 4)); // 4

        // 测试 topKFrequent 方法
        System.out.println("\n=== 测试 topKFrequent 方法 ===");
        int[] nums3 = {1,1,1,2,2,3};
        int[] result4 = solution.topKFrequent(nums3, 2);
        System.out.println(Arrays.toString(result4)); // [1, 2]

        int[] nums4 = {1};
        int[] result5 = solution.topKFrequent(nums4, 1);
        System.out.println(Arrays.toString(result5)); // [1]

        // 测试 MinStack 类
        System.out.println("\n=== 测试 MinStack 类 ===");
        MinStack minStack = new MinStack();
        minStack.push(-2);
        minStack.push(0);
        minStack.push(-3);
        System.out.println(minStack.getMin()); // -3
        minStack.pop();
        System.out.println(minStack.top());    // 0
        System.out.println(minStack.getMin()); // -2

        // 测试 MedianFinder 类
        System.out.println("\n=== 测试 MedianFinder 类 ===");
        MedianFinder medianFinder = new MedianFinder();
        medianFinder.addNum(1);
        medianFinder.addNum(2);
        System.out.println(medianFinder.findMedian()); // 1.5
        medianFinder.addNum(3);
        System.out.println(medianFinder.findMedian()); // 2.0
    }
}
