package com.rc.components;

import com.rc.utils.EmojiUtil;
import com.rc.utils.FontUtil;
import com.vdurmont.emoji.EmojiParser;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;

/**
 * Created by song on 17-6-4.
 */
public class SizeAutoAdjustTextArea extends JTextPane
{
    private final FontMetrics fontMetrics;
    private String[] lineArr;
    private int maxWidth;
    private Object tag;
    private Pattern emojiPattern;

    private String emojiRegx;
    private int emojiSize = 18;


    // 最长一行是第几行
    private int maxLengthLinePosition = 0;


    public SizeAutoAdjustTextArea(int maxWidth)
    {
        this.maxWidth = maxWidth;
        setOpaque(false);
        //setLineWrap(true);
        //setWrapStyleWord(false);
        this.setFont(FontUtil.getDefaultFont(14));
        setEditable(false);

        emojiRegx = ":.+?:";
        emojiPattern = Pattern.compile(emojiRegx);// 懒惰匹配，最小匹配
        fontMetrics = getFontMetrics(getFont());

    }


    @Override
    public void setText(String t)
    {
        // 对emoji的Unicode编码转别名
        t = EmojiParser.parseToAliases(t);

        if (t == null)
        {
            return;
        }


        // 总行数
        int lineCount = parseLineCount(t);

        // 每一行的emoji表情信息
        List<LineEmojiInfo> lineEmojiInfoList = parseLineEmojiInfo();

        //int[] lineEmojiCount = parseLineEmojiInfo();

        int[] lineWidthArr = parseLineActualWidth(lineEmojiInfoList);


        int lineHeight = fontMetrics.getHeight();
        int targetHeight = lineHeight * lineCount;
        int targetWidth = 20;


        if (lineCount > 0)
        {
            //targetWidth = fontMetrics.stringWidth(lineArr[maxLengthLinePosition]) + 5 + emojiTotalWidth;
            targetWidth = lineWidthArr[maxLengthLinePosition] + 10;
        }
        // 输入全为\n的情况
        else
        {
            targetHeight = lineHeight;
            t = " ";
        }


        // 如果最长的一行宽度超过了最大宽度，就要重新计算高度
        if (targetWidth > maxWidth)
        {
            targetWidth = maxWidth;

            int totalLine = 0;
            for (int lineWidth : lineWidthArr)
            {

                int ret = lineWidth / (maxWidth - 10);
                int l = ret == 0 ? ret : ret + 1;
                totalLine += l == 0 ? 1 : l;
            }

            targetHeight = lineHeight * totalLine;
        }

        // 如果该行有emoji表情，高度就要适当增加
        for (LineEmojiInfo info : lineEmojiInfoList)
        {
            if (info.getCount() > 0)
            {
                targetHeight += 4;
            }
        }

       /* for (int c : lineEmojiInfoList)
        {
            if (c > 0)
            {
                targetHeight += 4;
            }
        }*/

        //this.setPreferredSize(new Dimension(targetWidth, targetHeight + 2));
        this.setPreferredSize(new Dimension(targetWidth, targetHeight + 2));

        super.setText(t.replaceAll(emojiRegx, ""));
        //super.setText(t);

        insertEmoji(t);

        //insertIcon(IconUtil.getIcon(this, "/image/smile.png", 18,18));

/*        StyledDocument doc = getStyledDocument();
        MutableAttributeSet attr = new SimpleAttributeSet();
        StyleConstants.setIcon(attr, IconUtil.getIcon(this, "/emoji/apple.png", 15,15));
        doc.setCharacterAttributes(1,1, attr, false);*/
    }

