package com.rc.forms;

import com.rc.adapter.RoomMembersAdapter;
import com.rc.app.Launcher;
import com.rc.components.Colors;
import com.rc.components.GBC;
import com.rc.components.RCButton;
import com.rc.components.RCListView;
import com.rc.db.model.CurrentUser;
import com.rc.db.model.Room;
import com.rc.db.service.CurrentUserService;
import com.rc.db.service.RoomService;
import com.sun.javaws.jnl.LaunchDesc;

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
    private String roomId;
    private RoomService roomService = Launcher.roomService;
    private CurrentUserService currentUserService = Launcher.currentUserService;
    private CurrentUser currentUser;
    private Room room;

    public RoomMembersPanel(JPanel parent)
    {
        super(parent);
        roomMembersPanel = this;

        initComponents();
        initView();

        currentUser = currentUserService.findAll().get(0);
    }

    private void initComponents()
    {
        setBorder(new LineBorder(Colors.LIGHT_GRAY));
        setBackground(Colors.FONT_WHITE);

        setPreferredSize(new Dimension(ROOM_MEMBER_PANEL_WIDTH, MainFrame.getContext().currentWindowHeight));
        setVisible(false);
        listView.setScrollBarColor(Colors.SCROLL_BAR_THUMB, Colors.WINDOW_BACKGROUND);
        listView.setContentPanelBackground(Colors.FONT_WHITE);

        operationPanel.setPreferredSize(new Dimension(60, 80));
        operationPanel.setBackground(Colors.FONT_WHITE);



        leaveButton.setText("退出群聊");
        leaveButton.setForeground(Colors.RED);
        leaveButton.setPreferredSize(new Dimension(180, 30));

    }

    private void initView()
    {
        operationPanel.add(leaveButton);

        setLayout(new GridBagLayout());
        add(listView, new GBC(0, 0).setFill(GBC.BOTH).setWeight(1, 70));
        add(operationPanel, new GBC(0, 1).setFill(GBC.BOTH).setWeight(1, 1).setInsets(5, 0, 0, 0));

        listView.setAdapter(new RoomMembersAdapter(members));
    }

    public void setRoomId(String roomId)
    {
        this.roomId = roomId;
        room = roomService.findById(roomId);
    }

    @Override
    public void setVisible(boolean aFlag)
    {
        if (aFlag)
        {
            if (roomId != null)
            {
                getRoomMembers();

                // 单独聊天，不显示退出按钮
                if (room.getType().equals("d"))
                {
                    leaveButton.setVisible(false);
                }else
                {
                    leaveButton.setVisible(true);
                }

                super.setVisible(aFlag);
                listView.notifyDataSetChanged(false);
            }
        }

        super.setVisible(aFlag);
    }

    private void getRoomMembers()
    {
        members.clear();

        // 单独聊天，成员只显示两人
        if (room.getType().equals("d"))
        {
            members.add(currentUser.getUsername());
            members.add(room.getName());
        }
        else
        {
            if (isRoomCreator())
            {
                members.remove("添加成员");
                members.add("添加成员");

                if (members.size() > 2)
                {
                    members.remove("删除成员");
                    members.add("删除成员");
                }
            }

            String roomMembers = room.getMember();
            if (room.getCreatorName() != null)
            {
                members.add(room.getCreatorName());
            }

            if (roomMembers != null)
            {
                String[] userArr = roomMembers.split(",");
                for (int i = 0; i < userArr.length; i++)
                {
                    if (!members.contains(userArr[i]))
                    {
                        members.add(userArr[i]);
                    }
                }
            }
        }
    }


    /**
     * 判断当前用户是否是房间创建者
     *
     * @return
     */
    private boolean isRoomCreator()
    {
        return room.getCreatorName() != null && room.getCreatorName().equals(currentUser.getUsername());
    }


    public static RoomMembersPanel getContext()
    {
        return roomMembersPanel;
    }
}
