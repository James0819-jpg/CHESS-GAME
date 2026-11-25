package com.chinesechess.model;

/**
 * 卒/兵 (Soldier) - 過河前後規則不同，不能後退。
 */
public class Soldier extends Piece {
    public Soldier(int color) {
        super(color, color == RED ? "兵" : "卒");
    }

    @Override
    public boolean isValidMove(Board board, int startR, int startC, int endR, int endC) {
        if (isSameColor(board.getPiece(endR, endC))) return false;

        int dr = endR - startR; // 垂直移動距離
        int dc = Math.abs(endC - startC); // 水平移動距離

        // 1. 距離檢查：只能走一格
        if (Math.abs(dr) + dc != 1) return false;
        
        // 2. 不能後退檢查
        if (color == RED) {
            if (dr > 0) return false; // 紅方不能向上(R變小)走
        } else { // BLACK
            if (dr < 0) return false; // 黑方不能向下(R變大)走
        }

        // 3. 過河檢查：過河前不能左右走
        boolean isAcrossRiver;
        if (color == BLACK) {
            isAcrossRiver = startR >= 5; // 紅方河界在 5 行
        } else { // BLACK
            isAcrossRiver = startR <= 4; // 黑方河界在 4 行
        }
        
        if (!isAcrossRiver) {
            // 未過河：只能向前走 (dc 必須為 0)
            if (dc != 0) return false;
        }
        // 過河後：可以左右走 (dc 可以是 1)
        
        return true;
    }
}