package com.rc.app;


import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import com.vdurmont.emoji.EmojiParser;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by song on 14/06/2017.
 */

class Test extends JFrame
{
    public static void main(String[] args) throws IOException, FontFormatException
    {
        /*JFrame frame = new Frame();
        frame.setBounds(100, 100, 300, 400);
        frame.setVisible(true);*/

        //String regx = "(.)\\1+";
        //System.out.println("aaaaaaaaaaaaaaaaa".matches(regx));

        String str = "aaa";
        String key = "aa";

        int keyLen = key.length();
        boolean IsReduplication = key.matches("(.)\\1+");

        int pos = str.indexOf(key);//*第一个出现的索引位置
        List<Integer> posArr = new ArrayList<>();
        while (pos != -1)
        {
            posArr.add(pos);
            if (IsReduplication)
            {
                pos = str.indexOf(key, pos + keyLen); // 如果遇到关键字是叠词的情况，则间距为一个关键字
            }
            else
            {
                pos = str.indexOf(key, pos + 1);// 从这个索引往后开始第一个出现的位置
            }
        }

        System.out.println(posArr);

        int strIndex = 0;
        int posIndex = 0;
        while (strIndex < str.length())
        {
            if (posIndex >= posArr.size())
            {
                String s = str.substring(strIndex);
                System.out.println("out -- " + s);
                break;
            }

            String s = str.substring(strIndex, posArr.get(posIndex));
            System.out.println("out -- " + s);
            strIndex += s.length();

            System.out.println("out -- " + key);
            strIndex += keyLen;

            posIndex++;

        }

        //String[] strArr = new String[]
    }
}

class Frame extends JFrame
{
    private JTextArea textPane;

    public Frame() throws IOException, FontFormatException
    {
        textPane = new JTextArea();
        add(textPane);


       /* HTMLEditorKit htmledit=new HTMLEditorKit();
        //实例化一个HTMLEditorkit工具包，用来编辑和解析用来显示在jtextpane中的内容。
        HTMLDocument text_html=(HTMLDocument) htmledit.createDefaultDocument();
        //使用HTMLEditorKit类的方法来创建一个文档类，HTMLEditorKit创建的类型默认为htmldocument。
        textPane.setEditorKit(htmledit);
        //设置jtextpane组件的编辑器工具包，是其支持html格式。
        textPane.setContentType("text/html");
        //设置编辑器要处理的文档内容类型，有text/html,text/rtf.text/plain三种类型。
        textPane.setDocument(text_html);
        //设置编辑器关联的一个文档。*/


        String text = "\n\nA :cat:, :dog: and a :mouse: became friends<3. For :dog:'s birthday party, they all had :hamburger:s, :fries:s, :cookie:s and :cake:.";

        // String  str = EmojiUtils.emojify(text); //returns A 🐱, 🐶 and a 🐭 became friends❤️. For 🐶's birthday party, they all had 🍔s, 🍟s, 🍪s and 🍰.

        InputStream is = getClass().getResourceAsStream("/fonts/yahei.ttf");
        Font font = Font.createFont(Font.TRUETYPE_FONT, is);
        Font font2 = font.deriveFont(14);
        textPane.setFont(font2);
        String str = EmojiParser.parseToUnicode(text);
        textPane.setText(str);
        System.out.println(str);

    }
}

