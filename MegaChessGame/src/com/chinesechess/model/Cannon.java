package com.chinesechess.model;

/**
 * 炮/砲 (Cannon) - 走直線，吃子需隔一子。
 */
public class Cannon extends Piece {
    public Cannon(int color) {
        super(color, color == RED ? "炮" : "砲");
    }

    @Override
    public boolean isValidMove(Board board, int startR, int startC, int endR, int endC) {
        int dr = endR - startR;
        int dc = endC - startC;

        // 1. 必須走直線
        if (dr != 0 && dc != 0) return false;

        // 2. 計算中間阻擋的棋子數量
        int blockCount = 0;

        if (dr == 0) { // 水平移動
            int stepC = (dc > 0) ? 1 : -1;
            for (int c = startC + stepC; c != endC; c += stepC) {
                if (board.getPiece(startR, c) != null) blockCount++;
            }
        } else { // 垂直移動
            int stepR = (dr > 0) ? 1 : -1;
            for (int r = startR + stepR; r != endR; r += stepR) {
                if (board.getPiece(r, startC) != null) blockCount++;
            }
        }

        Piece targetPiece = board.getPiece(endR, endC);

        // 3. 規則判斷：
        if (targetPiece == null) {
            // 移動 (不吃子)：中間不能有阻擋 (Count 必須為 0)
            return blockCount == 0;
        } else {
            // 吃子：中間必須剛好隔一個子 (Count 必須為 1)，且不能是己方
            return blockCount == 1 && !isSameColor(targetPiece);
        }
    }
}