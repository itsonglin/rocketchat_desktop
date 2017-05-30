package com.rc.components;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Created by song on 17-5-29.
 */
public class ImagePanel extends JPanel
{
    private GeneralPath path = new GeneralPath();
    private BufferedImage image;

    public ImagePanel(BufferedImage image)
    {
        this.image = image;
        addComponentListener(new ComponentAdapter()
        {
            @Override
            public void componentResized(ComponentEvent e)
            {
            }
        });
    }

    @Override
    public void paint(Graphics g)
    {
        int width = this.getWidth(), height = this.getHeight();
       // width = 50;
       // height = 50;

        System.out.println(width + ", " + height);
        Image scaledImage = image.getScaledInstance(width, width, image.SCALE_SMOOTH);//设置缩放目标图片模板

        Graphics2D g2 = (Graphics2D) g;
        RoundRectangle2D rect = new RoundRectangle2D.Double(0, 0, width, height, 8, 8);
        path.append(rect, false);
        g2.setClip(path);
        g2.drawImage(scaledImage, 0, 0, null);
        g2.dispose();
/*                g2.setPaint(new GradientPaint(0.0F, 0.0F, Colors.DARK, 0.0F,
                    height, Colors.DARK, true));
                g2.drawRoundRect(0, 0, width - 1, height - 1, 10, 10);
                g2.drawRoundRect(1, 1, width - 2, height - 2, 8, 8);*/

    }
}
