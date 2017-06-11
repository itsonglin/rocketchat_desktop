package com.rc.forms;

import com.rc.adapter.message.MessageAdapter;
import com.rc.app.Launcher;
import com.rc.components.Colors;
import com.rc.components.GBC;
import com.rc.components.RCBorder;
import com.rc.db.model.CurrentUser;
import com.rc.db.model.Message;
import com.rc.db.service.CurrentUserService;
import com.rc.db.service.MessageService;
import com.rc.entity.MessageItem;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * 右侧聊天面板
 * <p>
 * Created by song on 17-5-30.
 */
public class ChatPanel extends ParentAvailablePanel
{
    private static ChatPanel context;

    private MessagePanel messagePanel;
    private MessageEditorPanel messageEditorPanel;
    private java.util.List<MessageItem> messageItems = new ArrayList<>();


    private MessageService messageService = Launcher.messageService;
    private CurrentUserService currentUserService = Launcher.currentUserService;
    private MessageAdapter adapter;
    private CurrentUser currentUser;

    // 当前消息分页数
    private int page = 2;

    // 每次加载的消息条数
    private static final int PAGE_LENGTH = 2;


    private String roomId;


    public ChatPanel(JPanel parent)
    {
        super(parent);
        context = this;
        currentUser = currentUserService.findAll().get(0);

        initComponents();
        initView();
        initData();
    }

    private void initComponents()
    {
        messagePanel = new MessagePanel(this);
        messagePanel.setBorder(new RCBorder(RCBorder.BOTTOM, Colors.LIGHT_GRAY));
        adapter = new MessageAdapter(messageItems);
        messagePanel.getMessageListView().setAdapter(adapter);

        messageEditorPanel = new MessageEditorPanel(this);
    }


    private void initView()
    {
        this.setLayout(new GridBagLayout());
        add(messagePanel, new GBC(0, 0).setFill(GBC.BOTH).setWeight(1, 4));
        add(messageEditorPanel, new GBC(0, 1).setFill(GBC.BOTH).setWeight(1, 1));

        if (roomId == null)
        {
            messagePanel.setVisible(false);
            messageEditorPanel.setVisible(false);
        }
    }

    public static ChatPanel getContext()
    {
        return context;
    }

    public void setRoomId(String roomId)
    {
        this.roomId = roomId;
    }


    private void initData()
    {
        if (roomId != null)
        {
            messagePanel.setVisible(true);
            messageEditorPanel.setVisible(true);
            loadLocalHistory();
        }
    }

    private void loadLocalHistory()
    {
        //System.out.println(messageService.countByRoom(roomId));

        // 当前房间消息总数
        //int msgSum = messageService.countByRoom(roomId);

        List<Message> messages = messageService.findByPage(roomId, page++, PAGE_LENGTH);

        for (Message message : messages)
        {
            MessageItem item = new MessageItem(message, currentUser.getUserId());
            messageItems.add(item);
        }
        messagePanel.getMessageListView().notifyDataSetChange();
    }

    public void notifyDataSetChanged()
    {
        messageItems.clear();
        page = 1;
        initData();
    }


}
