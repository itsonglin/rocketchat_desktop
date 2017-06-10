package com.rc.forms;

import com.rc.adapter.ContactsItemsAdapter;
import com.rc.adapter.RoomItemsAdapter;
import com.rc.app.Launcher;
import com.rc.components.Colors;
import com.rc.components.GBC;
import com.rc.components.RCListView;
import com.rc.db.model.ContactsUser;
import com.rc.db.service.ContactsUserService;
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
    private static ContactsPanel context;

    private RCListView contactsListView;
    private List<ContactsItem> contactsItemList;
    private ContactsUserService contactsUserService = Launcher.contactsUserService;

    public ContactsPanel(JPanel parent)
    {
        super(parent);
        context = this;

        initComponents();
        initView();
        initData();
    }


    private void initComponents()
    {
        contactsListView = new RCListView();
    }

    private void initView()
    {
        setLayout(new GridBagLayout());
        contactsListView.setContentPanelBackground(Colors.DARK);
        add(contactsListView, new GBC(0, 0).setFill(GBC.BOTH).setWeight(1, 1));
    }

    private void initData()
    {
        contactsItemList = new ArrayList<>();

        List<ContactsUser> contactsUsers = contactsUserService.findAll();
        for (ContactsUser contactsUser : contactsUsers)
        {
            ContactsItem item = new ContactsItem(contactsUser.getUserId(),
                    contactsUser.getUsername(), "d");

            contactsItemList.add(item);
        }

        contactsListView.setAdapter(new ContactsItemsAdapter(contactsItemList));
    }

    public void notifyDataSetChanged()
    {
        initData();
        contactsListView.notifyDataSetChange();
    }

    public static ContactsPanel getContext()
    {
        return context;
    }
}
