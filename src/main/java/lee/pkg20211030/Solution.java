package lee.pkg20211030;

import java.util.*;
import java.util.stream.IntStream;

public class Solution {
    public int[] singleNumber(int[] nums) {
        Map<Integer, Integer> s = new HashMap<>();
        Set<Integer> s1 = new HashSet<>();
        for (int x : nums) {
            boolean flag = (s.get(x) == null ? s.put(x, 1) : s.put(x, s.get(x) + 1)) == null ? s1.add(x) : s1.remove(x);
        }
        return s1.stream().flatMapToInt(IntStream::of).toArray();
    }

    public int[] singleNumber1(int[] nums) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i : nums) map.put(i, map.getOrDefault(i, 0) + 1);
        int[] ans = new int[2];
        int idx = 0;
        for (int i : nums) {
            if (map.get(i) == 1) ans[idx++] = i;
        }
        return ans;
    }

    //    异或也叫半加运算，其运算法则相当于不带进位的二进制加法：二进制下用1表示真，0表示假，则异或的运算法则为：0⊕0=0，1⊕0=1，0⊕1=1，1⊕1=0（同为0，异为1）
    public int[] singleNumber2(int[] nums) {
        int sum = 0;
        for (int i : nums) sum ^= i;
        int k = -1;
        for (int i = 31; i >= 0 && k == -1; i--) {
            if (((sum >> i) & 1) == 1) k = i;
        }
        int[] ans = new int[2];
        for (int i : nums) {
            if (((i >> k) & 1) == 1) ans[1] ^= i;
            else ans[0] ^= i;
        }
        return ans;
    }


    public static void main(String[] args) {
        int[] arr = new int[]{-1, 0};
        var list = new Solution().singleNumber(arr);
        Arrays.stream(list).forEach(System.out::println);
    }
}
