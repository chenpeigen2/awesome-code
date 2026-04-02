package 面试题目;

import java.util.Scanner;

/**
 * ACM 竞赛所有常见输入格式汇总
 *
 * 建议用 BufferedReader + StringTokenizer 替代 Scanner 以提升性能，
 * 此处为清晰展示输入模式，统一使用 Scanner。
 */
public class ScannerTest {

    // ======================== 1. 单个值 ========================
    // 输入: 42
    static void type1_singleValue(Scanner sc) {
        int n = sc.nextInt();
        System.out.println(n);
    }

    // ======================== 2. 一行多个值（已知个数） ========================
    // 输入: 1 2 3 4 5
    static void type2_fixedCountOnOneLine(Scanner sc) {
        int n = 5;
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) {
            arr[i] = sc.nextInt();
        }
        for (int v : arr) System.out.println(v);
    }

    // ======================== 3. 先给个数 N，再给 N 个值 ========================
    // 输入:
    //   5
    //   1 2 3 4 5
    static void type3_countThenValues(Scanner sc) {
        int n = sc.nextInt();
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) {
            arr[i] = sc.nextInt();
        }
        for (int v : arr) System.out.println(v);
    }

    // ======================== 4. 先给个数 N，再给 N 行数据 ========================
    // 输入:
    //   3
    //   1 2
    //   3 4
    //   5 6
    static void type4_countThenNLines(Scanner sc) {
        int n = sc.nextInt();
        for (int i = 0; i < n; i++) {
            int a = sc.nextInt();
            int b = sc.nextInt();
            System.out.println(a + b);
        }
    }

    // ======================== 5. 多组测试数据，以 0 或特定值结束 ========================
    // 输入:
    //   1 2
    //   3 4
    //   0 0        ← 结束标记
    static void type5_stopOnSentinel(Scanner sc) {
        while (true) {
            int a = sc.nextInt();
            int b = sc.nextInt();
            if (a == 0 && b == 0) break;
            System.out.println(a + b);
        }
    }

    // ======================== 6. 多组测试数据，读到 EOF 结束 ========================
    // 输入:
    //   1 2
    //   3 4
    //   (Ctrl+D / EOF)
    static void type6_untilEOF(Scanner sc) {
        while (sc.hasNextInt()) {
            int a = sc.nextInt();
            int b = sc.nextInt();
            System.out.println(a + b);
        }
    }

    // ======================== 7. 第一行组数 T，每组先给 N 再给 N 行 ========================
    // 输入:
    //   2          ← T 组
    //   3          ← 第 1 组: N=3
    //   1 2 3
    //   4 5 6
    //   7 8 9
    //   2          ← 第 2 组: N=2
    //   10 20
    //   30 40
    static void type7_tGroupsEachWithN(Scanner sc) {
        int t = sc.nextInt();
        while (t-- > 0) {
            int n = sc.nextInt();
            for (int i = 0; i < n; i++) {
                int a = sc.nextInt();
                int b = sc.nextInt();
                int c = sc.nextInt();
                System.out.println(a + " " + b + " " + c);
            }
        }
    }

    // ======================== 8. 读取字符串（含空格） ========================
    // 输入:
    //   hello world foo bar
    static void type8_stringWithSpaces(Scanner sc) {
        sc.nextLine(); // 消耗上一行残留的换行符
        String line = sc.nextLine();
        System.out.println(line);
    }

    // ======================== 9. 读取 N 行字符串 ========================
    // 输入:
    //   3
    //   hello world
    //   foo bar
    //   java test
    static void type9_nStrings(Scanner sc) {
        int n = sc.nextInt();
        sc.nextLine(); // 消耗换行符
        for (int i = 0; i < n; i++) {
            String line = sc.nextLine();
            System.out.println(line);
        }
    }

    // ======================== 10. 读取矩阵（二维数组） ========================
    // 输入:
    //   2 3        ← 行数 列数
    //   1 2 3
    //   4 5 6
    static void type10_matrix(Scanner sc) {
        int rows = sc.nextInt();
        int cols = sc.nextInt();
        int[][] matrix = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = sc.nextInt();
            }
        }
        for (int[] row : matrix) {
            for (int v : row) System.out.print(v + " ");
            System.out.println();
        }
    }

    // ======================== 11. 字符逐个读取 ========================
    // 输入:
    //   ABC
    static void type11_chars(Scanner sc) {
        String s = sc.next();
        for (char c : s.toCharArray()) {
            System.out.println(c);
        }
    }

    // ======================== 12. 图的边输入 ========================
    // 输入:
    //   5 6        ← 顶点数 边数
    //   1 2        ← 边
    //   1 3
    //   2 4
    //   2 5
    //   3 4
    //   4 5
    static void type12_graph(Scanner sc) {
        int v = sc.nextInt(); // 顶点数
        int e = sc.nextInt(); // 边数
        for (int i = 0; i < e; i++) {
            int from = sc.nextInt();
            int to = sc.nextInt();
            System.out.println(from + " -> " + to);
        }
    }

    // ======================== 13. 带权图的边输入 ========================
    // 输入:
    //   5 6
    //   1 2 10     ← from to weight
    //   1 3 5
    //   2 4 7
    static void type13_weightedGraph(Scanner sc) {
        int v = sc.nextInt();
        int e = sc.nextInt();
        for (int i = 0; i < e; i++) {
            int from = sc.nextInt();
            int to = sc.nextInt();
            int weight = sc.nextInt();
            System.out.println(from + " -> " + to + " : " + weight);
        }
    }

    // ======================== 14. 第一行 N，接下来 N 行每行格式不同（先给操作类型） ========================
    // 输入:
    //   5
    //   push 1
    //   push 2
    //   pop
    //   top
    //   empty
    static void type14_mixedCommands(Scanner sc) {
        int n = sc.nextInt();
        for (int i = 0; i < n; i++) {
            String op = sc.next();
            switch (op) {
                case "push":
                    int val = sc.nextInt();
                    System.out.println("push " + val);
                    break;
                case "pop":
                    System.out.println("pop");
                    break;
                case "top":
                    System.out.println("top");
                    break;
                case "empty":
                    System.out.println("empty");
                    break;
            }
        }
    }

    // ======================== 15. 多组数据，每组以空行分隔 ========================
    // 输入:
    //   1 2 3
    //   4 5 6
    //
    //   7 8 9
    //   10 11 12
    static void type15_blankLineSeparated(Scanner sc) {
        sc.nextLine(); // 消耗可能的残留换行符
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line.isEmpty()) {
                System.out.println("--- group separator ---");
                continue;
            }
            String[] parts = line.trim().split("\\s+");
            for (String p : parts) {
                System.out.print(Integer.parseInt(p) + " ");
            }
            System.out.println();
        }
    }

    // ======================== 16. 高性能输入模板（BufferedReader） ========================
    // 推荐: 大数据量时用 BufferedReader + StringTokenizer，速度比 Scanner 快 5-10 倍
    /*
    import java.io.*;
    import java.util.*;

    public class Main {
        public static void main(String[] args) throws IOException {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            StreamTokenizer st = new StreamTokenizer(br);
            // 读取 int:  st.nextToken(); int n = (int) st.nval;
            // 或者手动 StringTokenizer:
            StringTokenizer st2 = new StringTokenizer(br.readLine());
            int a = Integer.parseInt(st2.nextToken());
            int b = Integer.parseInt(st2.nextToken());
        }
    }
    */

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // 取消注释你要测试的输入类型:
        // type1_singleValue(sc);
        // type2_fixedCountOnOneLine(sc);
        // type3_countThenValues(sc);
        // type4_countThenNLines(sc);
        // type5_stopOnSentinel(sc);
        // type6_untilEOF(sc);
        // type7_tGroupsEachWithN(sc);
        // type8_stringWithSpaces(sc);
        // type9_nStrings(sc);
        // type10_matrix(sc);
        // type11_chars(sc);
        // type12_graph(sc);
        // type13_weightedGraph(sc);
        // type14_mixedCommands(sc);
        // type15_blankLineSeparated(sc);

        sc.close();
    }
}
