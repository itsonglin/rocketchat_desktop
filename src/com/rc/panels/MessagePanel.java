package com.rc.panels;

import com.rc.components.Colors;
import com.rc.components.RCListView;
import com.rc.components.message.MessagePopupMenu;
import com.rc.frames.MainFrame;

import javax.swing.*;
import java.awt.*;

/**
 * Created by song on 17-5-30.
 */
public class MessagePanel extends ParentAvailablePanel
{
    private static MessagePanel context;

    RCListView listView;

    public MessagePanel(JPanel parent)
    {
        super(parent);
        context = this;

        initComponents();
        setListeners();
        initView();
    }


    private void initComponents()
    {
        listView = new RCListView(0, 15);
        listView.setScrollBarColor(Colors.WINDOW_BACKGROUND, Colors.WINDOW_BACKGROUND);
        listView.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        listView.setScrollHiddenOnMouseLeave(listView);
    }

    private void setListeners()
    {
        /*listView.addMouseListener(new AbstractMouseListener(){

            @Override
            public void mouseClicked(MouseEvent e)
            {
                RoomMembersPanel.getContext().setVisible(false);
                super.mouseClicked(e);
            }
        });*/
    }

    private void initView()
    {
        this.setLayout(new BorderLayout());
        add(listView, BorderLayout.CENTER);
    }

    public RCListView getMessageListView()
    {
        return listView;
    }

    public static MessagePanel getContext()
    {
        return context;
    }
}

