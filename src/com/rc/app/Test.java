package com.rc.app;


import com.rc.components.message.JIMSendTextPane;
import com.rc.forms.ImageViewerFrame;
import com.rc.frames.ScreenShot;
import com.rc.panels.EmojiPanel;
import com.rc.utils.AvatarUtil;
import com.rc.utils.EmojiUtil;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import com.vdurmont.emoji.EmojiParser;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by song on 14/06/2017.
 */


class Test extends JFrame
{
    public Test()
    {
        super("JLayeredPane");
        /*
         * 由小到大定义组件深度数值，也就是Z-order layer的大小。
		 */
        Integer[] layerConstants = {JLayeredPane.DEFAULT_LAYER,
                JLayeredPane.PALETTE_LAYER, new Integer(101),
                JLayeredPane.MODAL_LAYER, new Integer(201),
                JLayeredPane.POPUP_LAYER, JLayeredPane.DRAG_LAYER};
		/*
		 * 定义每个JLabel的颜色
		 */
        Color[] colors = {Color.red, Color.blue, Color.magenta, Color.cyan,
                Color.yellow, Color.green, Color.pink};
        Point position = new Point(10, 10);
        JLabel[] label = new JLabel[7];
        JLayeredPane layeredPane = getLayeredPane();// 取得窗口的Layered Pane

        for (int i = 0; i < 7; i++)
        {
            label[i] = createLabel("第" + (i + 1) + "层", colors[i], position);
            position.x = position.x + 20;
            position.y = position.y + 20;
            // 将组件(JLabel)放入Layered Pane中并给予深度(Z-order layer)的数值。
            layeredPane.add(label[i], layerConstants[i]);
        }
        setSize(new Dimension(280, 280));
        show();
        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                System.exit(0);
            }
        });
    }

    public JLabel createLabel(String content, Color color, Point position)
    {
        JLabel label = new JLabel(content, JLabel.CENTER);
        label.setVerticalAlignment(JLabel.TOP);
        label.setBackground(color);
        label.setForeground(Color.black);
        label.setOpaque(true);
        label.setBounds(position.x, position.y, 100, 100);
        return label;
    }

    public static void main(String[] args)
    {
        new Test();
    }
}

