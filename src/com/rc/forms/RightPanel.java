package com.rc.forms;

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
    private JPanel roomMembersPanel;

    public RightPanel()
    {
        initComponents();
        initView();

    }

    private void initComponents()
    {
        titlePanel = new TitlePanel(this);
        chatPanel = new ChatPanel(this);
        roomMembersPanel = new JPanel();
        roomMembersPanel.setPreferredSize(new Dimension(150, 500));
        roomMembersPanel.setBorder(new LineBorder(Colors.LIGHT_GRAY));

    }

    private void initView()
    {
        this.setBackground(Colors.WINDOW_BACKGROUND);
        this.setLayout(new BorderLayout());
        add(titlePanel, BorderLayout.NORTH);
        add(chatPanel, BorderLayout.CENTER);
        add(roomMembersPanel, BorderLayout.EAST);
    }

    public JPanel getRoomMembersPanel()
    {
        return roomMembersPanel;
    }
}
