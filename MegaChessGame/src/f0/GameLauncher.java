package f0; // ★ 確保這裡變成 package f0;

import javax.swing.*;
import java.awt.*;

public class GameLauncher extends JFrame {
    
    public GameLauncher() {
        setTitle("Java 棋類遊戲大廳");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 1, 10, 10)); 
        
        getContentPane().setBackground(new Color(60, 60, 60));

        JLabel titleLabel = new JLabel("請選擇遊戲", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Microsoft JhengHei", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        add(titleLabel);

        // --- 1. 標準象棋按鈕 ---
        JButton btnXiangqi = createStyledButton("標準中國象棋 (Chinese Chess)");
        btnXiangqi.addActionListener(e -> {
            // 不關閉大廳，直接開啟象棋
            // 或者用 dispose() 關閉大廳也可以，看你需求
            // dispose(); 
            com.chinesechess.Main.main(new String[]{});
        });
        add(btnXiangqi);

        // --- 2. 暗棋按鈕 ---
        JButton btnDarkChess = createStyledButton("暗棋 (Dark Chess)");
        btnDarkChess.addActionListener(e -> {
            // ★★★ 點擊暗棋後，先詢問模式 (原本在登入後的邏輯移到這裡) ★★★
            showDarkChessModeSelection();
        });
        add(btnDarkChess);

        setVisible(true);
    }
    
    // ★★★ 暗棋模式選擇視窗 ★★★
    private void showDarkChessModeSelection() {
        String[] options = {"單人模式 (VS 電腦)", "雙人模式 (VS 朋友)"};
        int choice = JOptionPane.showOptionDialog(this, 
                "請選擇暗棋遊戲模式", 
                "模式選擇", 
                JOptionPane.DEFAULT_OPTION, 
                JOptionPane.INFORMATION_MESSAGE, 
                null, options, options[0]);
        
        // 如果使用者按了取消或關閉視窗，就不啟動遊戲
        if (choice != JOptionPane.CLOSED_OPTION) {
            boolean isSinglePlayer = (choice == 0);
            // dispose(); // 如果想要開啟遊戲後關閉選單，把這行註解拿掉
            new Fin(isSinglePlayer); // 啟動暗棋
        }
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Microsoft JhengHei", Font.BOLD, 18));
        btn.setFocusPainted(false);
        btn.setBackground(new Color(245, 222, 179)); 
        btn.setForeground(new Color(80, 40, 0));     
        return btn;
    }
}