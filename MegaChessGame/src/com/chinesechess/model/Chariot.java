package com.chinesechess.model;

/**
 * 車/俥 (Chariot) - 走直線，中間不能有任何棋子阻擋。
 */
public class Chariot extends Piece {
    public Chariot(int color) {
        super(color, color == RED ? "俥" : "車");
    }

    @Override
    public boolean isValidMove(Board board, int startR, int startC, int endR, int endC) {
        if (isSameColor(board.getPiece(endR, endC))) return false;

        int dr = Math.abs(endR - startR);
        int dc = Math.abs(endC - startC);

        // 1. 必須走直線 (垂直或水平)
        if (dr != 0 && dc != 0) return false;

        // 2. 路線檢查：中間不能有阻擋

        if (dr == 0) { // 水平移動
            int step = (endC > startC) ? 1 : -1;
            for (int c = startC + step; c != endC; c += step) {
                if (board.getPiece(startR, c) != null) return false;
            }
        } else { // 垂直移動 (dc == 0)
            int step = (endR > startR) ? 1 : -1;
            for (int r = startR + step; r != endR; r += step) {
                if (board.getPiece(r, startC) != null) return false;
            }
        }
        
        return true;
    }
}