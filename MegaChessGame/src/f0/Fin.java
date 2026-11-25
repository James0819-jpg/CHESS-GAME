package f0;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Fin extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final int ROWS = 4;
    private static final int COLS = 8;
    private DarkPiece[][] board = new DarkPiece[ROWS][COLS];
    private RoundButton[][] buttons = new RoundButton[ROWS][COLS];
    
    private ColorType currentTurn = ColorType.RED;
    private ColorType playerColor = ColorType.NONE;
    private Point selected = null;
    private boolean isSinglePlayer;
    private boolean isComputerThinking = false;
    
    private JLabel statusLabel;
    private Timer msgTimer;

    private List<DarkPiece> capturedByRed = new ArrayList<>();
    private List<DarkPiece> capturedByBlack = new ArrayList<>();
    private JPanel redCapturedPanel;
    private JPanel blackCapturedPanel;
    private JButton surrenderBtn;

    // 顏色定義
    private final Color COLOR_HIDDEN = new Color(80, 40, 10); // 配合新棋子邏輯，這個只要夠深即可
    private final Color COLOR_PIECE_BG = new Color(255, 250, 240); // 棋子正面底色(不會直接用到，因為RoundButton重寫了)
    private final Color COLOR_SELECTED = new Color(255, 215, 0);
    
    // 背景改為深木紋基底
    private final Color WOOD_DARK = new Color(60, 30, 10);
    private final Color WOOD_LIGHT = new Color(100, 60, 30);

    public Fin(boolean isSinglePlayer) {
        this.isSinglePlayer = isSinglePlayer;
        setTitle("Java 暗棋 - " + (isSinglePlayer ? "單人模式" : "雙人模式"));
        setSize(1100, 650); // 稍微加高一點
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // 設定主視窗背景
        getContentPane().setBackground(WOOD_DARK);

        initGame();
        initGUI();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initGame() {
        capturedByRed.clear();
        capturedByBlack.clear();

        ArrayList<DarkPiece> pieces = new ArrayList<>();
        pieces.add(new DarkPiece(ColorType.RED, "帥", 7));
        for(int i=0; i<2; i++) pieces.add(new DarkPiece(ColorType.RED, "仕", 6));
        for(int i=0; i<2; i++) pieces.add(new DarkPiece(ColorType.RED, "相", 5));
        for(int i=0; i<2; i++) pieces.add(new DarkPiece(ColorType.RED, "俥", 4));
        for(int i=0; i<2; i++) pieces.add(new DarkPiece(ColorType.RED, "傌", 3));
        for(int i=0; i<2; i++) pieces.add(new DarkPiece(ColorType.RED, "炮", 2));
        for(int i=0; i<5; i++) pieces.add(new DarkPiece(ColorType.RED, "兵", 1));
        
        pieces.add(new DarkPiece(ColorType.BLACK, "將", 7));
        for(int i=0; i<2; i++) pieces.add(new DarkPiece(ColorType.BLACK, "士", 6));
        for(int i=0; i<2; i++) pieces.add(new DarkPiece(ColorType.BLACK, "象", 5));
        for(int i=0; i<2; i++) pieces.add(new DarkPiece(ColorType.BLACK, "車", 4));
        for(int i=0; i<2; i++) pieces.add(new DarkPiece(ColorType.BLACK, "馬", 3));
        for(int i=0; i<2; i++) pieces.add(new DarkPiece(ColorType.BLACK, "包", 2));
        for(int i=0; i<5; i++) pieces.add(new DarkPiece(ColorType.BLACK, "卒", 1));

        Collections.shuffle(pieces);
        int index = 0;
        for (int r = 0; r < ROWS; r++) for (int c = 0; c < COLS; c++) board[r][c] = pieces.get(index++);
    }

    // ★★★ 美化版棋盤面板 ★★★
    private class BoardPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        public BoardPanel(LayoutManager layout) {
            super(layout);
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // 內縮
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            int w = getWidth();
            int h = getHeight();

            // 1. 畫背景 (木紋漸層)
            GradientPaint gp = new GradientPaint(0, 0, WOOD_LIGHT, w, h, WOOD_DARK);
            g2.setPaint(gp);
            g2.fillRect(0, 0, w, h);

            // 2. 畫格線 (凹槽效果)
            // 先畫亮線(右下偏移)，再畫暗線(左上)，製造雕刻感
            int cellW = (w - 20) / COLS; // 扣除 padding
            int cellH = (h - 20) / ROWS;
            int startX = 10;
            int startY = 10;

            g2.setStroke(new BasicStroke(2));
            
            // 畫垂直線
            for (int c = 1; c < COLS; c++) {
                int x = startX + c * cellW;
                g2.setColor(new Color(255, 255, 255, 30)); // 亮邊
                g2.drawLine(x + 1, startY, x + 1, h - startY);
                g2.setColor(new Color(0, 0, 0, 150)); // 暗邊
                g2.drawLine(x, startY, x, h - startY);
            }
            // 畫水平線
            for (int r = 1; r < ROWS; r++) {
                int y = startY + r * cellH;
                g2.setColor(new Color(255, 255, 255, 30)); 
                g2.drawLine(startX, y + 1, w - startX, y + 1);
                g2.setColor(new Color(0, 0, 0, 150)); 
                g2.drawLine(startX, y, w - startX, y);
            }
            
            // 畫外框
            g2.setStroke(new BasicStroke(5));
            g2.setColor(new Color(40, 20, 5));
            g2.drawRect(5, 5, w - 10, h - 10);
        }
    }

    private void initGUI() {
        // 使用新的美化面板
        BoardPanel boardPanel = new BoardPanel(new GridLayout(ROWS, COLS, 4, 4)); // 間距稍微大一點點

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                RoundButton btn = new RoundButton();
                // 調整字體，更像書法
                btn.setFont(new Font("KaiTi", Font.BOLD, 38)); 
                final int row = r;
                final int col = c;
                btn.addActionListener(e -> handleClick(row, col));
                buttons[r][c] = btn;
                boardPanel.add(btn);
            }
        }
        add(boardPanel, BorderLayout.CENTER);

        // 兩側面板背景色調整
        redCapturedPanel = createCapturedPanel("紅方戰利品 (黑棋)");
        add(redCapturedPanel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(WOOD_DARK); // 背景一致
        blackCapturedPanel = createCapturedPanel("黑方戰利品 (紅棋)");
        rightPanel.add(blackCapturedPanel, BorderLayout.CENTER);
        
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(WOOD_DARK);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        surrenderBtn = new JButton("投降 / 認輸");
        surrenderBtn.setFont(new Font("Microsoft JhengHei", Font.BOLD, 16));
        surrenderBtn.setBackground(new Color(180, 60, 60)); // 深紅質感
        surrenderBtn.setForeground(Color.WHITE);
        surrenderBtn.setFocusable(false);
        surrenderBtn.setPreferredSize(new Dimension(140, 45));
        surrenderBtn.setBorder(BorderFactory.createBevelBorder(0)); // 浮雕按鈕
        surrenderBtn.addActionListener(e -> handleSurrender());
        
        btnPanel.add(surrenderBtn);
        rightPanel.add(btnPanel, BorderLayout.SOUTH);
        add(rightPanel, BorderLayout.EAST);

        statusLabel = new JLabel("歡迎！" + (isSinglePlayer ? "你先手，請翻牌" : "請翻牌決定顏色"), SwingConstants.CENTER);
        statusLabel.setFont(new Font("Microsoft JhengHei", Font.BOLD, 22));
        statusLabel.setForeground(new Color(255, 255, 200)); // 淡黃色字體
        statusLabel.setBorder(BorderFactory.createEmptyBorder(15,0,15,0));
        statusLabel.setOpaque(true);
        statusLabel.setBackground(new Color(40, 20, 5)); // 很深的底色
        add(statusLabel, BorderLayout.SOUTH);
        
        refreshView();
        updateCapturedGui();
    }

    private void handleSurrender() {
        if (playerColor == ColorType.NONE) {
            JOptionPane.showMessageDialog(this, "遊戲尚未開始，請先翻牌！");
            return;
        }
        if (isSinglePlayer && isComputerThinking) return;

        int confirm = JOptionPane.showConfirmDialog(this, 
                "確定要投降認輸嗎？", "投降確認", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String winner = isSinglePlayer ? "電腦 (對手)" : ((currentTurn == ColorType.RED) ? "黑方" : "紅方");
            JOptionPane.showMessageDialog(this, "投降！ " + winner + " 獲勝！");
            dispose();
        }
    }

    private JPanel createCapturedPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 8));
        panel.setPreferredSize(new Dimension(180, 0)); // 稍微加寬
        panel.setBackground(new Color(60, 30, 10)); // 木頭深色
        
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(150, 100, 50), 1), 
                title);
        border.setTitleColor(new Color(220, 200, 160)); // 淺木色字體
        border.setTitleFont(new Font("Microsoft JhengHei", Font.BOLD, 16));
        panel.setBorder(border);
        return panel;
    }

    private void handleClick(int r, int c) {
        if (isSinglePlayer && playerColor != ColorType.NONE && currentTurn != playerColor) return;
        if (isComputerThinking) return;

        DarkPiece p = board[r][c];
        
        if (p != null && !p.isFaceUp) {
            if (selected != null) {
                buttons[r][c].showMyError();
                showErrorText("不能吃蓋著的棋子！");
                return;
            }
            p.isFaceUp = true;
            if (playerColor == ColorType.NONE) {
                playerColor = p.color;
                currentTurn = playerColor;
                toggleTurn(); 
            } else {
                toggleTurn();
            }
            refreshView();
            checkWinCondition();
            return;
        }

        if (p != null && p.isFaceUp) {
            if (selected == null) {
                if (p.color == currentTurn) {
                    selected = new Point(r, c);
                    refreshView();
                } else {
                    buttons[r][c].showMyError();
                    showErrorText("這不是你的棋子！");
                }
                return;
            }
            if (p.color == currentTurn) {
                if (selected.x == r && selected.y == c) selected = null;
                else selected = new Point(r, c);
                refreshView();
                return;
            }
        }

        if (selected != null) {
            if (isValidMove(selected.x, selected.y, r, c)) {
                executeMove(selected.x, selected.y, r, c);
                selected = null;
                toggleTurn();
                refreshView();
                checkWinCondition();
            } else {
                buttons[r][c].showMyError();
                showErrorText("移動不符合規則！");
            }
        }
    }

    private void toggleTurn() {
        currentTurn = (currentTurn == ColorType.RED) ? ColorType.BLACK : ColorType.RED;
        if (isSinglePlayer && currentTurn != playerColor) {
            isComputerThinking = true;
            statusLabel.setText("電腦思考中...");
            Timer aiTimer = new Timer(1000, e -> {
                computerMove();
                isComputerThinking = false;
                refreshView();
                checkWinCondition();
                ((Timer)e.getSource()).stop();
            });
            aiTimer.setRepeats(false);
            aiTimer.start();
        }
    }

    private void computerMove() {
        if (checkAiSurrender()) {
            JOptionPane.showMessageDialog(this, "電腦：可惡...我看不到贏的希望，我投降了！");
            dispose();
            return;
        }

        List<int[]> eatMoves = new ArrayList<>();
        List<Point> flipMoves = new ArrayList<>();
        List<int[]> walkMoves = new ArrayList<>();

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                DarkPiece p = board[r][c];
                if (p != null && !p.isFaceUp) {
                    flipMoves.add(new Point(r, c));
                    continue;
                }
                if (p != null && p.isFaceUp && p.color == currentTurn) {
                    for (int tr = 0; tr < ROWS; tr++) {
                        for (int tc = 0; tc < COLS; tc++) {
                            if (isValidMove(r, c, tr, tc)) {
                                DarkPiece target = board[tr][tc];
                                if (target != null) {
                                    eatMoves.add(new int[]{r, c, tr, tc, target.rank});
                                } else {
                                    walkMoves.add(new int[]{r, c, tr, tc});
                                }
                            }
                        }
                    }
                }
            }
        }

        if (!eatMoves.isEmpty()) {
            eatMoves.sort((a, b) -> b[4] - a[4]);
            int[] bestMove = eatMoves.get(0);
            executeMove(bestMove[0], bestMove[1], bestMove[2], bestMove[3]);
            showErrorText("電腦吃了你的棋子！");
            currentTurn = playerColor;
            return;
        }

        if (!flipMoves.isEmpty()) {
            Random rand = new Random();
            Point flip = flipMoves.get(rand.nextInt(flipMoves.size()));
            DarkPiece p = board[flip.x][flip.y];
            p.isFaceUp = true;
            currentTurn = playerColor; 
            return;
        }

        if (!walkMoves.isEmpty()) {
            Random rand = new Random();
            int[] move = walkMoves.get(rand.nextInt(walkMoves.size()));
            executeMove(move[0], move[1], move[2], move[3]);
            currentTurn = playerColor;
            return;
        }
        currentTurn = playerColor;
    }

    private boolean checkAiSurrender() {
        int aiPower = 0;
        int playerPower = 0;
        int hiddenCount = 0;

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                DarkPiece p = board[r][c];
                if (p != null) {
                    if (!p.isFaceUp) {
                        hiddenCount++;
                    } else {
                        if (p.color == currentTurn) aiPower += p.rank;
                        else playerPower += p.rank;
                    }
                }
            }
        }
        return (hiddenCount == 0 && aiPower * 3 < playerPower && playerPower > 10);
    }

    private void executeMove(int fr, int fc, int tr, int tc) {
        DarkPiece target = board[tr][tc];
        if (target != null) {
            if (target.color == ColorType.RED) capturedByBlack.add(target);
            else capturedByRed.add(target);
            updateCapturedGui();
        }
        board[tr][tc] = board[fr][fc];
        board[fr][fc] = null;
    }

    private void updateCapturedGui() {
        fillCapturedPanel(redCapturedPanel, capturedByRed);
        fillCapturedPanel(blackCapturedPanel, capturedByBlack);
    }

    private void fillCapturedPanel(JPanel panel, List<DarkPiece> pieces) {
        panel.removeAll();
        pieces.sort((p1, p2) -> p2.rank - p1.rank);
        for (DarkPiece p : pieces) {
            RoundButton btn = new RoundButton();
            btn.setText(p.name);
            btn.setFont(new Font("KaiTi", Font.PLAIN, 20));
            // 這裡設定戰利品的縮圖為平面顏色即可，不需要3D球體效果，避免太搶眼
            // 但為了方便，我們直接用 RoundButton 的邏輯
            btn.setBackground(new Color(245, 240, 230)); 
            if (p.color == ColorType.RED) btn.setForeground(new Color(200, 0, 0));
            else btn.setForeground(Color.BLACK);
            btn.setPreferredSize(new Dimension(40, 40));
            btn.setEnabled(false);
            panel.add(btn);
        }
        panel.revalidate();
        panel.repaint();
    }

    private void showErrorText(String msg) {
        if (msgTimer != null && msgTimer.isRunning()) msgTimer.stop();
        statusLabel.setText(msg);
        statusLabel.setForeground(new Color(255, 100, 100)); // 亮紅色
        msgTimer = new Timer(1500, e -> refreshView());
        msgTimer.setRepeats(false);
        msgTimer.start();
    }

    private boolean isValidMove(int fromR, int fromC, int toR, int toC) {
        DarkPiece attacker = board[fromR][fromC];
        DarkPiece target = board[toR][toC];
        int dist = Math.abs(fromR - toR) + Math.abs(fromC - toC);

        if (target != null && target.color == attacker.color) return false;
        if (target != null && !target.isFaceUp) return false;

        if (attacker.rank == 2) { 
            if (target == null) return dist == 1;
            if (fromR != toR && fromC != toC) return false;
            int obstacles = countObstacles(fromR, fromC, toR, toC);
            return obstacles == 1;
        }
        
        if (dist != 1) return false;
        if (target == null) return true;
        if (attacker.rank == 1 && target.rank == 7) return true;
        if (attacker.rank == 7 && target.rank == 1) return false;
        return attacker.rank >= target.rank;
    }

    private int countObstacles(int r1, int c1, int r2, int c2) {
        int count = 0;
        if (r1 == r2) {
            int min = Math.min(c1, c2);
            int max = Math.max(c1, c2);
            for (int i = min + 1; i < max; i++) if (board[r1][i] != null) count++;
        } else {
            int min = Math.min(r1, r2);
            int max = Math.max(r1, r2);
            for (int i = min + 1; i < max; i++) if (board[i][c1] != null) count++;
        }
        return count;
    }

    private void checkWinCondition() {
        boolean redAlive = false; boolean blackAlive = false; boolean unrevealed = false;
        for (int r = 0; r < ROWS; r++) for (int c = 0; c < COLS; c++) {
            DarkPiece p = board[r][c];
            if (p != null) {
                if (!p.isFaceUp) unrevealed = true;
                else if (p.color == ColorType.RED) redAlive = true;
                else if (p.color == ColorType.BLACK) blackAlive = true;
            }
        }
        if (!unrevealed) {
            if (!redAlive) { JOptionPane.showMessageDialog(this, "黑方獲勝！"); dispose(); }
            else if (!blackAlive) { JOptionPane.showMessageDialog(this, "紅方獲勝！"); dispose(); }
        }
    }

    private void refreshView() {
        if (!isComputerThinking) {
            String turnStr = (currentTurn == ColorType.RED) ? "紅方" : "黑方";
            String who = "";
            if (playerColor != ColorType.NONE) {
                if (currentTurn == playerColor) who = " (你)";
                else if (isSinglePlayer) who = " (電腦)";
                else who = " (對手)";
            }
            if (playerColor == ColorType.NONE) statusLabel.setText("請翻牌決定先後手");
            else statusLabel.setText("輪到: " + turnStr + who);
            statusLabel.setForeground(new Color(255, 255, 200));
        }

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                DarkPiece p = board[r][c];
                RoundButton btn = buttons[r][c];
                
                // 清除所有背景色，由 RoundButton 自己的 paintComponent 決定顏色
                btn.setBackground(new Color(0,0,0,0)); // 設為透明，讓 BoardPanel 的木紋透出來(空地時)

                if (p == null) {
                    btn.setText("");
                    btn.setEnabled(true);
                    // 這裡設一個特殊的顏色標記為"空地"，讓 RoundButton 知道不要畫
                    btn.setBackground(new Color(255,255,255)); 
                } else if (!p.isFaceUp) {
                    btn.setText(""); 
                    btn.setBackground(COLOR_HIDDEN);
                } else {
                    btn.setText(p.name);
                    btn.setBackground(COLOR_PIECE_BG); // 正面底色
                    if (p.color == ColorType.RED) btn.setForeground(new Color(220, 0, 0)); // 亮紅
                    else btn.setForeground(Color.BLACK);
                }
                
                if (selected != null && selected.x == r && selected.y == c) {
                    btn.setBackground(COLOR_SELECTED); // 選取時會蓋過上面的設定
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame());
    }
}