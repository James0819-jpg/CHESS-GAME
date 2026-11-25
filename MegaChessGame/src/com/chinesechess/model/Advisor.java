package com.chinesechess.model;

/**
 * 士/仕 (Advisor) - 只能在九宮格內，走對角線一格。
 */
public class Advisor extends Piece {
    public Advisor(int color) {
        super(color, color == RED ? "仕" : "士");
    }

    @Override
    public boolean isValidMove(Board board, int startR, int startC, int endR, int endC) {
        // 1. 檢查目標是否為己方棋子
        if (isSameColor(board.getPiece(endR, endC))) {
            return false;
        }

        int dr = Math.abs(endR - startR);
        int dc = Math.abs(endC - startC);

        // 2. 距離檢查：必須走對角線一格 (1x1)
        if (dr != 1 || dc != 1) {
            return false;
        }
        
        // 3. 九宮格檢查 (最常出錯的地方)
        // 檢查目標位置 (endR, endC) 是否在九宮格內
        // 假設九宮格的範圍是：列 3-5，紅方行 0-2，黑方行 7-9
        
        // 九宮格列檢查
        if (endC < 3 || endC > 5) {
            return false;
        }
        
        // 九宮格行檢查
        if (color == RED) {
            if (endR > 2) return false; // 紅方不能高於第 2 行
        } else { // BLACK
            if (endR < 7) return false; // 黑方不能低於第 7 行
        }

        return true; // 通過所有檢查
    }
}