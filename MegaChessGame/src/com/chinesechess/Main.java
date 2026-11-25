package com.chinesechess;

import com.chinesechess.controller.ChessController;
import com.chinesechess.model.Board;
import com.chinesechess.view.ChessPanel;
import com.chinesechess.view.ChessFrame;
import javax.swing.SwingUtilities;

public class Main {
    // 雖然現在從 LoginFrame 啟動，但保留 main 方便你單獨測試象棋
    public static void main(String[] args) {
        launchGame();
    }

    /**
     * 核心啟動方法：只負責開啟象棋遊戲視窗
     */
    public static void launchGame() {
        SwingUtilities.invokeLater(() -> {
            Board board = new Board();
            ChessFrame frame = new ChessFrame(board);
            ChessController controller = new ChessController(board, frame.getChessPanel());
            frame.getChessPanel().addMouseListener(controller);
            frame.setVisible(true);
        });
    }
}