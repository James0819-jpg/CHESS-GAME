package com.chinesechess.model;

public class General extends Piece {
    public General(int color) {
        super(color, color == RED ? "帥" : "將");
    }

    @Override
    public boolean isValidMove(Board board, int startR, int startC, int endR, int endC) {
        if (isSameColor(board.getPiece(endR, endC))) return false;

        int dr = Math.abs(endR - startR);
        int dc = Math.abs(endC - startC);
        
        // 只能走直線一格
        if (!((dr == 1 && dc == 0) || (dr == 0 && dc == 1))) return false;

        // 九宮格檢查
        int minRow = (color == RED) ? 7 : 0;
        int maxRow = (color == RED) ? 9 : 2;
        
        if (endR < minRow || endR > maxRow || endC < 3 || endC > 5) return false;
        
        // 飛將檢查 (需在 Board.movePiece 中實現，此處略過)
        
        return true;
    }
}