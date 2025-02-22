package expl;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class Tetris extends Frame implements KeyListener, ActionListener, Runnable {

    static final int WIDTH = 10;
    static final int HEIGHT = 20;
    static final int BLOCK_SIZE = 20;
    static final int DELAY = 500; // milliseconds

    int[][] grid = new int[HEIGHT][WIDTH];
    Random random = new Random();
    int currentPieceType;
    int currentPieceX, currentPieceY;
    int nextPieceType;
    boolean gameOver = false;
    Thread gameThread;

    public Tetris() {
        setSize(WIDTH * BLOCK_SIZE, HEIGHT * BLOCK_SIZE);
        setTitle("Tetris");
        addKeyListener(this);
        setResizable(false);
        setVisible(true);

        nextPieceType = random.nextInt(7);
        newPiece();
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void newPiece() {
        currentPieceType = nextPieceType;
        nextPieceType = random.nextInt(7);
        currentPieceX = WIDTH / 2 - getPieceWidth(currentPieceType) / 2;
        currentPieceY = 0;
        if (isCollision()) {
            gameOver = true;
        }
    }

    public int getPieceWidth(int type) {
        switch (type) {
            case 0:
                return 4; // I
            case 1:
                return 3; // J
            case 2:
                return 3; // L
            case 3:
                return 3; // O
            case 4:
                return 3; // S
            case 5:
                return 3; // T
            case 6:
                return 3; // Z
            default:
                return 0;
        }
    }

    public boolean isCollision() {
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                if (getPiece(currentPieceType, x, y) && (currentPieceX + x < 0 || currentPieceX + x >= WIDTH || currentPieceY + y >= HEIGHT || grid[currentPieceY + y][currentPieceX + x] != 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean getPiece(int type, int x, int y) {
        switch (type) {
            case 0: // I
                return x == 1 || x == 2;
            case 1: // J
                return x == 0 && y == 0 || x == 0 && y == 1 || x == 0 && y == 2 || x == 1 && y == 2;
            case 2: // L
                return x == 2 && y == 0 || x == 2 && y == 1 || x == 2 && y == 2 || x == 1 && y == 2;
            case 3: // O
                return x == 1 && y == 1 || x == 1 && y == 2 || x == 2 && y == 1 || x == 2 && y == 2;
            case 4: // S
                return x == 1 && y == 0 || x == 2 && y == 0 || x == 0 && y == 1 || x == 1 && y == 1;
            case 5: // T
                return x == 1 && y == 0 || x == 0 && y == 1 || x == 1 && y == 1 || x == 2 && y == 1;
            case 6: // Z
                return x == 0 && y == 0 || x == 1 && y == 0 || x == 1 && y == 1 || x == 2 && y == 1;
            default:
                return false;
        }
    }

    public void movePiece(int dx, int dy) {
        currentPieceX += dx;
        currentPieceY += dy;
        if (isCollision()) {
            currentPieceX -= dx;
            currentPieceY -= dy;
        }
    }

    public void rotatePiece() {
        int newType = (currentPieceType + 1) % 7;
        int newWidth = getPieceWidth(newType);
        int newX = currentPieceX + (getPieceWidth(currentPieceType) - newWidth) / 2;
        currentPieceType = newType;
        currentPieceX = newX;
        if (isCollision()) {
            currentPieceType = (newType + 6) % 7; // Rotate back
            currentPieceX = newX - (getPieceWidth(currentPieceType) - newWidth) / 2;
        }
    }

    public void lockPiece() {
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                if (getPiece(currentPieceType, x, y)) {
                    grid[currentPieceY + y][currentPieceX + x] = 1;
                }
            }
        }
        clearLines();
        newPiece();
    }

    public void clearLines() {
        for (int y = 0; y < HEIGHT; y++) {
            boolean full = true;
            for (int x = 0; x < WIDTH; x++) {
                if (grid[y][x] == 0) {
                    full = false;
                    break;
                }
            }
            if (full) {
                for (int yy = y; yy > 0; yy--) {
                    System.arraycopy(grid[yy - 1], 0, grid[yy], 0, WIDTH);
                }
                for (int x = 0; x < WIDTH; x++) {
                    grid[0][x] = 0;
                }
                y--; // Check this row again
            }
        }
    }

    public void paint(Graphics g) {
        g.clearRect(0, 0, getWidth(), getHeight());
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                if (grid[y][x] != 0) {
                    g.fillRect(x * BLOCK_SIZE, y * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                }
            }
        }
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                if (getPiece(currentPieceType, x, y)) {
                    g.fillRect((currentPieceX + x) * BLOCK_SIZE, (currentPieceY + y) * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                }
            }
        }
        // Draw next piece
        int nextX = WIDTH * BLOCK_SIZE + 10;
        int nextY = 10;
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                if (getPiece(nextPieceType, x, y)) {
                    g.fillRect(nextX + x * BLOCK_SIZE, nextY + y * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                }
            }
        }
        if (gameOver) {
            g.drawString("Game Over", getWidth() / 2 - 40, getHeight() / 2);
        }
    }

    public void run() {
        while (!gameOver) {
            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            movePiece(0, 1);
            if (isCollision()) {
                lockPiece();
            }
            repaint();
        }
    }

    public void keyPressed(KeyEvent e) {
        if (gameOver) return;
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_LEFT:
                movePiece(-1, 0);
                break;
            case KeyEvent.VK_RIGHT:
                movePiece(1, 0);
                break;
            case KeyEvent.VK_DOWN:
                movePiece(0, 1);
                if (isCollision()) {
                    lockPiece();
                }
                break;
            case KeyEvent.VK_UP:
                rotatePiece();
                break;
            case KeyEvent.VK_SPACE:
                while (!isCollision()) {
                    movePiece(0, 1);
                }
                lockPiece();
                break;
        }
        repaint();
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

    public void actionPerformed(ActionEvent e) {
    }

    public static void main(String[] args) {
        new Tetris();
    }
}
