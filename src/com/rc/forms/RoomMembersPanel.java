package com.rc.forms;

import com.rc.adapter.RoomMembersAdapter;
import com.rc.components.Colors;
import com.rc.components.GBC;
import com.rc.components.RCButton;
import com.rc.components.RCListView;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by song on 07/06/2017.
 */
public class RoomMembersPanel extends ParentAvailablePanel
{
    public static final int ROOM_MEMBER_PANEL_WIDTH = 200;
    private static RoomMembersPanel roomMembersPanel;

    private RCListView listView = new RCListView();
    private JPanel operationPanel = new JPanel();
    private JButton leaveButton = new RCButton();

    private List<String> members = new ArrayList<>();

    public RoomMembersPanel(JPanel parent)
    {
        super(parent);
        roomMembersPanel = this;

        initComponents();
        initView();
    }

    private void initComponents()
    {
        setBorder(new LineBorder(Colors.LIGHT_GRAY));

        setPreferredSize(new Dimension(ROOM_MEMBER_PANEL_WIDTH, MainFrame.getContext().currentWindowHeight));
        setVisible(false);
        listView.setScrollBarColor(Colors.SCROLL_BAR_THUMB, Colors.WINDOW_BACKGROUND);

        operationPanel.setPreferredSize(new Dimension(60, 80));

        leaveButton.setText("退出群聊");
        leaveButton.setForeground(Colors.RED);
        leaveButton.setPreferredSize(new Dimension(180,30));

    }

    private void initView()
    {
        operationPanel.add(leaveButton);

        setLayout(new GridBagLayout());
        add(listView, new GBC(0, 0).setFill(GBC.BOTH).setWeight(1, 70));
        add(operationPanel, new GBC(0, 1).setFill(GBC.BOTH).setWeight(1, 1).setInsets(5,0,0,0));

        listView.setAdapter(new RoomMembersAdapter(members));
    }


    @Override
    public void setVisible(boolean aFlag)
    {
        /// TODO 根据打开的房间获取房间成员
        if (aFlag)
        {
            getRoomMembers();
            super.setVisible(aFlag);
            listView.notifyDataSetChange();
        }

        super.setVisible(aFlag);
    }

    private void getRoomMembers()
    {
        members.clear();
        members.add("添加成员");
        members.add("删除成员");

        for (int i = 0; i < 20; i++)
        {
            members.add("user " + i);
        }
    }


    public static RoomMembersPanel getContext()
    {
        return roomMembersPanel;
    }
}
