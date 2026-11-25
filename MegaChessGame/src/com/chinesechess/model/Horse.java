package com.chinesechess.model;

/**
 * 馬/傌 (Horse) - 走日字，但必須檢查是否有絆腳。
 */
public class Horse extends Piece {
    public Horse(int color) {
        super(color, color == RED ? "傌" : "馬");
    }

    @Override
    public boolean isValidMove(Board board, int startR, int startC, int endR, int endC) {
        if (isSameColor(board.getPiece(endR, endC))) return false;
        
        int dr = Math.abs(endR - startR);
        int dc = Math.abs(endC - startC);

        // 1. 距離檢查：必須是日字 (1x2 或 2x1)
        boolean isLMove = (dr == 1 && dc == 2) || (dr == 2 && dc == 1);
        if (!isLMove) return false;
        
        // 2. 絆腳檢查
        int blockR = startR;
        int blockC = startC;
        
        if (dr == 1) { // 橫向日字 (垂直走 1，水平走 2) -> 絆腳點在水平方向
            blockC += (endC > startC ? 1 : -1);
        } else { // 垂直日字 (垂直走 2，水平走 1) -> 絆腳點在垂直方向
            blockR += (endR > startR ? 1 : -1);
        }
        
        if (board.getPiece(blockR, blockC) != null) return false; // 有棋子絆腳

        return true;
    }
}