package com.rc.components.message;

import com.rc.components.Colors;
import com.rc.components.RCMainOperationMenuItemUI;
import com.rc.components.RCMenuItemUI;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Created by song on 2017/6/5.
 */
public class MainOperationPopupMenu extends JPopupMenu
{
    public MainOperationPopupMenu()
    {
        initMenuItem();
    }

    private void initMenuItem()
    {
        JMenuItem item1 = new JMenuItem("创建群聊");
        JMenuItem item2 = new JMenuItem("系统设置");

        item1.setUI(new RCMainOperationMenuItemUI());
        item1.addActionListener(new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                System.out.println("创建群聊");
            }
        });
        ImageIcon icon1 = new ImageIcon(getClass().getResource("/image/chat.png"));
        icon1.setImage(icon1.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        item1.setIcon(icon1);
        item1.setIconTextGap(3);


        item2.setUI(new RCMainOperationMenuItemUI());
        item2.addActionListener(new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                System.out.println("系统设置");
            }
        });
        ImageIcon icon2 = new ImageIcon(getClass().getResource("/image/setting.png"));
        icon2.setImage(icon2.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        item2.setIcon(icon2);
        item2.setIconTextGap(3);


        this.add(item1);
        this.add(item2);

        setBorder(new LineBorder(Colors.SCROLL_BAR_TRACK_LIGHT));
        setBackground(Colors.FONT_WHITE);
    }
}
