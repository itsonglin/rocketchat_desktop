package com.rc.components.message;

import com.rc.components.Colors;
import com.rc.components.RCMenuItemUI;
import com.rc.components.SizeAutoAdjustTextArea;
import com.rc.utils.ClipboardUtil;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;

/**
 * Created by song on 2017/6/5.
 */
public class MessagePopupMenu extends JPopupMenu
{
    public MessagePopupMenu()
    {
        initMenuItem();
    }

    private void initMenuItem()
    {
        JMenuItem item1 = new JMenuItem("复制");
        JMenuItem item2 = new JMenuItem("删除");
        JMenuItem item3 = new JMenuItem("转发");

        item1.setUI(new RCMenuItemUI());
        item1.addActionListener(new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                SizeAutoAdjustTextArea textArea = (SizeAutoAdjustTextArea) getInvoker();

                String text = textArea.getSelectedText() == null ? textArea.getText() : textArea.getSelectedText();
                if (text != null)
                {
                    ClipboardUtil.copyString(text);
                }

            }
        });


        item2.setUI(new RCMenuItemUI());
        item2.addActionListener(new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                System.out.println("删除");
            }
        });

        item3.setUI(new RCMenuItemUI());
        item3.addActionListener(new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                System.out.println("转发");
            }
        });

        this.add(item1);
        this.add(item2);
        this.add(item3);

        setBorder(new LineBorder(Colors.SCROLL_BAR_TRACK_LIGHT));
        setBackground(Colors.FONT_WHITE);
    }
}
