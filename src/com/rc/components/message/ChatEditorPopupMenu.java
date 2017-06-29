package com.rc.components.message;

import com.rc.components.Colors;
import com.rc.components.RCMenuItemUI;
import com.rc.utils.ClipboardUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;

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


        selectAllItem.setUI(new RCMenuItemUI(80, 25));
        selectAllItem.addActionListener(new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                JTextPane textPane = (JTextPane) getInvoker();
                textPane.selectAll();
            }
        });


        copyItem.setUI(new RCMenuItemUI(80, 25));
        copyItem.addActionListener(new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                JTextPane textPane = (JTextPane) getInvoker();

                String text = textPane.getSelectedText();
                if (text != null)
                {
                    ClipboardUtil.copyString(text);
                }
            }
        });


        cutItem.setUI(new RCMenuItemUI(80, 25));
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

        pasteItem.setUI(new RCMenuItemUI(80, 25));
        pasteItem.addActionListener(new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                JTextPane textPane = (JTextPane) getInvoker();
                //String text = (String) ClipboardUtil.paste();
                Object data = ClipboardUtil.paste();
                if (data instanceof String)
                {
                    textPane.replaceSelection((String) data);
                }
                else if (data instanceof ImageIcon)
                {
                    ImageIcon icon = (ImageIcon) data;
                    insertIcon(textPane, icon);
                }
                else if (data instanceof java.util.List)
                {
                    List<Object> list = (List<Object>) data;
                    for (Object obj : list)
                    {
                        if (obj instanceof ImageIcon)
                        {
                            insertIcon(textPane, (ImageIcon) obj);
                        }
                    }
                }

               //textPane.insertComponent(new JButton("哈哈"));

            }
        });

        deleteItem.setUI(new RCMenuItemUI(80, 25));
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

    /**
     * 插入图片到编辑框，并自动调整图片大小
     * @param textPane
     * @param icon
     */
    private void insertIcon(JTextPane textPane, ImageIcon icon)
    {
        int iconWidth = icon.getIconWidth();
        int iconHeight = icon.getIconHeight();
        float scale = iconWidth * 1.0F / iconHeight;
        boolean needToScale = false;
        int max = 100;
        if (iconWidth >= iconHeight && iconWidth > max)
        {
            iconWidth = max;
            iconHeight = (int) (iconWidth / scale);
            needToScale = true;
        }
        else if (iconHeight >= iconWidth && iconHeight > max)
        {
            iconHeight = max;
            iconWidth = (int) (iconHeight * scale);
            needToScale = true;
        }

        if (needToScale)
        {
            ImageIcon scaledIcon = new ImageIcon(icon.getImage().getScaledInstance(iconWidth, iconHeight, Image.SCALE_SMOOTH));
            scaledIcon.setDescription(icon.getDescription());
            textPane.insertIcon(scaledIcon);
        }
        else
        {
            textPane.insertIcon(icon);
        }
    }
}
