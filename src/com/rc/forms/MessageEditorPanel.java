package com.rc.forms;

import com.rc.components.Colors;
import com.rc.components.GBC;
import com.rc.components.JIMSendTextPane;
import com.rc.components.RCButton;
import com.rc.utils.FontUtil;

import javax.swing.*;
import java.awt.*;

/**
 * Created by song on 17-5-30.
 */
public class MessageEditorPanel extends ParentAvailablePanel
{
    private JPanel controlLabel;
    private JLabel fileLabel;
    private JScrollPane textScrollPane;
    private JIMSendTextPane textEditor;
    private JPanel sendPanel;
    private RCButton sendButton;


    public MessageEditorPanel(JPanel parent)
    {
        super(parent);

        initComponents();
        initView();
    }

    private void initComponents()
    {
        Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);
        controlLabel = new JPanel();
        controlLabel.setLayout(new FlowLayout(FlowLayout.LEFT, 15,5));

        fileLabel = new JLabel();
        fileLabel.setIcon(new ImageIcon(getClass().getResource("/image/file.png")));
        fileLabel.setCursor(handCursor);
        fileLabel.setToolTipText("上传附件");
        controlLabel.add(fileLabel);

        textEditor = new JIMSendTextPane();
        textEditor.setBackground(Colors.WINDOW_BACKGROUND);
        textEditor.setFont(FontUtil.getDefaultFont(16));
        textEditor.setMargin(new Insets(0,15,0,0));
        textScrollPane = new JScrollPane(textEditor);
        textScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        textScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        textScrollPane.setBorder(null);

        sendPanel = new JPanel();
        sendPanel.setLayout(new BorderLayout());

        sendButton = new RCButton("发 送");
        sendPanel.add(sendButton, BorderLayout.EAST);
        sendButton.setForeground(Colors.DARKER);
        sendButton.setFont(FontUtil.getDefaultFont(13));
        sendButton.setPreferredSize(new Dimension(75,23));
    }

    private void initView()
    {
        this.setLayout(new GridBagLayout());

        add(controlLabel, new GBC(0, 0).setFill(GBC.HORIZONTAL).setWeight(1, 1));
        add(textScrollPane, new GBC(0, 1).setFill(GBC.BOTH).setWeight(1, 15));
        add(sendPanel, new GBC(0, 2).setFill(GBC.BOTH).setWeight(1, 1).setInsets(0,0,10,10));
    }
}
