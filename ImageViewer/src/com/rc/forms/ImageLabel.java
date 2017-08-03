package com.rc.forms;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.font.LineMetrics;
import java.util.*;

/**
 * Created by song on 26/06/2017.
 */
public class ImageLabel extends JLabel
{
    private Image image;
    private Image lastImage;
    private int xDistance = 0;
    private int yDistance = 0;

    int x = -1;
    int y = -1;

    private boolean firstDraw = true;
    private boolean scaleImage = false;

    public ImageLabel()
    {
        setListeners();
    }

    private void setListeners()
    {
        addComponentListener(new ComponentAdapter()
        {
            @Override
            public void componentResized(ComponentEvent e)
            {
                firstDraw = true;
                repaint();
                super.componentResized(e);
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setFont(getFont());
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int currentWidth = image.getWidth(null);
        int currentHeight = image.getHeight(null);

        int width = getWidth();
        int height = getHeight();

        if (firstDraw)
        {
            // 图片于容器垂直居中
            x = (width - currentWidth) / 2;
            y = (height- currentHeight) / 2;
            firstDraw = false;
        }
        else if (scaleImage)
        {
            int lastWidth = 0;
            int lastHeight = 0;
            int xOffset = 0;
            int yOffset = 0;

            if (lastImage != null)
            {
                lastWidth = lastImage.getWidth(null);
                lastHeight = lastImage.getHeight(null);
                xOffset = (lastWidth - currentWidth) / 2;
                yOffset = (lastHeight - currentHeight) / 2;
            }

            // 图片于容器垂直居中
            x += xOffset;
            y += yOffset;


            y = y < (height - currentHeight) ? (height - currentHeight) : y;
            x = x < (width - currentWidth) ? (width - currentWidth) : x;

            if (x > 0)
            {
                x = (width - currentWidth) / 2;
            }
            if (y > 0)
            {
                y = (height - currentHeight) / 2;
            }

            scaleImage = false;
        }
        else
        {


            if (currentWidth < width && currentHeight < height)
            {

            }
            else
            {
                // 移动图像
                x += xDistance;
                y += yDistance;

                y = height - y > currentHeight ? height - currentHeight : y;
                x = width - x > currentWidth ? width - currentWidth : x;

                x = x > 0 ? 0 : x;
                y = y > 0 ? 0 : y;
            }

            //y = y < (height - currentHeight) ? y : ;
        }

        /*System.out.println("x = " + x + ", y = " + y + ",   width = " + getWidth() + ", height = " + getHeight()
                + ",   currentWidth = " + currentWidth + ", currentHeight = " + currentHeight
                + ", height - currentHeight = " + (height - currentHeight));*/

        g2d.drawImage(image, x, y, null);
        g2d.dispose();
    }

    public Image getImage()
    {
        return image;
    }

    public void setImage(Image image)
    {
        lastImage = this.image;
        this.image = image;
        firstDraw = true;
    }

    public void scaleImage(Image image)
    {
        lastImage = this.image;
        this.image = image;
        scaleImage = true;
        repaint();
    }

    public void moveImage(int xDistance, int yDistance)
    {
        this.xDistance = xDistance;
        this.yDistance = yDistance;

        /*if (x == 0 && y == 0)
        {
            return;
        }*/


        this.repaint();
    }


}
