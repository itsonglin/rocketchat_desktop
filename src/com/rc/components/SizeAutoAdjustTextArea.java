package com.rc.components;

import com.rc.forms.MainFrame;
import com.rc.utils.FontUtil;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseListener;

/**
 * Created by song on 17-6-4.
 */
public class SizeAutoAdjustTextArea extends JTextArea
{
    private String[] lineArr;
    private int maxWidth;
    private Object tag;

    public SizeAutoAdjustTextArea(int maxWidth)
    {
        this.maxWidth = maxWidth;
        setOpaque(false);
        setLineWrap(true);
        setWrapStyleWord(false);
        this.setFont(FontUtil.getDefaultFont(14));
        setEditable(false);
    }


    @Override
    public void setText(String t)
    {
        if (t == null)
        {
            return;
        }

        FontMetrics fm = getFontMetrics(getFont());

        int[] info = parseText(t);
        int lineCount = info[0];
        int lineHeight = fm.getHeight();

        //int targetHeight = lineHeight * lineCount + 20;
        //int targetWidth = fm.stringWidth(lineArr[info[1]]) + 25;

        int targetHeight = lineHeight * lineCount;
        int targetWidth = 20;

        if (lineCount > 0)
        {
            targetWidth = fm.stringWidth(lineArr[info[1]]) + 5;
        }
        // 输入全为\n的情况
        else
        {
            targetHeight = lineHeight;
            t = " ";
        }


        if (targetWidth > maxWidth)
        {
            targetWidth = maxWidth;

            // 解析每一行的宽度
            int totalLine = 0;
            for (String line : lineArr)
            {
                int w = fm.stringWidth(line);
                //int ret = w / (maxWidth - 25);
                int ret = w / (maxWidth - 5);
                int l = ret == 0 ? ret : ret + 1;
                totalLine += l == 0 ? 1 : l;
            }
            //targetHeight = lineHeight * totalLine + 20;
            targetHeight = lineHeight * totalLine;
        }

        this.setPreferredSize(new Dimension(targetWidth, targetHeight + 2));

        super.setText(t);
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

    public Object getTag()
    {
        return tag;
    }

    public void setTag(Object tag)
    {
        this.tag = tag;
    }


    @Override
    public synchronized void addMouseListener(MouseListener l)
    {
        for (MouseListener listener : getMouseListeners())
        {
            if (listener == l)
            {
                return;
            }
        }

        super.addMouseListener(l);
    }
}
