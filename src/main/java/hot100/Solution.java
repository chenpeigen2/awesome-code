package hot100;

import java.util.*;

public class Solution {
    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            if (map.containsKey(target - nums[i])) {
                return new int[]{map.get(target - nums[i]), i};
            }
            map.put(nums[i], i);
        }
        return new int[]{-1, -1};
    }


    public List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        for (String str : strs) {
            char[] arr = str.toCharArray();
            Arrays.sort(arr);
            String s = new String(arr);
            List<String> list = map.getOrDefault(s, new ArrayList<>());
            list.add(str);
            map.put(s, list);
        }

        return new ArrayList<>(map.values());
    }

    public int longestConsecutive(int[] nums) {
        Arrays.sort(nums);
        if (nums.length == 0) return 0;
        int ret = 1;
        for (int i = 1, count = 1, prev = nums[0]; i < nums.length; i++) {
            if (nums[i] == prev + 1) {
                count++;
            } else if (nums[i] == nums[i - 1]) {
                continue;
            } else {
                count = 1;
            }
            prev = nums[i];
            ret = Math.max(ret, count);
        }
        return ret;
    }

    public int sumNumbers(TreeNode root) {
        if (root == null) return 0;
        dfs_sumNumbers(0, root);
        return sumNumbers_sum;
    }

    int sumNumbers_sum = 0;

    void dfs_sumNumbers(int level_sum, TreeNode node) {
        level_sum = level_sum * 10 + node.val;
        if (node.left == null && node.right == null) {
            sumNumbers_sum += level_sum;
        }
        if (node.left != null) dfs_sumNumbers(level_sum, node.left);
        if (node.right != null) dfs_sumNumbers(level_sum, node.right);
    }

    //    https://leetcode.cn/problems/move-zeroes/?envType=study-plan-v2&envId=top-100-liked
    public void moveZeroes(int[] nums) {
        if (nums == null) {
            return;
        }
        int j = 0;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] != 0) {
                int temp = nums[i];

                nums[i] = nums[j];

                nums[j++] = temp;
            }
        }
    }

    public int removeElement(int[] nums, int val) {
//        int l = 0;
//        int r = nums.length;
//
//
//        while (l < r) {
//            if (nums[l] == val) {
//                nums[l] = nums[r - 1];
//                r--;
//            } else {
//                l++;
//            }
//        }
//        return l;

        int left = 0;
        for (int right = 0; right < nums.length; right++) {
            if (nums[right] != val) {
                nums[left] = nums[right];
                left++;
            }
        }
        return left;
    }

    //    https://leetcode.cn/problems/remove-duplicates-from-sorted-array/?envType=study-plan-v2&envId=top-interview-150
    public int removeDuplicates(int[] nums) {
        int left = 0;

        for (int right = 0; right < nums.length; right++) {
            if (nums[left] == nums[right]) continue;
            nums[++left] = nums[right];
        }

        return left + 1;
    }

    // you can change the 2 -> 1
    //    https://leetcode.cn/problems/remove-duplicates-from-sorted-array-ii/?envType=study-plan-v2&envId=top-interview-150
    public int removeDuplicates1(int[] nums) {
        int n = nums.length;
        if (n <= 2) {
            return n;
        }

        int slow = 2, fast = 2;

        while (fast < n) {
            if (nums[slow - 2] != nums[fast]) {
                nums[slow] = nums[fast];
                slow++;
            }
            fast++;
        }
        return slow;
    }

    //    https://leetcode.cn/problems/majority-element/?envType=study-plan-v2&envId=top-interview-150
    public int majorityElement(int[] nums) {
        Arrays.sort(nums);
        return nums[nums.length / 2];
    }

    //    https://leetcode.cn/problems/rotate-array/?envType=study-plan-v2&envId=top-interview-150
    public void rotate(int[] nums, int k) {
        int len = nums.length;
        k = k % len;
        int[] newArr = new int[len];
        for (int i = 0; i < len; i++) {
            newArr[(i + (k)) % len] = nums[i];
        }
        System.arraycopy(newArr, 0, nums, 0, len);
    }

    public int maxProfit(int[] prices) {
        int min = prices[0];

        int res = 0;
        for (int price : prices) {
            res = Math.max(res, price - min);
            min = Math.min(min, price);
        }
        return res;
    }

    //    https://leetcode.cn/problems/best-time-to-buy-and-sell-stock-ii/?envType=study-plan-v2&envId=top-interview-150
    public int maxProfit1(int[] prices) {
        int res = 0;

        int prev = prices[0];
        for (int i = 1; i < prices.length; i++) {
            if (prices[i] > prev) {
                res += (prices[i] - prev);
            }
            prev = prices[i];
        }

        return res;
    }

    //    https://leetcode.cn/problems/jump-game/?envType=study-plan-v2&envId=top-interview-150
    public boolean canJump(int[] nums) {
        int k = 0;
        for (int i = 0; i < nums.length; i++) {
            if (i > k) return false;
            k = Math.max(k, i + nums[i]);
        }
        return true;
    }

    //    https://leetcode.cn/problems/jump-game-ii/?envType=study-plan-v2&envId=top-interview-150

