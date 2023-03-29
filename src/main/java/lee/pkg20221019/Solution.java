package lee.pkg20221019;

public class Solution {
    public int countStudents(int[] students, int[] sandwiches) {

        // 这样的思路错了，因为得到的stu会出队
//        int len = students.length;
//        int[] arr = new int[len * 2];
//        for (int i = 0; i < arr.length; i++) {
//            arr[i] = students[i % len];
//        }
//        int max = 0;
//        for (int i = 0, j = 0; i < arr.length && j < sandwiches.length; i++) {
//            if (arr[i] != sandwiches[j]) {
//                j = 0;
//            } else {
//                j++;
//                max = Math.max(max, j);
//            }
//        }
//
//        return sandwiches.length - max;

        /**
         * 问题转换为 【如果我提供的面包没人愿意吃，代表loop就结束了】
         */
        int[] cnt = new int[2];
        for (int x : students) cnt[x]++;
        for (int i = 0; i < sandwiches.length; i++) {
            if (--cnt[sandwiches[i]] == -1) return sandwiches.length - i;
        }
        return 0;
    }
}
