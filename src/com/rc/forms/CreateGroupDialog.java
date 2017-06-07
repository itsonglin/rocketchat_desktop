package com.rc.forms;

import com.rc.adapter.SelectUserItemViewHolder;
import com.rc.adapter.SelectUserItemsAdapter;
import com.rc.components.Colors;
import com.rc.components.GBC;
import com.rc.components.RCBorder;
import com.rc.components.RCListView;
import com.rc.entity.ContactsItem;
import com.rc.listener.AbstractMouseListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by song on 07/06/2017.
 */
public class CreateGroupDialog extends JDialog
{
    private JPanel leftPanel;
    private JPanel rightPanel;
    private RCListView selectUserListView;
    private RCListView selectedUserListView;

    public static final int DIALOG_WIDTH = 600;
    public static final int DIALOG_HEIGHT = 500;

    private List<ContactsItem> contactsItemList = new ArrayList<>();
    private SelectUserItemsAdapter selectUserItemsAdapter;


    public CreateGroupDialog(Frame owner, boolean modal)
    {
        super(owner, modal);
        initComponents();
        initView();
    }

    private void initComponents()
    {
        int posX = MainFrame.getContext().getX();
        int posY = MainFrame.getContext().getY();

        posX = posX + (MainFrame.getContext().currentWindowWidth - DIALOG_WIDTH) / 2;
        posY = posY + (MainFrame.getContext().currentWindowHeight - DIALOG_HEIGHT) / 2;
        setBounds(posX, posY, DIALOG_WIDTH, DIALOG_HEIGHT);
        setUndecorated(true);

        leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(DIALOG_WIDTH / 2, DIALOG_HEIGHT));
        leftPanel.setBorder(new RCBorder(RCBorder.RIGHT, Colors.LIGHT_GRAY));
        //leftPanel.setBorder(new LineBorder(Colors.RED));

        rightPanel = new JPanel();
        rightPanel.setPreferredSize(new Dimension(DIALOG_WIDTH / 2, DIALOG_HEIGHT));


        selectUserListView = new RCListView();
        getContacts();

        selectUserItemsAdapter = new SelectUserItemsAdapter(contactsItemList);
        selectUserItemsAdapter.setMouseListener(new AbstractMouseListener(){
            @Override
            public void mouseClicked(MouseEvent e)
            {
                SelectUserItemViewHolder holder = (SelectUserItemViewHolder) e.getSource();
                System.out.println(holder.roomName.getText());
            }
        });
        selectUserListView.setAdapter(selectUserItemsAdapter);


        selectedUserListView = new RCListView();

    }

    private void getContacts()
    {
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

        for (int i = 0; i < 10; i++)
        {
            ContactsItem contactsItem = new ContactsItem();
            contactsItem.setName("User " + i);
            contactsItemList.add(contactsItem);
        }
    }

    private void initView()
    {
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        add(leftPanel);
        add(rightPanel);


        leftPanel.setLayout(new GridBagLayout());
        leftPanel.add(selectUserListView, new GBC(0, 0).setFill(GBC.BOTH).setWeight(1, 1));
        selectUserListView.setScrollBarColor(Colors.SCROLL_BAR_THUMB, Colors.WINDOW_BACKGROUND);

        rightPanel.setLayout(new GridBagLayout());
        rightPanel.add(selectedUserListView, new GBC(0, 0).setFill(GBC.BOTH).setWeight(1, 1));
        selectedUserListView.setScrollBarColor(Colors.SCROLL_BAR_THUMB, Colors.WINDOW_BACKGROUND);

        //leftPanel.add(selectUserListView);
        //rightPanel.add(selectedUserListView);
    }
}
