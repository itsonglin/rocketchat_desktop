package com.rc.forms;

import com.rc.adapter.RoomItemsAdapter;
import com.rc.components.*;
import com.rc.entity.RoomItem;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 左侧聊天列表
 * Created by song on 17-5-30.
 */
public class ChatItemsPanel extends ParentAvailablePanel
{
    //private JScrollPane scrollPane;
    private RCListView roomItemsListView;
    private List<RoomItem> roomItemList;

    public ChatItemsPanel(JPanel parent)
    {
        super(parent);
        initComponents();
        initView();
    }

    private void initComponents()
    {
        roomItemsListView = new RCListView();

        roomItemList = new ArrayList<>();
        for (int i = 0 ; i < 10; i ++)
        {
            RoomItem item = new RoomItem();
            roomItemList.add(item);
        }
        roomItemsListView.setAdapter(new RoomItemsAdapter(roomItemList));
    }

    private void initView()
    {
        setLayout(new GridBagLayout());
        roomItemsListView.setContentPanelBackground(Colors.DARK);
        add(roomItemsListView, new GBC(0, 0).setFill(GBC.BOTH).setWeight(1, 1));
        //add(scrollPane, new GBC(0, 0).setFill(GBC.BOTH).setWeight(1, 1));
    }

}
