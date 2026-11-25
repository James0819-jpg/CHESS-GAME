package com.chinesechess.view;

import com.chinesechess.model.Board;
import com.chinesechess.model.Piece;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.geom.Point2D;

public class ChessPanel extends JPanel {

    // --- 尺寸參數 (保持不變以維持 Controller 判定準確) ---
    private static final int TILE_SIZE = 60; 
    private static final int MARGIN = 30;    
    private static final int WIDTH = (Board.COLS - 1) * TILE_SIZE + 2 * MARGIN;
    private static final int HEIGHT = (Board.ROWS - 1) * TILE_SIZE + 2 * MARGIN;

    // --- 風格顏色定義 (與暗棋一致) ---
    private final Color WOOD_DARK = new Color(60, 30, 10);
    private final Color WOOD_LIGHT = new Color(100, 60, 30);
    // 棋子顏色 (象牙白立體感)
    private final Color PIECE_COLOR_1 = new Color(255, 255, 255);
    private final Color PIECE_COLOR_2 = new Color(230, 220, 190);
    private final Color PIECE_SHADOW = new Color(0, 0, 0, 100);

    private final Board board;

    // 用於高亮顯示當前選中的棋子
    private int selectedR = -1;
    private int selectedC = -1;

    public ChessPanel(Board board) {
        this.board = board;
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        // 背景色交給 paintComponent 的漸層處理，這裡設什麼不重要
        setBackground(WOOD_DARK);
    }

    @Override
    protected void paintComponent(Graphics g) {
        // 不要呼叫 super.paintComponent，我們要完全重繪背景
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 1. 繪製深色木紋背景 (Gradient)
        GradientPaint gp = new GradientPaint(0, 0, WOOD_LIGHT, getWidth(), getHeight(), WOOD_DARK);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // 2. 繪製帶雕刻感的棋盤線
        drawWoodBoard(g2d);

        // 3. 繪製九宮格
        drawPalace(g2d);

        // 4. 繪製楚河漢界
        drawRiver(g2d);

        // 5. 繪製移動軌跡 (紅色框框)
        drawLastMoveHighlight(g2d);
        
        // 6. 繪製所有棋子 (3D 立體風格)
        drawPieces(g2d);

        // 7. 繪製選中高亮 (綠色發光)
        drawSelectionHighlight(g2d);
    }

    // --- 繪圖邏輯修改區 ---

    private void drawWoodBoard(Graphics2D g2d) {
        // 繪製垂直線
        for (int c = 0; c < Board.COLS; c++) {
            int x = c * TILE_SIZE + MARGIN;
            // 上半部
            drawEtchedLine(g2d, x, MARGIN, x, 4 * TILE_SIZE + MARGIN);
            // 下半部
            drawEtchedLine(g2d, x, 5 * TILE_SIZE + MARGIN, x, (Board.ROWS - 1) * TILE_SIZE + MARGIN);
        }
        
        // 補上左右兩邊的連接線 (楚河漢界兩側的直線)
        int xLeft = MARGIN;
        int xRight = (Board.COLS - 1) * TILE_SIZE + MARGIN;
        drawEtchedLine(g2d, xLeft, 4 * TILE_SIZE + MARGIN, xLeft, 5 * TILE_SIZE + MARGIN);
        drawEtchedLine(g2d, xRight, 4 * TILE_SIZE + MARGIN, xRight, 5 * TILE_SIZE + MARGIN);

        // 繪製橫線
        for (int r = 0; r < Board.ROWS; r++) {
            int y = r * TILE_SIZE + MARGIN;
            drawEtchedLine(g2d, MARGIN, y, (Board.COLS - 1) * TILE_SIZE + MARGIN, y);
        }
    }

