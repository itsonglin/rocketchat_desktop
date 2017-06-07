package com.rc.forms;

import com.rc.adapter.RoomMembersAdapter;
import com.rc.components.Colors;
import com.rc.components.GBC;
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
    private static RoomMembersPanel roomMembersPanel;

    public static final int ROOM_MEMBER_PANEL_WIDTH = 200;
    private RCListView listView = new RCListView();
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
        setPreferredSize(new Dimension(ROOM_MEMBER_PANEL_WIDTH, MainFrame.getContext().currentWindowHeight));
        setBorder(new LineBorder(Colors.LIGHT_GRAY));
        setVisible(false);
        listView.setScrollBarColor(Colors.SCROLL_BAR_THUMB, Colors.WINDOW_BACKGROUND);
    }

    private void initView()
    {
        //add(listView);
        setLayout(new GridBagLayout());
        add(listView, new GBC(0, 0).setFill(GBC.BOTH).setWeight(1, 1));
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
