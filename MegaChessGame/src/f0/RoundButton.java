package f0;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

public class RoundButton extends JButton {
    private static final long serialVersionUID = 1L;
    private boolean errorState = false;

    // 定義棋子配色 (仿象牙/玉石質感)
    private final Color PIECE_COLOR_START = new Color(255, 250, 240); // 中心亮點
    private final Color PIECE_COLOR_END = new Color(220, 200, 160);   // 邊緣陰影
    private final Color BACK_COLOR_START = new Color(160, 82, 45);    // 背面中心 (木頭色)
    private final Color BACK_COLOR_END = new Color(100, 40, 10);      // 背面邊緣

    public RoundButton() {
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setFocusable(false);
    }

    public void showMyError() {
        errorState = true;
        repaint();
        Timer t = new Timer(500, e -> {
            errorState = false;
            repaint();
            ((Timer)e.getSource()).stop();
        });
        t.setRepeats(false);
        t.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        // 開啟最高畫質反鋸齒
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int diameter = Math.min(width, height) - 6; // 留多一點邊距畫光暈
        int x = (width - diameter) / 2;
        int y = (height - diameter) / 2;

        // --- 1. 繪製棋子本體 (立體漸層) ---
        if (!getText().isEmpty()) {
            // 正面 (翻開) - 放射狀漸層 (RadialGradient) 模擬球體反光
            float radius = diameter / 2f;
            float[] dist = {0.1f, 1.0f};
            Color[] colors = {PIECE_COLOR_START, PIECE_COLOR_END};
            RadialGradientPaint p = new RadialGradientPaint(
                new Point(x + diameter/3, y + diameter/3), // 光源在左上角
                radius, 
                new Point(x + diameter/2, y + diameter/2),
                dist, colors, RadialGradientPaint.CycleMethod.NO_CYCLE);
            g2.setPaint(p);
        } else {
            // 背面 (蓋著) - 深色木紋質感
            if (getBackground().getRed() < 100) { // 判斷是否為隱藏狀態的顏色
                float radius = diameter / 2f;
                float[] dist = {0.0f, 1.0f};
                Color[] colors = {BACK_COLOR_START, BACK_COLOR_END};
                RadialGradientPaint p = new RadialGradientPaint(
                    new Point(x + diameter/2, y + diameter/2), 
                    radius, dist, colors);
                g2.setPaint(p);
            } else {
                // 空地 (不畫)
                return;
            }
        }
        g2.fillOval(x, y, diameter, diameter);

        // --- 2. 繪製邊框 (立體浮雕感) ---
        if (!getText().isEmpty() || getBackground().getRed() < 100) {
            g2.setStroke(new BasicStroke(2));
            g2.setColor(new Color(255, 255, 255, 100)); // 上半部亮光
            g2.drawArc(x, y, diameter, diameter, 45, 180);
            
            g2.setColor(new Color(0, 0, 0, 50)); // 下半部陰影
            g2.drawArc(x, y, diameter, diameter, 225, 180);
            
            // 外圈深色線條
            g2.setColor(new Color(80, 40, 0, 150));
            g2.setStroke(new BasicStroke(1));
            g2.drawOval(x, y, diameter, diameter);
            
            // 內圈裝飾線
            if (!getText().isEmpty()) {
                g2.setColor(new Color(180, 160, 120));
                g2.drawOval(x + 5, y + 5, diameter - 10, diameter - 10);
            }
        }

        // --- 3. 繪製選取光圈 (高亮顯示) ---
        // 透過檢查背景色是否為黃色來判斷是否被選取
        if (getBackground().equals(new Color(255, 215, 0))) { // COLOR_SELECTED
            g2.setColor(new Color(255, 215, 0, 200)); // 金色光圈
            g2.setStroke(new BasicStroke(4));
            g2.drawOval(x - 2, y - 2, diameter + 4, diameter + 4);
        }

        // --- 4. 繪製文字 (刻痕效果) ---
        if (getText() != null && !getText().isEmpty()) {
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            Rectangle stringBounds = fm.getStringBounds(getText(), g2).getBounds();
            int textX = (width - stringBounds.width) / 2;
            int textY = (height - stringBounds.height) / 2 + fm.getAscent();

            // 畫文字陰影 (淺色在右下，製造凹陷感)
            g2.setColor(new Color(255, 255, 255, 180));
            g2.drawString(getText(), textX + 1, textY + 1);

            // 畫文字本體
            g2.setColor(getForeground());
            g2.drawString(getText(), textX, textY);
        }

        // --- 5. 錯誤紅叉叉 ---
        if (errorState) {
            g2.setColor(new Color(220, 0, 0));
            g2.setStroke(new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(x + 15, y + 15, x + diameter - 15, y + diameter - 15);
            g2.drawLine(x + diameter - 15, y + 15, x + 15, y + diameter - 15);
        }
    }
    
    @Override
    public boolean contains(int x, int y) {
        Shape shape = new Ellipse2D.Float(0, 0, getWidth(), getHeight());
        return shape.contains(x, y);
    }
}