    // 輔助：畫出雕刻效果的線 (暗線+亮線)
    private void drawEtchedLine(Graphics2D g2d, int x1, int y1, int x2, int y2) {
        g2d.setStroke(new BasicStroke(2));
        g2d.setColor(new Color(0, 0, 0, 180)); // 深色凹槽
        g2d.drawLine(x1, y1, x2, y2);
        g2d.setColor(new Color(255, 255, 255, 40)); // 淺色邊緣 (反光)
        g2d.drawLine(x1 + 1, y1 + 1, x2 + 1, y2 + 1);
    }
    
    private void drawPalace(Graphics2D g2d) {
        // 紅方九宮
        drawEtchedLine(g2d, 3 * TILE_SIZE + MARGIN, 0 * TILE_SIZE + MARGIN, 
                            5 * TILE_SIZE + MARGIN, 2 * TILE_SIZE + MARGIN);
        drawEtchedLine(g2d, 5 * TILE_SIZE + MARGIN, 0 * TILE_SIZE + MARGIN, 
                            3 * TILE_SIZE + MARGIN, 2 * TILE_SIZE + MARGIN);
        
        // 黑方九宮
        drawEtchedLine(g2d, 3 * TILE_SIZE + MARGIN, 7 * TILE_SIZE + MARGIN, 
                            5 * TILE_SIZE + MARGIN, 9 * TILE_SIZE + MARGIN);
        drawEtchedLine(g2d, 5 * TILE_SIZE + MARGIN, 7 * TILE_SIZE + MARGIN, 
                            3 * TILE_SIZE + MARGIN, 9 * TILE_SIZE + MARGIN);
    }
    
    private void drawRiver(Graphics2D g2d) {
        // 這裡不再畫實心矩形，改為直接畫文字，保持木紋底
        int riverY = 4 * TILE_SIZE + MARGIN + TILE_SIZE / 2;
        
        g2d.setFont(new Font("KaiTi", Font.BOLD, 36)); // 改用楷體更像象棋
        g2d.setColor(new Color(0, 0, 0, 60)); // 文字陰影
        g2d.drawString("楚 河", 2 * TILE_SIZE + MARGIN + 2, riverY + 12);
        g2d.drawString("漢 界", 5 * TILE_SIZE + MARGIN + 2, riverY + 12);

        g2d.setColor(new Color(255, 255, 255, 50)); // 文字本體 (淡淡的浮雕感)
        g2d.drawString("楚 河", 2 * TILE_SIZE + MARGIN, riverY + 10);
        g2d.drawString("漢 界", 5 * TILE_SIZE + MARGIN, riverY + 10);
    }

    private void drawPieces(Graphics2D g2d) {
        // 使用與暗棋 RoundButton 類似的邏輯
        int pieceDiameter = TILE_SIZE - 6; // 稍微留點縫隙
        Font pieceFont = new Font("KaiTi", Font.BOLD, 32);
        g2d.setFont(pieceFont);

        for (int r = 0; r < Board.ROWS; r++) {
            for (int c = 0; c < Board.COLS; c++) {
                Piece piece = board.getPiece(r, c);
                if (piece != null) {
                    int x = c * TILE_SIZE + MARGIN - pieceDiameter / 2;
                    int y = r * TILE_SIZE + MARGIN - pieceDiameter / 2;

                    // 1. 陰影
                    g2d.setColor(PIECE_SHADOW);
                    g2d.fillOval(x + 3, y + 3, pieceDiameter, pieceDiameter);

                    // 2. 3D 球體漸層 (模擬 RoundButton)
                    Point2D center = new Point2D.Float(x + pieceDiameter / 2.0f, y + pieceDiameter / 2.0f);
                    float radius = pieceDiameter / 2.0f;
                    float[] dist = {0.0f, 0.8f, 1.0f};
                    Color[] colors = {PIECE_COLOR_1, PIECE_COLOR_2, new Color(180, 170, 140)};
                    RadialGradientPaint p = new RadialGradientPaint(center, radius, dist, colors);
                    
                    g2d.setPaint(p);
                    g2d.fillOval(x, y, pieceDiameter, pieceDiameter);

                    // 3. 邊框 (細線)
                    g2d.setColor(new Color(100, 90, 70));
                    g2d.setStroke(new BasicStroke(1));
                    g2d.drawOval(x, y, pieceDiameter, pieceDiameter);

                    // 4. 文字
                    if (piece.getColor() == Piece.RED) {
                        g2d.setColor(new Color(200, 0, 0)); // 深紅
                    } else {
                        g2d.setColor(new Color(20, 20, 20)); // 深黑
                    }
                    
                    String text = piece.getName();
                    FontMetrics fm = g2d.getFontMetrics();
                    int textX = x + (pieceDiameter - fm.stringWidth(text)) / 2;
                    int textY = y + (pieceDiameter - fm.getHeight()) / 2 + fm.getAscent() - 2;
                    
                    g2d.drawString(text, textX, textY);
                    
                    // 增加一點內圈裝飾線
                    g2d.setStroke(new BasicStroke(1));
                    g2d.drawOval(x + 5, y + 5, pieceDiameter - 10, pieceDiameter - 10);
                }
            }
        }
    }
    
