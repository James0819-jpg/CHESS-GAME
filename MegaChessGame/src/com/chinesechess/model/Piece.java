package com.chinesechess.model;

public abstract class Piece {
    
    public static final int RED = 0;
    public static final int BLACK = 1;

    protected final int color;
    protected final String name;

    public Piece(int color, String name) {
        this.color = color;
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    /**
     * 檢查移動是否合法。
     * @param board 遊戲棋盤的狀態
     * @param startR 起始行 (0-9)
     * @param startC 起始列 (0-8)
     * @param endR 目標行 (0-9)
     * @param endC 目標列 (0-8)
     * @return 如果移動合法則返回 true
     */
    public abstract boolean isValidMove(Board board, int startR, int startC, int endR, int endC);

    protected boolean isSameColor(Piece targetPiece) {
        return targetPiece != null && this.color == targetPiece.color;
    }

    /**
     * 檢查目標位置是否在棋盤內
     */
    protected boolean isWithinBounds(int r, int c) {
        return r >= 0 && r < Board.ROWS && c >= 0 && c < Board.COLS;
    }
}