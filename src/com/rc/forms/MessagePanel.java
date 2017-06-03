package com.rc.forms;

import com.rc.adapter.message.MessageAdapter;
import com.rc.components.RCListView;
import com.rc.entity.MessageItem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by song on 17-5-30.
 */
public class MessagePanel extends ParentAvailablePanel
{
    RCListView listView;
    MessageAdapter adapter;

    private List<MessageItem> messageItems;

    public MessagePanel(JPanel parent)
    {
        super(parent);

        initComponents();
        initView();
    }


    private void initComponents()
    {
        getData();

        adapter = new MessageAdapter(messageItems);
        listView = new RCListView(0, 20);
        //listView.setVisible(false);
        listView.setAdapter(adapter);

    }

    private void initView()
    {
        this.setLayout(new BorderLayout());

        add(listView, BorderLayout.CENTER);
        /*listView.repaint();
        listView.setVisible(true);*/


        /*addComponentListener(new ComponentAdapter()
        {
            @Override
            public void componentResized(ComponentEvent e)
            {
                listView.repaint();
            }
        });*/

    }

    private void getData()
    {
        MessageItem item = new MessageItem();
        item.setMessageType(MessageItem.RIGHT_TEXT);
        item.setMessageContent("尊敬的用户您好s        网络时长尊敬的用户您好：本公sd sdad wbe    jj司对规划   监控安装，介绍有福利和返佣.网络维修和介绍请致电18928914412 或微信kuandaikefu10000");
        item.setTimestamp(System.currentTimeMillis());

        MessageItem item2 = new MessageItem();
        item2.setMessageType(MessageItem.RIGHT_TEXT);
        item2.setMessageContent("http://www.baidu.com");
        item2.setTimestamp(System.currentTimeMillis());

        MessageItem item3 = new MessageItem();
        item3.setMessageType(MessageItem.RIGHT_TEXT);
        item3.setMessageContent("addComponentListener(new ComponentAdapter()");
        item3.setTimestamp(System.currentTimeMillis());

        messageItems = new ArrayList<>();
        messageItems.add(item);
        messageItems.add(item2);
        messageItems.add(item3);
    }
}
