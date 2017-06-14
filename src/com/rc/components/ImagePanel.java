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
    private Image image;

    public ImagePanel(Image image)
    {
        this.image = image;
    }

    public ImagePanel()
    {

    }

    public void setImage(Image image)
    {
        this.image = image;
    }


    @Override
    public void paint(Graphics g)
    {
        int width = this.getWidth(), height = this.getHeight();

        // 图像缩放到容器宽高
        ImageIcon imageIcon = new ImageIcon();
        imageIcon.setImage(image.getScaledInstance(width, height, Image.SCALE_SMOOTH));

        Graphics2D g2 = (Graphics2D) g;
        RoundRectangle2D rect = new RoundRectangle2D.Float(0, 0, width, height, 8, 8);
        path.append(rect, false);
        g2.setClip(path);
        g2.drawImage(imageIcon.getImage(), 0, 0, null);
        g2.dispose();
    }
}
