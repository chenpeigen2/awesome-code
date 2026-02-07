package 面试题目;

import java.util.ArrayDeque;
import java.util.Deque;

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
     *
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
