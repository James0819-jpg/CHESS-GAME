package com.chinesechess.model;

public class Board {
    public static final int ROWS = 10;
    public static final int COLS = 9;

    private Piece[][] board = new Piece[ROWS][COLS];
    private int currentPlayer = Piece.RED; // 0:紅, 1:黑

    // 儲存上一步的座標，用於 View 層高亮顯示
    private int lastStartR = -1;
    private int lastStartC = -1;
    private int lastEndR = -1;
    private int lastEndC = -1;

    public Board() {
        initializeBoard();
    }

    /**
     * 初始化棋盤，將所有棋子放置到初始位置。
     */
    private void initializeBoard() {
        // 假設 Piece.RED=0, Piece.BLACK=1
        int R = Piece.BLACK;   // 紅方
        int B = Piece.RED; // 黑方

        // 紅方 (RED, 底部 0-4 行)
        board[0][0] = new com.chinesechess.model.Chariot(R);
        board[0][1] = new com.chinesechess.model.Horse(R);
        board[0][2] = new com.chinesechess.model.Elephant(R);
        board[0][3] = new com.chinesechess.model.Advisor(R);
        board[0][4] = new com.chinesechess.model.General(R);
        board[0][5] = new com.chinesechess.model.Advisor(R);
        board[0][6] = new com.chinesechess.model.Elephant(R);
        board[0][7] = new com.chinesechess.model.Horse(R);
        board[0][8] = new com.chinesechess.model.Chariot(R);
        
        board[2][1] = new com.chinesechess.model.Cannon(R);
        board[2][7] = new com.chinesechess.model.Cannon(R);

        for (int c = 0; c < COLS; c += 2) {
            board[3][c] = new com.chinesechess.model.Soldier(R);
        }

        // 黑方 (BLACK, 頂部 5-9 行)
        board[9][0] = new com.chinesechess.model.Chariot(B);
        board[9][1] = new com.chinesechess.model.Horse(B);
        board[9][2] = new com.chinesechess.model.Elephant(B);
        board[9][3] = new com.chinesechess.model.Advisor(B);
        board[9][4] = new com.chinesechess.model.General(B);
        board[9][5] = new com.chinesechess.model.Advisor(B);
        board[9][6] = new com.chinesechess.model.Elephant(B);
        board[9][7] = new com.chinesechess.model.Horse(B);
        board[9][8] = new com.chinesechess.model.Chariot(B);

        board[7][1] = new com.chinesechess.model.Cannon(B);
        board[7][7] = new com.chinesechess.model.Cannon(B);
        
        for (int c = 0; c < COLS; c += 2) {
            board[6][c] = new com.chinesechess.model.Soldier(B);
        }
    }

    public Piece getPiece(int r, int c) {
        if (r >= 0 && r < ROWS && c >= 0 && c < COLS) {
            return board[r][c];
        }
        return null;
    }

    public boolean movePiece(int startR, int startC, int endR, int endC) {
        Piece piece = getPiece(startR, startC);
        
        if (piece == null || piece.getColor() != currentPlayer) {
            return false;
        }

        // 核心規則檢查
        if (piece.isValidMove(this, startR, startC, endR, endC)) {
            
            // 🚨 這是最關鍵的一步：在執行移動前，先檢查這一步是否會導致我方被將軍 (反將)
            // 執行臨時移動
            Piece capturedPiece = board[endR][endC];
            board[endR][endC] = piece;
            board[startR][startC] = null;
            
            boolean selfInCheck = isCheck(currentPlayer);
            
            // 撤銷臨時移動
            board[startR][startC] = piece;
            board[endR][endC] = capturedPiece;

            if (selfInCheck) {
                // 如果移動後我方被將軍，則移動非法
                return false;
            }

            // --- 正式執行移動 ---
            board[endR][endC] = piece;
            board[startR][startC] = null;
            
            // 更新最近移動的座標
            this.lastStartR = startR;
            this.lastStartC = startC;
            this.lastEndR = endR;
            this.lastEndC = endC;
            
            // 切換玩家
            currentPlayer = 1 - currentPlayer;
            return true;
        }
        return false;
    }

    /**
     * 檢查指定顏色方的將/帥是否被對方將軍。
     * 這是 Minimax 成功運作的關鍵！
     * @param color 要檢查的顏色 (Piece.RED 或 Piece.BLACK)
     * @return 如果被將軍，返回 true
     */
    public boolean isCheck(int color) {
        int opponentColor = 1 - color;
        int generalR = -1, generalC = -1;

        // 1. 找到我方的將/帥的位置
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Piece p = getPiece(r, c);
                // 使用 instanceof 來判斷是否為 General 類型的實例
                if (p != null && p.getColor() == color && p instanceof com.chinesechess.model.General) {
                    generalR = r;
                    generalC = c;
                    break;
                }
            }
            if (generalR != -1) break;
        }
        
        // 如果將/帥已經被吃掉，則不被將軍 (遊戲已結束)
        if (generalR == -1) return false;

        // 2. 遍歷對方的所有棋子，檢查它們能否走到我方將/帥的位置
        for (int startR = 0; startR < ROWS; startR++) {
            for (int startC = 0; startC < COLS; startC++) {
                Piece opponentPiece = getPiece(startR, startC);
                
                if (opponentPiece != null && opponentPiece.getColor() == opponentColor) {
                    // 檢查對方的棋子能否走到我方將/帥的位置
                    if (opponentPiece.isValidMove(this, startR, startC, generalR, generalC)) {
                        return true; // 發現一個將軍的威脅
                    }
                }
            }
        }
        
        // 3. 檢查「飛將」規則 (兩將/帥對面，中間沒有棋子)
        int opponentGeneralR = -1, opponentGeneralC = -1;
        // 遍歷找到對方的將/帥
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Piece p = getPiece(r, c);
                if (p != null && p.getColor() == opponentColor && p instanceof com.chinesechess.model.General) {
                    opponentGeneralR = r;
                    opponentGeneralC = c;
                    break;
                }
            }
            if (opponentGeneralR != -1) break;
        }

        // 只有當將/帥在同一列時才檢查飛將
        if (generalC == opponentGeneralC) {
            int piecesInBetween = 0;
            int minR = Math.min(generalR, opponentGeneralR);
            int maxR = Math.max(generalR, opponentGeneralR);
            
            // 計算兩個將/帥之間有多少個棋子
            for (int r = minR + 1; r < maxR; r++) {
                if (getPiece(r, generalC) != null) {
                    piecesInBetween++;
                }
            }

            if (piecesInBetween == 0) {
                return true; // 飛將成立
            }
        }
        
        return false;
    }

    /**
     * 檢查指定顏色方的將/帥是否還在棋盤上。
     */
    public boolean isGeneralMissing(int color) {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Piece p = getPiece(r, c);
                if (p != null && p.getColor() == color && p instanceof com.chinesechess.model.General) {
                    return false; // 找到了該顏色的將/帥
                }
            }
        }
        return true; 
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    // 🚨 這是提供給 AI 使用的核心數據
    public Piece[][] getBoardData() {
        return board;
    }

    // Getter 方法：用於 View 層讀取最近移動的座標
    public int getLastStartR() { return lastStartR; }
    public int getLastStartC() { return lastStartC; }
    public int getLastEndR() { return lastEndR; }
    public int getLastEndC() { return lastEndC; }
}