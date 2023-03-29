package lee.pkg20211218;

public class Solution {

//    【打卡】：
//
//    又是一个【阅读理解题】，读懂题意后，就是一道【简单题】了
//
//【一次遍历】
//
//    当遇到board[i][j]=='X'时，分别检查左侧和上方的位置是否也是'X'：
//
//    如果是，则不是一艘新战舰；
//    否则，增加一艘新战舰；

    public int countBattleships(char[][] board) {
        int m = board.length, n = board[0].length, ans = 0;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (board[i][j] == 'X') {
                    ans += (j > 0 && board[i][j - 1] == 'X' ? 0 : (i > 0 && board[i - 1][j] == 'X' ? 0 : 1));
                }
            }
        }
        return ans;
    }


    public int countBattleships(char[][] board, int prefix) {
        int m = board.length, n = board[0].length, ans = 0;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (board[i][j] == 'X') {
                    if (j > 0 && board[i][j - 1] == 'X') {
                        ans += 0;
                    } else {
                        if (i > 0 && board[i - 1][j] == 'X') {
                            ans += 0;
                        } else {
                            ans += 1;
                        }
                    }
                }
            }
        }
        return ans;
    }
}
