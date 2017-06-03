package com.rc.components;

import com.rc.forms.MainFrame;
import com.rc.utils.FontUtil;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * <code>RTextPane</code>
 *
 * @author Jimmy
 * @since v1.0.0 (Oct 15, 2013)
 */
public class RCMessageBubble extends JTextArea
{

    private static final long serialVersionUID = 1L;
    private  NinePatchImageIcon backgroundNormal;
    private  NinePatchImageIcon backgroundActive;

    private Icon mBgIcon;
    private String[] lineArr;


    public RCMessageBubble()
    {
        super();
        setOpaque(false);
        setLineWrap(true);
        setWrapStyleWord(false);
        this.setFont(FontUtil.getDefaultFont(14));
        setEditable(false);

        backgroundNormal = new NinePatchImageIcon(this.getClass().getResource("/image/right.9.png"));
        backgroundActive = new NinePatchImageIcon(this.getClass().getResource("/image/right_active.9.png"));
        setBackgroundIcon(backgroundNormal);

        setListener();
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
                RCMessageBubble.this.setBackgroundIcon(backgroundActive);
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e)
            {
                setBackgroundIcon(backgroundNormal);
                repaint();
            }
        });
    }


    public void setBackgroundIcon(Icon icon)
    {
        mBgIcon = icon;
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        if (mBgIcon != null)
        {
            mBgIcon.paintIcon(this, g, 0, 0);
        }
        super.paintComponent(g);
    }

    public void setText(String t)
    {
        int maxWidth = (int) (MainFrame.getContext().currentWindowWidth * 0.5);
        FontMetrics fm = getFontMetrics(getFont());

        int[] info = parseText(getText());
        int lineCount = info[0];
        int lineHeight = fm.getHeight();

        int targetHeight = lineHeight * lineCount + 20;
        int targetWidth = fm.stringWidth(lineArr[info[1]]) + 25;


        if (targetWidth > maxWidth)
        {
            targetWidth = maxWidth;

            // 解析每一行的宽度
            int totalLine = 0;
            for (String line : lineArr)
            {
                int w = fm.stringWidth(line);
                int ret = w / (maxWidth - 25);
                int l = ret == 0 ? ret : ret + 1;
                totalLine += l == 0 ? 1 : l;
            }
            targetHeight = lineHeight * totalLine + 20;
        }

        this.setPreferredSize(new Dimension(targetWidth, targetHeight));

        super.setText(t);
    }

    @Override
    public Insets getInsets()
    {
        return new Insets(10, 10, 10, 10);
    }

    public int[] parseText(String text)
    {
        int[] retArr = new int[2];

        lineArr = text.split("\\n");
        int maxLength = 0;
        int position = 0;
        for (int i = 0; i < lineArr.length; i++)
        {
            if (lineArr[i].length() > maxLength)
            {
                maxLength = lineArr[i].length();
                position = i;
            }
        }

        retArr[0] = lineArr.length;
        retArr[1] = position;

        return retArr;
    }
}