    private void insertEmoji(String src)
    {
        Document doc = getDocument();

        Map<Integer, String> retMap = new HashMap();
        StringBuilder stringBuilder = new StringBuilder();

        char[] charArr = src.toCharArray();
        char ch;
        boolean emojiStart = false;
        int pos = -1;
        for (int i = 0; i < charArr.length; i++)
        {
            ch = charArr[i];
            if (ch == ':')
            {
                if (!emojiStart)
                {
                    emojiStart = true;
                    stringBuilder.append(ch);
                    pos = i;
                }
                else
                {
                    emojiStart = false;
                    stringBuilder.append(ch);
                    retMap.put(pos, stringBuilder.toString());


                    setCaretPosition(pos);
                    Icon icon = EmojiUtil.getEmoji(this, stringBuilder.toString());
                    if (icon != null)
                    {
                        insertIcon(icon);

                        charArr = resetCharArr(new String(charArr), stringBuilder.toString());
                        i = pos;
                        stringBuilder.setLength(0);
                    }
                    else
                    {
                        // 表情不存在，原样输出
                        try
                        {
                            doc.insertString(pos, stringBuilder.toString(), getCharacterAttributes());
                        } catch (BadLocationException e)
                        {
                            e.printStackTrace();
                        }
                        stringBuilder.setLength(0);

                    }
                }
            }
            else
            {
                if (emojiStart)
                {
                    stringBuilder.append(ch);
                }
            }

        }

    }

    private char[] resetCharArr(String src, String s)
    {
        String str = src.replaceFirst(s, "#");
        return str.toCharArray();
    }


    /**
     * 分析每一行的emoji数量
     * @return
     */
    private List<LineEmojiInfo> parseLineEmojiInfo()
    {

        //int[] retArr = new int[lineArr.length];

        List<LineEmojiInfo> infoList = new ArrayList<>(lineArr.length);
        List<String> emojiList;
        LineEmojiInfo info;
        for (int i = 0; i < lineArr.length; i++)
        {
            emojiList = parseEmoji(lineArr[i]);
            info = new LineEmojiInfo(emojiList.size(), emojiList);
            infoList.add(info);
            //retArr[i] = emojiList.size();
        }


        return infoList;
    }

    /**
     * 提取字符串中的所有emoji表情编码
     *
     * @param src
     * @return
     */
    private List<String> parseEmoji(String src)
    {
        List<String> emojiList = new ArrayList<>();

        Matcher emojiMatcher = emojiPattern.matcher(src);

        while (emojiMatcher.find())
        {
            String code = emojiMatcher.group();
            if (EmojiUtil.isRecognizableEmoji(this, code))
            {
                emojiList.add(code);
            }
        }

        return emojiList;
    }


    /**
     * 分析消息文本一共有几行
     *
     * @param text 消息文本
     * @return 消息的行数，以\n分隔
     */
    private int parseLineCount(String text)
    {
        lineArr = text.split("\\n");

        return lineArr.length;
    }


    /**
     * 解析每一行的实际宽度，包括字符串宽度和表情宽度
     * @param lineEmojiInfoList
     * @return
     */
    private int[] parseLineActualWidth(List<LineEmojiInfo> lineEmojiInfoList)
    {
        String[] lineArrCopy = lineArr.clone();
        int[] retArr = new int[lineArrCopy.length];

        int maxLength = 0;
        maxLengthLinePosition = 0;

        for (int i = 0; i < lineArrCopy.length; i++)
        {
            //String exceptEmoji = lineArrCopy[i].replaceAll(emojiRegx, "");
            String exceptEmoji = lineArrCopy[i];

            List<String> emojiList = lineEmojiInfoList.get(i).getEmojiList();
            for (String emoji : emojiList)
            {
                exceptEmoji = exceptEmoji.replace(emoji, "");
            }

            int width = fontMetrics.stringWidth(exceptEmoji);
            width += lineEmojiInfoList.get(i).getCount() * emojiSize;
            retArr[i] = width;

            if (width > maxLength)
            {
                maxLength = width;
                maxLengthLinePosition = i;
            }
        }

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

class LineEmojiInfo
{
    /**
     * emoji 表情的数量
     */
    private int count;

    private List<String> emojiList;

    public LineEmojiInfo(int count, List<String> emojiList)
    {
        this.count = count;
        this.emojiList = emojiList;
    }

    public int getCount()
    {
        return count;
    }

    public void setCount(int count)
    {
        this.count = count;
    }

    public List<String> getEmojiList()
    {
        return emojiList;
    }

    public void setEmojiList(List<String> emojiList)
    {
        this.emojiList = emojiList;
    }
}
