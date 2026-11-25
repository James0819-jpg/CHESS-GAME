package com.chinesechess.model;

/**
 * 象/相 (Elephant) - 走田字，不能過河，必須檢查田心。
 */
public class Elephant extends Piece {
    public Elephant(int color) {
        super(color, color == RED ? "相" : "象");
    }

    @Override
    public boolean isValidMove(Board board, int startR, int startC, int endR, int endC) {
        if (isSameColor(board.getPiece(endR, endC))) return false;

        int dr = Math.abs(endR - startR);
        int dc = Math.abs(endC - startC);
        
        // 1. 距離檢查：必須走田字 (2x2 對角線)
        if (dr != 2 || dc != 2) return false;

        // 2. 過河檢查：象不能過河
        if (color == RED && endR < 5) return false; // 紅方在 5-9 行
        if (color == BLACK && endR > 4) return false; // 黑方在 0-4 行

        // 3. 絆腳檢查 (田心)
        int midR = (startR + endR) / 2;
        int midC = (startC + endC) / 2;
        
        if (board.getPiece(midR, midC) != null) return false; // 田心有棋子
        
        return true;
    }
}