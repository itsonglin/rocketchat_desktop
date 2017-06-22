package com.rc.app;


import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import com.vdurmont.emoji.EmojiParser;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by song on 14/06/2017.
 */

class Test extends JFrame
{
    public static void main(String[] args) throws IOException, FontFormatException
    {

        Pattern p = Pattern.compile("((.)+)+");
        String str = "asdasd";
        /*Matcher m = p.matcher(str);
        if (m.matches())
        {
            System.out.println("货币金额: " + m.group(1));
            System.out.println("货币种类: " + m.group(2));
        }*/


        boolean ss = str.length() % 2 == 0 && str.substring(0, str.length() / 2).equals(str.substring(str.length() / 2));
        System.out.println(ss);



        //boolean IsReduplication = "asdasd".matches("((.))\\1+\\2+"); // 关键字是否是叠，如aa、aaa

        //System.out.println(IsReduplication);
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

