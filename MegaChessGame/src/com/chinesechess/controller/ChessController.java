package com.chinesechess.controller;

import com.chinesechess.model.Board;
import com.chinesechess.model.Piece;
import com.chinesechess.view.ChessPanel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
* 處理滑鼠輸入，連結 Model 和 View。
* 協調人類玩家 (紅方) 和 AI 玩家 (黑方) 的回合。
*/
public class ChessController extends MouseAdapter {

private final Board board;
private final ChessPanel panel;
private final ChineseChessAI chessAI;

// 儲存被選中棋子的座標
private int startR = -1;
private int startC = -1;

// 用於標記遊戲是否已結束
private boolean isGameOver = false;

// 防止玩家在 AI 思考時點擊
private boolean isAIThinking = false;

public ChessController(Board board, ChessPanel panel) {
this.board = board;
this.panel = panel;
this.chessAI = new ChineseChessAI(board);
}

@Override
public void mouseClicked(MouseEvent e) {
// 如果遊戲結束、輪到黑方(AI)、或 AI 正在思考中，則忽略點擊
if (isGameOver || board.getCurrentPlayer() == Piece.BLACK || isAIThinking) {
return;
}

int r = panel.getRowFromY(e.getY());
int c = panel.getColFromX(e.getX());

if (r < 0 || r >= Board.ROWS || c < 0 || c >= Board.COLS) {
return;
}

Piece clickedPiece = board.getPiece(r, c);
boolean moveExecuted = false;

// --- 1. 人類選擇/移動邏輯 ---
if (startR == -1) {
// 選棋
if (clickedPiece != null && clickedPiece.getColor() == board.getCurrentPlayer()) {
startR = r;
startC = c;
panel.setSelectedPosition(r, c);
}
} else {
// 二次點擊：可能是改選自己人，或是移動/吃子
if (clickedPiece != null && clickedPiece.getColor() == board.getCurrentPlayer()) {
// 改選
startR = r;
startC = c;
panel.setSelectedPosition(r, c);
} else {
// 移動
boolean success = board.movePiece(startR, startC, r, c);
if (success) {
moveExecuted = true;
// 重置選擇並重繪
startR = -1;
startC = -1;
panel.setSelectedPosition(-1, -1);
panel.repaint(); // 立即更新畫面顯示人類的一步

if (checkGameOver()) {
return;
}
} else {
// 移動不合法 (例如被將軍、違反規則)
// 不彈出視窗擾民，只取消選取或播放音效皆可
System.out.println("移動不合法");
}
}
}

// --- 2. 觸發 AI 邏輯 (使用獨立執行緒) ---
if (moveExecuted && !isGameOver) {
startAIThread();
}
}

/**
* 開啟一個新的執行緒來跑 AI，避免卡死介面
*/
private void startAIThread() {
isAIThinking = true; // 鎖定玩家輸入

new Thread(() -> {
try {
// 模擬思考時間，讓人類看清楚剛剛發生什麼事
Thread.sleep(600);

// 執行 AI 運算 (這比較耗時)
boolean aiMoved = chessAI.makeMove();

// 運算完畢，回到主執行緒 (EDT) 更新畫面
SwingUtilities.invokeLater(() -> {
if (aiMoved) {
panel.repaint();
checkGameOver();
} else {
// AI 無路可走 (困斃)
if (!isGameOver) {
displayWinner("紅方 (人類) - 對手困斃");
isGameOver = true;
}
}
isAIThinking = false; // 解鎖
});

} catch (InterruptedException ex) {
ex.printStackTrace();
isAIThinking = false;
}
}).start();
}

private boolean checkGameOver() {
if (board.isGeneralMissing(Piece.RED)) {
displayWinner("黑方 (電腦)");
isGameOver = true;
return true;
}

if (board.isGeneralMissing(Piece.BLACK)) {
displayWinner("紅方 (人類)");
isGameOver = true;
return true;
}

return false;
}

private void displayWinner(String winner) {
String message = winner + " 獲勝！";
JOptionPane.showMessageDialog(panel, message, "遊戲結束", JOptionPane.INFORMATION_MESSAGE);

// 簡單處理：結束後不做動作，如果要重玩需重開程式
panel.removeMouseListener(this);
}
}
