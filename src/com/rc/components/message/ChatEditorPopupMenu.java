package com.rc.components.message;

import com.rc.components.Colors;
import com.rc.components.RCMenuItemUI;
import com.rc.components.SizeAutoAdjustTextArea;
import com.rc.utils.ClipboardUtil;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.event.ActionEvent;

/**
 * Created by song on 2017/6/5.
 */
public class ChatEditorPopupMenu extends JPopupMenu
{
    public ChatEditorPopupMenu()
    {
        initMenuItem();
    }

    private void initMenuItem()
    {
        JMenuItem selectAllItem = new JMenuItem("全选");
        JMenuItem copyItem = new JMenuItem("复制");
        JMenuItem cutItem = new JMenuItem("剪切");
        JMenuItem pasteItem = new JMenuItem("粘贴");
        JMenuItem deleteItem = new JMenuItem("删除");


        selectAllItem.setUI(new RCMenuItemUI(80, 30));
        selectAllItem.addActionListener(new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                JTextPane textPane = (JTextPane) getInvoker();
                textPane.selectAll();
            }
        });


        copyItem.setUI(new RCMenuItemUI(80, 30));
        copyItem.addActionListener(new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                JTextPane textPane = (JTextPane) getInvoker();

                String text = textPane.getSelectedText() == null ? textPane.getText() : textPane.getSelectedText();
                if (text != null)
                {
                    ClipboardUtil.copyString(text);
                }
            }
        });


        cutItem.setUI(new RCMenuItemUI(80, 30));
        cutItem.addActionListener(new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                JTextPane textPane = (JTextPane) getInvoker();
                String text = textPane.getSelectedText();
                if (text != null)
                {
                    ClipboardUtil.copyString(text);
                    textPane.replaceSelection("");
                }
            }
        });

        pasteItem.setUI(new RCMenuItemUI(80, 30));
        pasteItem.addActionListener(new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                JTextPane textPane = (JTextPane) getInvoker();
                String text = ClipboardUtil.pasteString();

                textPane.replaceSelection(text);

            }
        });

        deleteItem.setUI(new RCMenuItemUI(80, 30));
        deleteItem.addActionListener(new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                JTextPane textPane = (JTextPane) getInvoker();
                String text = textPane.getSelectedText();
                if (text != null)
                {
                    textPane.replaceSelection("");
                }
            }
        });

        this.add(selectAllItem);
        this.add(copyItem);
        this.add(cutItem);
        this.add(pasteItem);
        this.add(deleteItem);

        setBorder(new LineBorder(Colors.SCROLL_BAR_TRACK_LIGHT));
        setBackground(Colors.FONT_WHITE);
    }
}
