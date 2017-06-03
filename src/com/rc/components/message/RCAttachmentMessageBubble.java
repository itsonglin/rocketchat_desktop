package com.rc.components.message;

import com.rc.forms.MainFrame;
import com.rc.utils.FontUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * 文本气泡
 *
 * Created by song on 17-6-3.
 */
public class RCAttachmentMessageBubble extends JPanel
{
    private  NinePatchImageIcon backgroundNormalIcon;
    private  NinePatchImageIcon backgroundActiveIcon;
    private Icon currentBackgroundIcon;


    public RCAttachmentMessageBubble()
    {
        setOpaque(false);
        setListener();
    }

    public void setBackgroundIcon(Icon icon)
    {
        currentBackgroundIcon = icon;
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        if (currentBackgroundIcon != null)
        {
            currentBackgroundIcon.paintIcon(this, g, 0, 0);
        }
        super.paintComponent(g);
    }

    private void setListener()
    {
        addMouseListener(new MouseListener()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
            }

            @Override
            public void mousePressed(MouseEvent e)
            {

            }

            @Override
            public void mouseReleased(MouseEvent e)
            {
            }

            @Override
            public void mouseEntered(MouseEvent e)
            {
                setBackgroundIcon(backgroundActiveIcon);
                RCAttachmentMessageBubble.this.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e)
            {
                setBackgroundIcon(backgroundNormalIcon);
                RCAttachmentMessageBubble.this.repaint();
            }
        });
    }

    public NinePatchImageIcon getBackgroundNormalIcon()
    {
        return backgroundNormalIcon;
    }

    public void setBackgroundNormalIcon(NinePatchImageIcon backgroundNormalIcon)
    {
        this.backgroundNormalIcon = backgroundNormalIcon;
    }

    public NinePatchImageIcon getBackgroundActiveIcon()
    {
        return backgroundActiveIcon;
    }

    public void setBackgroundActiveIcon(NinePatchImageIcon backgroundActiveIcon)
    {
        this.backgroundActiveIcon = backgroundActiveIcon;
    }

    public Icon getCurrentBackgroundIcon()
    {
        return currentBackgroundIcon;
    }

    public void setCurrentBackgroundIcon(Icon currentBackgroundIcon)
    {
        this.currentBackgroundIcon = currentBackgroundIcon;
    }
}
