package com.rc.forms;

import com.rc.adapter.message.MessageRightTextViewHolder;
import com.rc.components.Colors;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Created by song on 17-5-29.
 */
public class RightPanel extends JPanel
{
    private TitlePanel titlePanel;
    private ChatPanel chatPanel;
    private JPanel chatRoomInfoPanel;

    public RightPanel()
    {
        initComponents();
        initView();

    }

    private void initComponents()
    {
        titlePanel = new TitlePanel(this);
        chatPanel = new ChatPanel(this);
        chatRoomInfoPanel = new JPanel();
        chatRoomInfoPanel.setPreferredSize(new Dimension(150, 500));
        chatRoomInfoPanel.setBorder(new LineBorder(Color.red));
    }

    private void initView()
    {
        this.setBackground(Colors.WINDOW_BACKGROUND);
        this.setLayout(new BorderLayout());
        add(titlePanel, BorderLayout.NORTH);
        add(chatPanel, BorderLayout.CENTER);
        add(chatRoomInfoPanel, BorderLayout.EAST);
    }
}
