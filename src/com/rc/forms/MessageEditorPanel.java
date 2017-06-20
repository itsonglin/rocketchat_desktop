package com.rc.forms;

import com.rc.components.Colors;
import com.rc.components.GBC;
import com.rc.components.ScrollUI;
import com.rc.components.message.ChatEditorPopupMenu;
import com.rc.components.message.JIMSendTextPane;
import com.rc.components.RCButton;
import com.rc.utils.FontUtil;
import com.rc.utils.IconUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by song on 17-5-30.
 */
public class MessageEditorPanel extends ParentAvailablePanel
{
    private JPanel controlLabel;
    private JLabel fileLabel;
    private JScrollPane textScrollPane;
    private JTextPane textEditor;
    private JPanel sendPanel;
    private RCButton sendButton;
    private ImageIcon fileNormalIcon;
    private ImageIcon fileActiveIcon;
    private ChatEditorPopupMenu chatEditorPopupMenu;


    public MessageEditorPanel(JPanel parent)
    {
        super(parent);

        initComponents();
        initView();
        setListeners();
    }

    private void initComponents()
    {
        Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);
        controlLabel = new JPanel();
        controlLabel.setLayout(new FlowLayout(FlowLayout.LEFT, 15,5));

        fileLabel = new JLabel();
        fileNormalIcon = IconUtil.getIcon(this, "/image/file.png");
        fileActiveIcon = IconUtil.getIcon(this, "/image/file_active.png");
        fileLabel.setIcon(fileNormalIcon);
        fileLabel.setCursor(handCursor);
        fileLabel.setToolTipText("上传附件");
        controlLabel.add(fileLabel);

        textEditor = new JTextPane();
        textEditor.setBackground(Colors.WINDOW_BACKGROUND);
        textEditor.setFont(FontUtil.getDefaultFont(14));
        textEditor.setMargin(new Insets(0,15,0,0));
        textScrollPane = new JScrollPane(textEditor);
        textScrollPane.getVerticalScrollBar().setUI(new ScrollUI(Colors.SCROLL_BAR_THUMB, Colors.WINDOW_BACKGROUND));
        textScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        textScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        textScrollPane.setBorder(null);

        sendPanel = new JPanel();
        sendPanel.setLayout(new BorderLayout());

        sendButton = new RCButton("发 送");
        sendPanel.add(sendButton, BorderLayout.EAST);
        sendButton.setForeground(Colors.DARKER);
        sendButton.setFont(FontUtil.getDefaultFont(13));
        sendButton.setPreferredSize(new Dimension(75,23));

        chatEditorPopupMenu = new ChatEditorPopupMenu();
    }

    private void initView()
    {
        this.setLayout(new GridBagLayout());

        add(controlLabel, new GBC(0, 0).setFill(GBC.HORIZONTAL).setWeight(1, 1));
        add(textScrollPane, new GBC(0, 1).setFill(GBC.BOTH).setWeight(1, 15));
        add(sendPanel, new GBC(0, 2).setFill(GBC.BOTH).setWeight(1, 1).setInsets(0,0,10,10));
    }

    private void setListeners()
    {
        fileLabel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseEntered(MouseEvent e)
            {
                fileLabel.setIcon(fileActiveIcon);
                super.mouseEntered(e);
            }

            @Override
            public void mouseExited(MouseEvent e)
            {
                fileLabel.setIcon(fileNormalIcon);
                super.mouseExited(e);
            }
        });

        textEditor.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseReleased(MouseEvent e)
            {
                if (e.getButton() == MouseEvent.BUTTON3)
                {
                    chatEditorPopupMenu.show((Component) e.getSource(), e.getX(), e.getY());
                }
                super.mouseClicked(e);
            }
        });
    }


    public JTextPane getEditor()
    {
        return textEditor;
    }

    public JButton getSendButton()
    {
        return sendButton;
    }

    public JLabel getUploadFileLabel()
    {
        return fileLabel;
    }

}