//    int jump(vector<int>& nums) {
//        int n=nums.size();
//        vector<int>v(n);
//        for(int i=0;i<n-1;i++){
//            int border=(nums[i]+i)>=n?n-1:nums[i]+i;
//            for(int j=i+1;j<=border;j++){
//                if(v[j]==0||v[i]+1<v[j]){
//                    v[j]=v[i]+1;
//                }
//            }
//        }
//        return v[n-1];
//    }

    // 每次在上次能跳到的范围（end）内选择一个能跳的最远的位置（也就是能跳到max_far位置的点）作为下次的起跳点 ！
    public int jump(int[] nums) {
        int length = nums.length;
        // 记录所用步数
        int steps = 0;
        // 记录在边界范围内，能跳跃的最远位置的下标
        int maxPosition = 0;
        // 记录在边界范围内，能跳跃的最远位置的下标
        int end = 0;
        for (int i = 0; i < length - 1; i++) {
            maxPosition = Math.max(maxPosition, i + nums[i]);
            if (i == end) {
                end = maxPosition;
                steps++;
            }
        }

        return steps;
    }

    //    https://leetcode.cn/problems/h-index/?envType=study-plan-v2&envId=top-interview-150
    public int hIndex(int[] citations) {
        Arrays.sort(citations);
        int len = citations.length;
        int h = 0;
//        for (int i = len - 1; i >= 0 && citations[i] > h; ) {
//            h++;
//            i--;
//        }
        for (int i = len - 1; i >= 0; i--) {
            if (citations[i] >= len - i) {
                h++;
            }
        }

        return h;
    }

    //    https://leetcode.cn/problems/insert-delete-getrandom-o1/description/?envType=study-plan-v2&envId=top-interview-150
    class RandomizedSet {

        static int[] nums = new int[200010];

        Random r = new Random();
        Map<Integer, Integer> m = new HashMap<>();
        int idx = -1;

        public RandomizedSet() {
        }

        /**
         * @param val the key we should input
         * @return true if succeed
         */
        public boolean insert(int val) {
            if (m.containsKey(val)) return false;
            nums[++idx] = val;
            m.put(val, idx);
            return true;
        }

        public boolean remove(int val) {
            if (!m.containsKey(val)) return false;
            int loc = m.remove(val); // the idx the val hold
            // value -- > index
            if (loc != idx) m.put(nums[idx], loc); // not the last, then put the map last insert-value into the loc
            nums[loc] = nums[idx--]; // index --> value
            return true;
        }

        public int getRandom() {
            return nums[r.nextInt(idx + 1)];
        }
    }

    /**
     * Your RandomizedSet object will be instantiated and called as such:
     * RandomizedSet obj = new RandomizedSet();
     * boolean param_1 = obj.insert(val);
     * boolean param_2 = obj.remove(val);
     * int param_3 = obj.getRandom();
     */

