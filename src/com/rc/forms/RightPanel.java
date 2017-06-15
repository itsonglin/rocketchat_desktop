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
    private RoomMembersPanel roomMembersPanel;

    private ChatPanel chatPanel;
    private TipPanel tipPanel;

    private JPanel contentPanel;

    private CardLayout cardLayout;

    public static final String MESSAGE = "MESSAGE";
    public static final String TIP = "TIP";



    public RightPanel()
    {
        initComponents();
        initView();

    }

    private void initComponents()
    {
        cardLayout = new CardLayout();
        contentPanel = new JPanel();
        contentPanel.setLayout(cardLayout);

        titlePanel = new TitlePanel(this);
        chatPanel = new ChatPanel(this);
        roomMembersPanel = new RoomMembersPanel(this);
        tipPanel = new TipPanel(this);
    }

    private void initView()
    {
        contentPanel.add(tipPanel, TIP);
        contentPanel.add(chatPanel, MESSAGE);

        this.setBackground(Colors.WINDOW_BACKGROUND);
        this.setLayout(new BorderLayout());
        add(titlePanel, BorderLayout.NORTH);
        add(roomMembersPanel, BorderLayout.EAST);
        add(contentPanel, BorderLayout.CENTER);


        // add(chatPanel, BorderLayout.CENTER);
        //add(tipPanel, BorderLayout.CENTER);
    }

    public void showPanel(String who)
    {
        cardLayout.show(contentPanel, who);
    }


    public JPanel getRoomMembersPanel()
    {
        return roomMembersPanel;
    }

    public JPanel getTipPanel()
    {
        return tipPanel;
    }
}
