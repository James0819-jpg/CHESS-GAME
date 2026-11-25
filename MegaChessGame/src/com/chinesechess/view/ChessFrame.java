package com.chinesechess.view;

import javax.swing.*;
import java.awt.*;
import com.chinesechess.model.Board;

/**
 * 遊戲主視窗
 */
public class ChessFrame extends JFrame {
    
    private final ChessPanel chessPanel;

    public ChessFrame(Board board) {
        setTitle("Java SE 17 Swing 中國象棋");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        chessPanel = new ChessPanel(board);
        
        setLayout(new BorderLayout());
        add(chessPanel, BorderLayout.CENTER);
        
        pack();
        setLocationRelativeTo(null); // 視窗置中
    }

    public ChessPanel getChessPanel() {
        return chessPanel;
    }
}