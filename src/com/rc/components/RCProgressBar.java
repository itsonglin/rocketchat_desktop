package com.rc.components;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Created by song on 17-6-4.
 */
public class RCProgressBar extends JProgressBar
{
    public RCProgressBar()
    {
        setForeground(Colors.MAIN_COLOR);

        setBorder(new LineBorder(Colors.MAIN_COLOR_DARKER));
    }

    @Override
    protected void paintBorder(Graphics g)
    {
    }


    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(getWidth(), 8);
    }
}
