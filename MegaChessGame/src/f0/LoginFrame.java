package f0;

import javax.swing.*;
import java.awt.*;
// 引入你的遊戲大廳，假設它在 com.chinesechess 或 com.launcher
// 如果你放在同一個 package 就不用這行
import com.chinesechess.GameLauncher; 

public class LoginFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private AccountManager manager = new AccountManager();
    private JTextField userField;
    private JPasswordField passField;

    public LoginFrame() {
        setTitle("遊戲系統登入");
        setSize(300, 220); // 稍微調高一點，避免元件擠在一起
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 1, 5, 5)); // 增加間距

        JPanel row1 = new JPanel(); 
        row1.add(new JLabel("帳號:")); 
        userField = new JTextField(15); 
        row1.add(userField);

        JPanel row2 = new JPanel(); 
        row2.add(new JLabel("密碼:")); 
        passField = new JPasswordField(15); 
        row2.add(passField);

        JPanel row3 = new JPanel(); 
        JButton loginBtn = new JButton("登入"); 
        JButton regBtn = new JButton("註冊"); 
        row3.add(loginBtn); 
        row3.add(regBtn);
        
        JLabel msgLabel = new JLabel("請輸入帳號密碼", SwingConstants.CENTER);

        add(row1); add(row2); add(row3); add(msgLabel);
        
        this.getRootPane().setDefaultButton(loginBtn);
        userField.addActionListener(e -> passField.requestFocusInWindow());

        // ★★★ 登入按鈕邏輯：指向新的 GameLauncher ★★★
        loginBtn.addActionListener(e -> {
            String u = userField.getText();
            String p = new String(passField.getPassword());
            if (manager.login(u, p)) {
                dispose(); // 關閉登入視窗
                
                // 這裡開啟那個有發光按鈕的畫面
                // 如果你的 GameLauncher 放在 com.chinesechess 裡面：
                new com.chinesechess.GameLauncher(); 
                
                // 如果你想直接進象棋遊戲(不選模式)，就改成：
                // com.chinesechess.Main.launchGame();
                
            } else {
                msgLabel.setText("帳號或密碼錯誤！");
                msgLabel.setForeground(Color.RED);
            }
        });

        regBtn.addActionListener(e -> {
            String u = userField.getText();
            String p = new String(passField.getPassword());
            if (!u.isEmpty() && !p.isEmpty() && manager.register(u, p)) {
                msgLabel.setText("註冊成功！請登入");
                msgLabel.setForeground(new Color(0, 100, 0)); // 深綠色
            } else {
                msgLabel.setText("註冊失敗或帳號已存在");
                msgLabel.setForeground(Color.RED);
            }
        });
        
        setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame());
    }
}