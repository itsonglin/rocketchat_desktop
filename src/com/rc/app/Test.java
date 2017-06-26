package com.rc.app;


import com.rc.forms.ImageViewerFrame;
import com.rc.utils.AvatarUtil;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import com.vdurmont.emoji.EmojiParser;
import sun.misc.BASE64Encoder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by song on 14/06/2017.
 */

class Test
{
    public static void main(String[] args) throws IOException, FontFormatException
    {

        //String strImg = getImageStr("/Users/song/Pictures/ubuntu.png");
        //System.out.println(strImg);

        /*JFrame frame = new JFrame();
        frame.setSize(new Dimension(800, 600));
        JLabel label = new JLabel("AAAAAAAAAAAAAAA");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        frame.add(label);

        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem enlargeItem = new JMenuItem("放大");
        JMenuItem narrowItem = new JMenuItem("缩小");
        JMenuItem saveAsItem = new JMenuItem("另存为");

        *//*saveAsItem.setUI(new RCMenuItemUI());
        narrowItem.setUI(new RCMenuItemUI());
        enlargeItem.setUI(new RCMenuItemUI());*//*

        popupMenu.add(enlargeItem);
        popupMenu.add(narrowItem);
        popupMenu.add(saveAsItem);

        frame.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                if (e.getButton() == MouseEvent.BUTTON3)
                {
                    popupMenu.show(frame, e.getX(), e.getY());
                }
                super.mouseClicked(e);
            }
        });


        frame.setVisible(true);*/

        //ImageViewerFrame frame = new ImageViewerFrame();
        //frame.setVisible(true);
    }
}