    private void drawSelectionHighlight(Graphics2D g2d) {
        if (selectedR != -1 && selectedC != -1) {
            int x = selectedC * TILE_SIZE + MARGIN;
            int y = selectedR * TILE_SIZE + MARGIN;
            int size = TILE_SIZE;

            // 改用發光效果的綠色方框
            g2d.setColor(new Color(0, 255, 0, 200));
            g2d.setStroke(new BasicStroke(3));
            
            // 畫四個角落的框，比較有質感
            int len = 15; // 角落線長度
            int offset = size / 2;
            
            // 左上
            g2d.drawLine(x - offset, y - offset, x - offset + len, y - offset);
            g2d.drawLine(x - offset, y - offset, x - offset, y - offset + len);
            // 右上
            g2d.drawLine(x + offset, y - offset, x + offset - len, y - offset);
            g2d.drawLine(x + offset, y - offset, x + offset, y - offset + len);
            // 左下
            g2d.drawLine(x - offset, y + offset, x - offset + len, y + offset);
            g2d.drawLine(x - offset, y + offset, x - offset, y + offset - len);
            // 右下
            g2d.drawLine(x + offset, y + offset, x + offset - len, y + offset);
            g2d.drawLine(x + offset, y + offset, x + offset, y + offset - len);
        }
    }
    
    private void drawLastMoveHighlight(Graphics2D g2d) {
        g2d.setColor(new Color(255, 0, 0, 180));
        g2d.setStroke(new BasicStroke(2)); 
        
        int startR = board.getLastStartR();
        int startC = board.getLastStartC();
        int endR = board.getLastEndR();
        int endC = board.getLastEndC();
        
        if (startR != -1 && startC != -1) drawHighlightRect(g2d, startR, startC);
        if (endR != -1 && endC != -1) drawHighlightRect(g2d, endR, endC);
    }
    
    private void drawHighlightRect(Graphics2D g2d, int r, int c) {
        int x = c * TILE_SIZE + MARGIN;
        int y = r * TILE_SIZE + MARGIN;
        int size = TILE_SIZE;
        g2d.drawRect(x - size / 2 + 2, y - size / 2 + 2, size - 4, size - 4);
    }

    // --- Controller 介面方法 (完全保留，無需更改) ---
    
    public void setSelectedPosition(int r, int c) {
        this.selectedR = r;
        this.selectedC = c;
        repaint(); 
    }
    
    public int getRowFromY(int y) {
        int r = Math.round((float)(y - MARGIN) / TILE_SIZE);
        return (r >= 0 && r < Board.ROWS) ? r : -1;
    }
    
    public int getColFromX(int x) {
        int c = Math.round((float)(x - MARGIN) / TILE_SIZE);
        return (c >= 0 && c < Board.COLS) ? c : -1;
    }
}