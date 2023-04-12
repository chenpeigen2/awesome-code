package lee.pkg;

import lee.adds.TreeNode;

public class Solution {
    public int[] runningSum(int[] nums) {
        if (nums.length == 1) {
            return nums;
        }
        for (int i = 1; i < nums.length; i++) {
            nums[i] = nums[i] + nums[i - 1];
        }
        return nums;
    }

    int[] tmp;
    int[] arr;

    public int sumOddLengthSubarrays1(int[] arr) {
        int sum = 0;
        int[] tmp = new int[arr.length];
        tmp[0] = arr[0];
        for (int i = 1; i < tmp.length; i++) {
            tmp[i] = arr[i] + tmp[i - 1];
        }

        for (int i = 0; i < arr.length; i++) {
            for (int j = i; j < arr.length; j += 2) {
                if (i >= 1) {
                    sum = sum + tmp[j] - tmp[i - 1];
                } else {
                    sum = sum + tmp[j];
                }
            }
        }
        return sum;
    }

    public int sumOddLengthSubarrays(int[] arr) {
        int sum = 0;
        tmp = new int[arr.length];
        tmp[0] = arr[0];
        this.arr = arr;
        for (int i = 1; i < tmp.length; i++) {
            tmp[i] = arr[i] + tmp[i - 1];
        }

        for (int i = 0; i < arr.length; i++) {
            for (int j = i; j < arr.length; j += 2) {
                sum = sum + sumbetween(i, j);
            }
        }
        return sum;
    }

    int sumbetween(int i, int j) {
        if (i == j) {
            return arr[i];
        } else {
            int i1 = i - 1;
            if (i1 == -1) {
                return tmp[j];
            } else {
                return tmp[j] - tmp[i1];
            }
        }
    }

    public int[] corpFlightBookings(int[][] bookings, int n) {
        int[] result = new int[n];
        for (int[] booking : bookings) {
            int start = booking[0] - 1;
            // end 可能是最后一个数字
            int end = booking[1] - 1;
            int seats = booking[2];
            result[start] += seats;
            if (end + 1 < n) {
                result[end + 1] -= seats;
            }
        }
        for (int i = 1; i < n; i++) {
            result[i] += result[i - 1];
        }
        return result;
    }

    public int compareVersion(String version1, String version2) {
        String[] s1 = version1.split("\\.");
        String[] s2 = version2.split("\\.");

        int len1 = s1.length;
        int len2 = s2.length;

        for (int i = 0; i < len1 || i < len2; i++) {
            int a1 = 0;
            int a2 = 0;
            if (i < len1) {
                a1 = Integer.parseInt(s1[i]);
            }
            if (i < len2) {
                a2 = Integer.parseInt(s2[i]);
            }
            if (a1 > a2) {
                return 1;
            } else if (a1 < a2) {
                return -1;
            }
        }

        return 0;
    }

    //    https://leetcode.cn/problems/construct-binary-tree-from-preorder-and-inorder-traversal/solution/qian-xu-bian-li-python-dai-ma-java-dai-ma-by-liwei/
    public TreeNode buildTree(int[] preorder, int[] inorder) {
        int preLen = preorder.length;
        int inLen = inorder.length;
        if (preLen != inLen) throw new RuntimeException("Incorrect input data");
        return buildTree(preorder, 0, preLen - 1, inorder, 0, inLen - 1);
    }

    private TreeNode buildTree(int[] preorder, int preLeft, int preRight,
                               int[] inorder, int inLeft, int inRight) {
        if (preLeft > preRight || inLeft > inRight) return null;
        int pivot = preorder[preLeft];
        TreeNode root = new TreeNode(pivot);

        int pivotIndex = inLeft;
        while (inorder[pivotIndex] != pivot) pivotIndex++;

        root.left = buildTree(preorder, preLeft + 1, pivotIndex - inLeft + preLeft, inorder, inLeft, pivotIndex - 1);
        root.right = buildTree(preorder, pivotIndex - inLeft + preLeft + 1, preRight, inorder, pivotIndex + 1, inRight);

        return root;

    }

    //    https://leetcode.cn/problems/longest-common-prefix/solution/zui-chang-gong-gong-qian-zhui-by-leetcode-solution/
    public String longestCommonPrefix(String[] strs) {
        if (strs == null || strs.length == 0) {
            return "";
        }
        int length = strs[0].length();
        int count = strs.length;
        for (int i = 0; i < length; i++) {
            char c = strs[0].charAt(i);
            for (int j = 1; j < count; j++) {
                if (i == strs[j].length() || strs[j].charAt(i) != c) {
                    return strs[0].substring(0, i);
                }
            }
        }
        return strs[0];
    }


    public static void main(String[] args) {
        var app = new Solution();
//        int[] arr = new int[]{1, 4, 2, 5, 3};
        var a = app.compareVersion("7.5.2.4", "7.5.3");
        System.out.println(a);
    }
}