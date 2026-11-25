package com.chinesechess.controller;

import com.chinesechess.model.Board;
import com.chinesechess.model.Piece;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
* 中國象棋 AI 玩家 (黑方)。
*/
public class ChineseChessAI {

private final Board board;

// 棋子價值表 (AI 為黑方，黑方為 MIN 玩家，但這裡我們用絕對分數計算，最後 AI 選最高分移動)
private static final double VALUE_GENERAL = 10000.0;
private static final double VALUE_CHARIOT = 900.0;
private static final double VALUE_CANNON = 450.0;
private static final double VALUE_HORSE = 400.0;
private static final double VALUE_ADVISOR = 200.0;
private static final double VALUE_ELEPHANT = 200.0;
private static final double VALUE_SOLDIER = 100.0;

public ChineseChessAI(Board board) {
this.board = board;
}

private static class Move {
int startR, startC, endR, endC;
double score; // 該步的分數

public Move(int sr, int sc, int er, int ec) {
this.startR = sr; this.startC = sc;
this.endR = er; this.endC = ec;
}
}

/**
* 執行 AI 移動
*/
public boolean makeMove() {
if (board.getCurrentPlayer() != Piece.BLACK) {
return false;
}

// 1. 取得所有合法移動
List<Move> moves = generateAllLegalMoves(Piece.BLACK);
if (moves.isEmpty()) {
return false; // 無路可走
}

// 2. 簡單評估每一步 (單層深度，貪婪演算法，吃最有價值的子)
Move bestMove = null;
double maxScore = -Double.MAX_VALUE;

for (Move move : moves) {
// 模擬移動
Piece target = board.getPiece(move.endR, move.endC);

// 基礎分：吃子加分
double moveScore = 0;
if (target != null) {
moveScore += getPieceValue(target);
}

// 稍微加一點隨機性，避免 AI 每次都走一樣
moveScore += (new Random().nextDouble() * 10.0);

// 紀錄分數
move.score = moveScore;

if (moveScore > maxScore) {
maxScore = moveScore;
bestMove = move;
}
}

// 3. 執行最佳移動
if (bestMove != null) {
System.out.println("AI 移動: (" + bestMove.startR + "," + bestMove.startC + ") -> (" + bestMove.endR + "," + bestMove.endC + ")");
return board.movePiece(bestMove.startR, bestMove.startC, bestMove.endR, bestMove.endC);
}

return false;
}

/**
* 產生所有合法步數
*/
private List<Move> generateAllLegalMoves(int color) {
List<Move> moves = new ArrayList<>();

for (int r = 0; r < Board.ROWS; r++) {
for (int c = 0; c < Board.COLS; c++) {
Piece p = board.getPiece(r, c);
if (p != null && p.getColor() == color) {

// 掃描棋盤所有點，看能不能走
for (int tr = 0; tr < Board.ROWS; tr++) {
for (int tc = 0; tc < Board.COLS; tc++) {

// 1. 基本規則檢查
if (p.isValidMove(board, r, c, tr, tc)) {

// 2. "自殺"檢查 (反將檢查)
// 必須模擬走這一步，看自己帥/將會不會被吃
Piece target = board.getPiece(tr, tc);
// 執行假移動
board.getBoardData()[tr][tc] = p;
board.getBoardData()[r][c] = null;

boolean selfInCheck = board.isCheck(color);

// 復原
board.getBoardData()[r][c] = p;
board.getBoardData()[tr][tc] = target;

// 只有這一步不會導致自己被將軍，才是合法的
if (!selfInCheck) {
moves.add(new Move(r, c, tr, tc));
}
}
}
}
}
}
}
return moves;
}

private double getPieceValue(Piece p) {
String n = p.getName();
switch (n) {
case "帥": case "將": return VALUE_GENERAL;
case "俥": case "車": return VALUE_CHARIOT;
case "炮": case "砲": return VALUE_CANNON;
case "傌": case "馬": return VALUE_HORSE;
case "相": case "象": return VALUE_ELEPHANT;
case "仕": case "士": return VALUE_ADVISOR;
case "兵": case "卒": return VALUE_SOLDIER;
default: return 0;
}
}
}

