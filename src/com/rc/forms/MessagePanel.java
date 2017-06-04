package com.rc.forms;

import com.rc.adapter.message.MessageAdapter;
import com.rc.components.RCListView;
import com.rc.entity.FileAttachmentItem;
import com.rc.entity.ImageAttachmentItem;
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
        item.setMessageContent("尊敬、大厅dsad124567890-=12fsdbnmvb qiofqeseOjojoihiu返佣.网络维修和介绍请致电18928914412 或微信kuandaikefu10000");
        item.setTimestamp(System.currentTimeMillis());

        MessageItem item2 = new MessageItem();
        item2.setMessageType(MessageItem.LEFT_TEXT);
        item2.setMessageContent("http://www.baidu.com");
        item2.setTimestamp(System.currentTimeMillis());

        MessageItem item3 = new MessageItem();
        item3.setMessageType(MessageItem.RIGHT_TEXT);
        item3.setMessageContent("addComponentListener(new Cpter()你好你好啊，。/");
        item3.setTimestamp(System.currentTimeMillis());

        MessageItem item4 = new MessageItem();
        item4.setMessageType(MessageItem.LEFT_TEXT);
        item4.setMessageContent("一、不得利用本站危害国家安全、泄露国家秘密，利用本站危害国家安全、泄露国家秘密，不得侵犯国家社会利用本站危害国家安全、泄露国家秘密，不得侵犯国家社会利用本站危害国家安全、泄露国家秘密，不得侵犯国家社会利用本站危害国家安全、泄露国家秘密，不得侵犯国家社会利用本站危害国家安全、泄露国家秘密，不得侵犯国家社会利用本站危害国家安全、泄露国家秘密，不得侵犯国家社会利用本站危害国家安全、泄露国家秘密，不得侵犯国家社会利用本站危害国家安全、泄露国家秘密，不得侵犯国家社会利用本站危害国家安全、泄露国家秘密，不得侵犯国家社会利用本站危害国家安全、泄露国家秘密，不得侵犯国家社会利用本站危害国家安全、泄露国家秘密，不得侵犯国家社会利用本站危害国家安全、泄露国家秘密，不得侵犯国家社会利用本站危害国家安全、泄露国家秘密，不得侵犯国家社会不得侵犯国家社会集体的和公民的合法权益，不得利用本站制作、复制和传播不法有害信息！");
        item4.setTimestamp(System.currentTimeMillis());

        MessageItem item5 = new MessageItem();
        item5.setMessageType(MessageItem.LEFT_IMAGE);
        item5.setMessageContent("图片");
        item5.setSenderUsername("Songlin");
        item5.setTimestamp(System.currentTimeMillis());
        item5.getImageAttachments().add(0,new ImageAttachmentItem("/image/pdf.png"));

        MessageItem item6 = new MessageItem();
        item6.setMessageType(MessageItem.RIGHT_IMAGE);
        item6.setMessageContent("图片");
        item6.setSenderUsername("Songlin");
        item6.setTimestamp(System.currentTimeMillis());
        item6.getImageAttachments().add(0,new ImageAttachmentItem("/image/avatar.jpg"));

        MessageItem item7 = new MessageItem();
        item7.setMessageType(MessageItem.RIGHT_ATTACHMENT);
        item7.setMessageContent("111.pdf");
        item7.setSenderUsername("Songlin");
        item7.setTimestamp(System.currentTimeMillis());
        item7.getFileAttachments().add(0,new FileAttachmentItem("官网使用手册.pdf"));

        messageItems = new ArrayList<>();
        messageItems.add(item);
       //] messageItems.add(item2);
       messageItems.add(item3);
//        messageItems.add(item5);
//        messageItems.add(item6);
        messageItems.add(item7);
        //messageItems.add(item4);
    }
}
