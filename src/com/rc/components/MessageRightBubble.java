package com.rc.components;

import javax.swing.*;
import java.awt.*;

/**
 * Created by song on 17-6-2.
 */
public class MessageRightBubble extends JIMSendTextPane
{
    @Override
    public void paintComponent(Graphics g)
    {

        Graphics2D g2D = (Graphics2D) g;
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // 反锯齿平滑绘制


        // 绘制额消息气泡左边小箭头
        int xPoints[] = new int[3];
        int yPoints[] = new int[3];

        Point point = new Point(10, 10);

        int messagePaintWidth = 100;
        int messagePaintHeight = 40;

        Color selfMessageColor = new Color(43, 207, 79);
        Color selfMessageBorderColor = new Color(24, 161, 55);

        // 绘制自己消息圆角消息气泡矩形
        g2D.setColor(selfMessageColor);
        g2D.fillRoundRect(point.x - 7, point.y - 7, messagePaintWidth + 14, messagePaintHeight + 14, 10, 10);
        // 绘制圆角消息气泡边框
        g2D.setColor(selfMessageColor);
        g2D.drawRoundRect(point.x - 7, point.y - 7, messagePaintWidth + 14, messagePaintHeight + 14, 10, 10);

        // 消息发出者是自己，则头像靠右显示
        xPoints[0] = (point.x - 7) + (messagePaintWidth + 14);
        yPoints[0] = point.y;
        xPoints[1] = xPoints[0] + 7;
        yPoints[1] = point.y;
        xPoints[2] = xPoints[0];
        yPoints[2] = point.y + 7;

        g2D.setColor(selfMessageColor);
        g2D.fillPolygon(xPoints, yPoints, 3);
        g2D.setColor(selfMessageBorderColor);
        g2D.drawPolyline(xPoints, yPoints, 3);
        g2D.setColor(selfMessageColor);
        g2D.drawLine(xPoints[0], yPoints[0] + 1, xPoints[2], yPoints[2] - 1);

        super.paintComponent(g); // 执行默认组件绘制（消息文本、图片以及段落显示等内容）

    }
}
