package com.rc.forms;

import com.rc.adapter.ContactsItemsAdapter;
import com.rc.adapter.RoomItemsAdapter;
import com.rc.components.Colors;
import com.rc.components.GBC;
import com.rc.components.RCListView;
import com.rc.entity.ContactsItem;
import com.rc.entity.RoomItem;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by song on 17-5-30.
 */
public class ContactsPanel extends ParentAvailablePanel
{
    private RCListView contactsListView;
    private List<ContactsItem> contactsItemList;

    public ContactsPanel(JPanel parent)
    {
        super(parent);
        initComponents();
        initView();
    }

    private void initComponents()
    {
        contactsListView = new RCListView();

        contactsItemList = new ArrayList<>();
        /*for (int i = 0 ; i < 10; i ++)
        {
            ContactsItem item = new ContactsItem();
            item.setName("User用户" + i);
            contactsItemList.add(item);
        }*/
        ContactsItem item = new ContactsItem();
        item.setName("阿哥");
        contactsItemList.add(item);

        ContactsItem item2 = new ContactsItem();
        item2.setName("讨论组");
        contactsItemList.add(item2);

        ContactsItem item3 = new ContactsItem();
        item3.setName("波哥");
        contactsItemList.add(item3);

        ContactsItem item4 = new ContactsItem();
        item4.setName("不好");
        contactsItemList.add(item4);

        ContactsItem item5 = new ContactsItem();
        item5.setName("123");
        contactsItemList.add(item5);

        contactsListView.setAdapter(new ContactsItemsAdapter(contactsItemList));

        //getData();

        /*JTable table = new JTable();
        table.setTableHeader(null);
        table.setModel(roomItemModel);
        table.getColumnModel().getColumn(0).setMaxWidth(100);
        table.getColumnModel().getColumn(0).setCellRenderer(new RoomItemRenderer());
        //scrollPane.setViewportView(table);
        table.setBackground(Color.white);
        table.setOpaque(true);
        table.setShowHorizontalLines(false);*/

        //JPanel panel = new JPanel();
        //panel.setLayout(new GridBagLayout());
        //panel.setBackground(Colors.DARK);

        /*for (int i = 0; i < 20; i++)
        {
            JPanel panelItem = new JPanel();
            panelItem.setPreferredSize(new Dimension(100, 64));
            panelItem.setBackground(Colors.DARK);
            panelItem.setBorder(new RCBorder(RCBorder.BOTTOM));
            panelItem.setOpaque(true);
            panelItem.setForeground(Colors.FONT_WHITE);

            // 头像
            JLabel avatar = new JLabel();
            ImageIcon imageIcon = new ImageIcon(getClass().getResource("/image/avatar.jpg"));
            imageIcon.setImage(imageIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));
            avatar.setIcon(imageIcon);

            // 名字
            JLabel roomName = new JLabel();
            roomName.setText("aaa");
            roomName.setForeground(Colors.FONT_WHITE);

            // 消息
            JLabel brief = new JLabel();
            brief.setText("这是一条消息");
            brief.setForeground(Colors.FONT_GRAY);
            brief.setFont(FontUtil.getDefaultFont(12));

            JPanel nameBrief = new JPanel();
            nameBrief.setLayout(new BorderLayout());
            nameBrief.setBackground(Colors.DARK);
            nameBrief.add(roomName, BorderLayout.NORTH);
            nameBrief.add(brief, BorderLayout.CENTER);

            // 时间
            JLabel time = new JLabel();
            time.setText("14:51");
            time.setForeground(Colors.FONT_GRAY);

            // 未读消息数
            JLabel unreadCount = new JLabel();
            unreadCount.setIcon(new ImageIcon(getClass().getResource("/image/count_bg.png")));
            unreadCount.setPreferredSize(new Dimension(10,10));
            unreadCount.setForeground(Colors.FONT_WHITE);
            unreadCount.setText("2");
            unreadCount.setHorizontalTextPosition(SwingConstants.CENTER);
            unreadCount.setHorizontalAlignment(SwingConstants.CENTER);
            unreadCount.setVerticalAlignment(SwingConstants.CENTER);
            unreadCount.setVerticalTextPosition(SwingConstants.CENTER);


            JPanel timeUnread = new JPanel();
            timeUnread.setLayout(new BorderLayout());
            timeUnread.setBackground(Colors.DARK);
            timeUnread.add(time, BorderLayout.NORTH);
            timeUnread.add(unreadCount, BorderLayout.CENTER);

            panelItem.setLayout(new GridBagLayout());
            panelItem.add(avatar, new GBC(0, 0).setWeight(2, 1).setFill(GBC.BOTH).setInsets(0,5,0,0));
            panelItem.add(nameBrief, new GBC(1, 0).setWeight(9, 1).setFill(GBC.BOTH));
            panelItem.add(timeUnread, new GBC(2, 0).setWeight(1, 1).setFill(GBC.BOTH));

            panel.add(panelItem, new GBC(0, i).setFill(GBC.BOTH).setWeight(1, 1));
        }

        scrollPane = new JScrollPane();
        scrollPane.setViewportView(panel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(17);
        scrollPane.setBackground(Colors.DARK);

        //scrollPane = new JScrollPane(table);
        //add(scrollPane, BorderLayout.CENTER);
        //scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.getVerticalScrollBar().setUI(new ScrollUI());*/
    }

    private void initView()
    {
        setLayout(new GridBagLayout());
        contactsListView.setContentPanelBackground(Colors.DARK);
        add(contactsListView, new GBC(0, 0).setFill(GBC.BOTH).setWeight(1, 1));
    }

}
