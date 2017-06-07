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

        for (int i = 0 ;i < 10; i++)
        {
            ContactsItem contactsItem = new ContactsItem();
            contactsItem.setName("User " + i);
            contactsItemList.add(contactsItem);
        }

        contactsListView.setAdapter(new ContactsItemsAdapter(contactsItemList));
    }

    private void initView()
    {
        setLayout(new GridBagLayout());
        contactsListView.setContentPanelBackground(Colors.DARK);
        add(contactsListView, new GBC(0, 0).setFill(GBC.BOTH).setWeight(1, 1));
    }

}
