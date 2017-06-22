package com.rc.components;

import javax.swing.*;
import java.awt.*;
import java.awt.font.LineMetrics;

/**
 * Created by song on 22/06/2017.
 */
public class HighLightLabel extends JLabel
{
    private String keyWord;
    private Color highLightColor;


    public HighLightLabel()
    {
        this(null, Color.ORANGE);
    }

    public HighLightLabel(String keyWord, Color highLightColor)
    {

        this.keyWord = keyWord;
        this.highLightColor = highLightColor;
    }

    public void setKeyWord(String keyWord)
    {
        this.keyWord = keyWord;
    }

    public void setHighLightColor(Color highLightColor)
    {
        this.highLightColor = highLightColor;
    }

    @Override
    public void paint(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setFont(getFont());
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        FontMetrics fm = getFontMetrics(getFont());
        LineMetrics lm = getFont().getLineMetrics(getText(), g2d.getFontRenderContext());

        // 文本于容器垂直居中
        int y = (int) (fm.getHeight() + lm.getAscent() - lm.getDescent());

        // 未提供关键字或关键字为空，则正常绘制
        if (keyWord == null || keyWord.isEmpty())
        {
            g2d.drawString(getText(), 0 , y);
        }
        else
        {
            int x = 0;

            String[] strArr = getText().split(keyWord);
            for (int i = 0; i < strArr.length; i++)
            {
                g2d.setColor(getForeground());
                g2d.drawString(strArr[i], x , y);
                x += fm.stringWidth(strArr[i]);

                if (i < strArr.length - 1)
                {
                    g2d.setColor(highLightColor);
                    g2d.drawString(keyWord, x , y);
                    x += fm.stringWidth(keyWord);
                }
            }

            if (strArr.length == 0 && getText().length() > 0)
            {
                g2d.setColor(highLightColor);
                g2d.drawString(getText(), x , y);
                return;
            }

        }
    }


}