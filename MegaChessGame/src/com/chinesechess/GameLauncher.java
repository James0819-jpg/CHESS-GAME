package com.chinesechess;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GameLauncher extends JFrame {

    private final Color WOOD_DARK = new Color(60, 30, 10);
    private final Color WOOD_LIGHT = new Color(100, 60, 30);

    public GameLauncher() {
        setTitle("象棋遊戲大廳 - 選擇模式");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        BackgroundPanel mainPanel = new BackgroundPanel();
        mainPanel.setLayout(new GridBagLayout());
        setContentPane(mainPanel);

        // 標題製作
        JLabel titleLabel = new JLabel("中國象棋系列");
        titleLabel.setFont(new Font("KaiTi", Font.BOLD, 48));
        titleLabel.setForeground(new Color(255, 215, 0));

        // 建立發光按鈕
        GlowButton btnAnqi = new GlowButton("進入 暗棋 (Dark Chess)");
        GlowButton btnXiangqi = new GlowButton("進入 象棋 (Standard)");
        GlowButton btnExit = new GlowButton("離開遊戲 (Exit)");

        // --- 按鈕邏輯修正 ---
        
// --- 按鈕邏輯修正 ---
        
        btnAnqi.addActionListener(e -> {
            // 彈出一個小選項詢問模式
            String[] options = {"單人模式 (對抗電腦)", "雙人模式 (好友對戰)"};
            int choice = JOptionPane.showOptionDialog(this, 
                    "請選擇暗棋遊戲模式：", 
                    "模式選擇", 
                    JOptionPane.DEFAULT_OPTION, 
                    JOptionPane.QUESTION_MESSAGE, 
                    null, options, options[0]);
            
            if (choice == 0) { // 單人
                new f0.Fin(true); 
                this.dispose();
            } else if (choice == 1) { // 雙人
                new f0.Fin(false);
                this.dispose();
            }
            // 如果點擊取消或關閉，則留在選單畫面
        });

        btnXiangqi.addActionListener(e -> {
            // 直接呼叫 Main 裡面的啟動方法
            com.chinesechess.Main.launchGame(); 
            this.dispose(); 
        });

        btnExit.addActionListener(e -> System.exit(0));

        // --- 排版 ---
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 0, 15, 0);
        gbc.gridx = 0;
        
        gbc.gridy = 0; mainPanel.add(titleLabel, gbc);
        gbc.gridy = 1; mainPanel.add(btnAnqi, gbc);
        gbc.gridy = 2; mainPanel.add(btnXiangqi, gbc);
        gbc.gridy = 3; mainPanel.add(btnExit, gbc);
        
        setVisible(true);
    }

    // 內部類別：木紋背景
    private class BackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setPaint(new GradientPaint(0, 0, WOOD_LIGHT, 0, getHeight(), WOOD_DARK));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    // 內部類別：發光按鈕
    private static class GlowButton extends JButton {
        private boolean isHovering = false;
        public GlowButton(String text) {
            super(text);
            setFont(new Font("Microsoft JhengHei", Font.BOLD, 22));
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setPreferredSize(new Dimension(320, 60));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { isHovering = true; repaint(); }
                public void mouseExited(MouseEvent e) { isHovering = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (isHovering) {
                // 繪製發光感
                g2.setColor(new Color(255, 215, 0, 80));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(Color.WHITE);
            } else {
                g2.setColor(new Color(250, 240, 220));
            }
            
            g2.fillRoundRect(5, 5, getWidth()-10, getHeight()-10, 20, 20);
            g2.setColor(new Color(60, 30, 10)); // 按鈕邊框
            g2.drawRoundRect(5, 5, getWidth()-10, getHeight()-10, 20, 20);
            
            super.paintComponent(g);
        }
    }
}