package lee.pkg20220712;

public class Solution {
    public int oddCells(int m, int n, int[][] indices) {
        int[] rows = new int[m];
        int[] cols = new int[n];
        for (int[] index : indices) {
            rows[index[0]]++;
            cols[index[1]]++;
        }
        int res = 0;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (((rows[i] + cols[j]) & 1) != 0) {
                    res++;
                }
            }
        }
        return res;
    }

    public int oddCells1(int m, int n, int[][] ins) {
        boolean[] r = new boolean[m], c = new boolean[n];
        int a = 0, b = 0;
        for (int[] info : ins) {
            // fucking amazing
            a += (r[info[0]] = !r[info[0]]) ? 1 : -1;
            b += (c[info[1]] = !c[info[1]]) ? 1 : -1;
        }
        return a * (n - b) + (m - a) * b;
    }

    public static void main(String[] args) {
        boolean[] r = new boolean[12], c = new boolean[12];
        System.out.println();
    }
}