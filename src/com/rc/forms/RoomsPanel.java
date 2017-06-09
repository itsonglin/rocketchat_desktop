package com.rc.forms;

import com.rc.adapter.RoomItemsAdapter;
import com.rc.app.Launcher;
import com.rc.components.*;
import com.rc.db.model.Room;
import com.rc.db.service.RoomService;
import com.rc.entity.RoomItem;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 左侧聊天列表
 * Created by song on 17-5-30.
 */
public class RoomsPanel extends ParentAvailablePanel
{
    private RCListView roomItemsListView;
    private List<RoomItem> roomItemList;
    private RoomService roomService = Launcher.roomService;

    public RoomsPanel(JPanel parent)
    {
        super(parent);
        initComponents();
        initView();
        initData();
    }

    private void initComponents()
    {
        roomItemsListView = new RCListView();
    }

    private void initView()
    {
        setLayout(new GridBagLayout());
        roomItemsListView.setContentPanelBackground(Colors.DARK);
        add(roomItemsListView, new GBC(0, 0).setFill(GBC.BOTH).setWeight(1, 1));
        //add(scrollPane, new GBC(0, 0).setFill(GBC.BOTH).setWeight(1, 1));
    }

    private void initData()
    {
        roomItemList = new ArrayList<>();
       /* for (int i = 0 ; i < 10; i ++)
        {
            RoomItem item = new RoomItem();
            roomItemList.add(item);
        }*/
        List<Room> rooms = roomService.findAll();

        for (Room room : rooms)
        {
            RoomItem item = new RoomItem();
            item.setRoomId(room.getRoomId());
            item.setTimestamp(room.getLastChatAt());
            item.setTitle(room.getName());
            item.setType(room.getType());
            item.setLastMessage(room.getLastMessage());
            item.setUnreadCount(room.getUnreadCount());

            roomItemList.add(item);
        }

        roomItemsListView.setAdapter(new RoomItemsAdapter(roomItemList));
    }
}