//    https://leetcode.cn/problems/product-of-array-except-self/?envType=study-plan-v2&envId=top-interview-150
    public int[] productExceptSelf(int[] nums) {
        int n = nums.length;
        int[] sum = new int[n];
        Arrays.fill(sum, 1);
        int beforeSum = 1;
        int afterSum = 1;
        for (int i = 0, j = n - 1; i < n; i++, j--) {

            sum[i] *= beforeSum;
            sum[j] *= afterSum;

            beforeSum *= nums[i];
            afterSum *= nums[j];
        }

        return sum;
    }

    //    https://leetcode.cn/problems/gas-station/?envType=study-plan-v2&envId=top-interview-150
//    public int canCompleteCircuit(int[] gas, int[] cost) {
//        int len = gas.length;
//        int[] offset = new int[len];
//        for (int i = 0; i < len; i++) {
//            offset[i] = gas[i] - cost[i];
//        }
//        int idx = -1;
//        int sum = 0;
//        for (int i = 0; i < len; i++) {
//            sum += offset[i];
//            if (offset[i] > 0 && idx == -1) {
//                idx = i;
//            }
//        }
//
//        return sum >= 0 ? idx : -1;
//    }
    public int canCompleteCircuit(int[] gas, int[] cost) {
        int start = -1;
        int gasSum = 0;
        int costSum = 0;

        for (int i = 0, suffixSum = 0; i < gas.length; i++) {
            gasSum += gas[i];
            costSum += cost[i];
            suffixSum += gas[i] - cost[i];

            if (suffixSum < 0) {
                start = -1;
                suffixSum = 0;
            } else {
                if (start == -1) start = i;
            }
        }

        return costSum > gasSum ? -1 : start;
    }


    //    https://leetcode.cn/problems/roman-to-integer/description/?envType=study-plan-v2&envId=top-interview-150
    public int romanToInt(String s) {
        int sum = 0;
        int preNum = getValue(s.charAt(0));
        for (int i = 1; i < s.length(); i++) {
            int num = getValue(s.charAt(i));
            if (preNum < num) {
                sum -= preNum;
            } else {
                sum += preNum;
            }
            preNum = num;
        }
        sum += preNum;
        return sum;
    }

    private int getValue(char ch) {
        switch (ch) {
            case 'I':
                return 1;
            case 'V':
                return 5;
            case 'X':
                return 10;
            case 'L':
                return 50;
            case 'C':
                return 100;
            case 'D':
                return 500;
            case 'M':
                return 1000;
            default:
                return 0;
        }
    }

    //    https://leetcode.cn/problems/word-pattern/?envType=study-plan-v2&envId=top-interview-150
    public boolean wordPattern(String pattern, String s) {
        HashMap<Character, String> map = new HashMap<>();
        int len = pattern.length();
        String[] str = s.split(" ");
        if (len != str.length) return false;
        for (int i = 0; i < len; i++) {
            char key = pattern.charAt(i);
            if (!map.containsKey(key)) {
                if (map.containsValue(str[i])) {
                    return false;
                }
                map.put(key, str[i]);
            } else if (!map.get(key).equals(str[i])) {
                return false;
            }
        }
        return true;
    }

    //    https://leetcode.cn/problems/valid-anagram/?envType=study-plan-v2&envId=top-interview-150
    public boolean isAnagram(String s, String t) {
        int[] arr = new int[26];
        for (char ch : s.toCharArray()) {
            arr[ch - 'a']++;
        }
        for (char ch : t.toCharArray()) {
            arr[ch - 'a']--;
            if (arr[ch-'a'] < 0) return false;
        }
        return true;
    }

    public static void main(String[] args) {
        var app = new Solution();
        app.removeDuplicates1(new int[]{2, 2, 2, 3});
        int ans = app.romanToInt("IV");
        System.out.println(ans);
    }

}
