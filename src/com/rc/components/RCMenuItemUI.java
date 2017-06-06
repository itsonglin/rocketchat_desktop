package com.rc.components;

import com.rc.utils.FontUtil;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuItemUI;
import java.awt.*;

/**
 * Created by song on 2017/6/5.
 */
public class RCMenuItemUI extends BasicMenuItemUI
{
    @Override
    public void installUI(JComponent c)
    {
        super.installUI(c);

        c.setPreferredSize(new Dimension(70, 30));
        c.setBackground(Colors.FONT_WHITE);
        c.setFont(FontUtil.getDefaultFont(12));
        c.setBorder(null);
    }


    @Override
    protected void paintText(Graphics g, JMenuItem menuItem, Rectangle textRect, String text)
    {
        int x = (int) ((menuItem.getSize().getWidth() - textRect.width) / 2);


        Rectangle newRect =  new Rectangle(x, textRect.y, textRect.width, textRect.height);
        super.paintText(g, menuItem, newRect, text);
    }

    @Override
    protected void paintBackground(Graphics g, JMenuItem menuItem, Color bgColor)
    {
        super.paintBackground(g, menuItem, Colors.SCROLL_BAR_TRACK_LIGHT);
    }
}
