package com.rc.forms;

import com.rc.adapter.SelectUserItemViewHolder;
import com.rc.adapter.SelectUserItemsAdapter;
import com.rc.adapter.SelectedUserItemsAdapter;
import com.rc.components.*;
import com.rc.entity.ContactsItem;
import com.rc.listener.AbstractMouseListener;
import com.rc.utils.IconUtil;

import javax.swing.*;
import javax.swing.border.LineBorder;
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
    private JPanel buttonPanel;
    private JButton cancelButton;
    private JButton okButton;

    public static final int DIALOG_WIDTH = 600;
    public static final int DIALOG_HEIGHT = 500;

    private List<ContactsItem> contactsItemList = new ArrayList<>();
    private SelectUserItemsAdapter selectUserItemsAdapter;
    private SelectedUserItemsAdapter selectedUserItemsAdapter;
    private ImageIcon checkIcon;
    private ImageIcon uncheckIcon;


    public CreateGroupDialog(Frame owner, boolean modal)
    {
        super(owner, modal);
        initComponents();
        setListeners();
        initView();
    }

    @Override
    public void paintComponents(Graphics g)
    {
        super.paintComponents(g);

        System.out.println("aaa");
        g.setColor(Color.RED);
        g.drawRect(getX(), getY(), getWidth() - 1,getHeight() - 1);
    }


    private void initComponents()
    {
        checkIcon = IconUtil.getIcon(this, "/image/check.png");
        uncheckIcon = IconUtil.getIcon(this, "/image/uncheck.png");

        int posX = MainFrame.getContext().getX();
        int posY = MainFrame.getContext().getY();

        posX = posX + (MainFrame.getContext().currentWindowWidth - DIALOG_WIDTH) / 2;
        posY = posY + (MainFrame.getContext().currentWindowHeight - DIALOG_HEIGHT) / 2;
        setBounds(posX, posY, DIALOG_WIDTH, DIALOG_HEIGHT);
        setUndecorated(true);


        leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(DIALOG_WIDTH / 2 - 1, DIALOG_HEIGHT - 13));
        leftPanel.setBorder(new RCBorder(RCBorder.RIGHT, Colors.LIGHT_GRAY));
        //leftPanel.setBorder(new LineBorder(Colors.RED));

        rightPanel = new JPanel();
        rightPanel.setPreferredSize(new Dimension(DIALOG_WIDTH / 2 - 1, DIALOG_HEIGHT - 13));


        // 选择用户列表
        selectUserListView = new RCListView();
        getContacts();
        selectUserItemsAdapter = new SelectUserItemsAdapter(contactsItemList);
        selectUserItemsAdapter.setMouseListener(new AbstractMouseListener(){
            @Override
            public void mouseClicked(MouseEvent e)
            {
                SelectUserItemViewHolder holder = (SelectUserItemViewHolder) e.getSource();

                if (holder.active)
                {
                    holder.icon.setIcon(uncheckIcon);
                    holder.active = false;

                }else
                {
                    holder.icon.setIcon(checkIcon);
                    holder.active = true;
                }

                System.out.println(holder.roomName.getText());
            }
        });
        selectUserListView.setScrollBarColor(Colors.SCROLL_BAR_THUMB, Colors.WINDOW_BACKGROUND);
        selectUserListView.setAdapter(selectUserItemsAdapter);

        // 已选中用户列表
        selectedUserListView = new RCListView();
        selectedUserItemsAdapter = new SelectedUserItemsAdapter(contactsItemList);
        selectedUserListView.setScrollBarColor(Colors.SCROLL_BAR_THUMB, Colors.WINDOW_BACKGROUND);
        selectedUserListView.setAdapter(selectedUserItemsAdapter);


       // 按钮组
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        cancelButton = new RCButton("取消");
        cancelButton.setForeground(Colors.FONT_BLACK);

        okButton = new RCButton("创建", Colors.MAIN_COLOR, Colors.MAIN_COLOR_DARKER, Colors.MAIN_COLOR_DARKER);
        okButton.setBackground(Colors.PROGRESS_BAR_START);
    }

    private void setListeners()
    {
        cancelButton.addMouseListener(new AbstractMouseListener(){
            @Override
            public void mouseClicked(MouseEvent e)
            {
                setVisible(false);

                super.mouseClicked(e);
            }
        });
    }

    private void initView()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 10));
        panel.add(leftPanel);
        panel.add(rightPanel);
        panel.setBorder(new LineBorder(Colors.FONT_GRAY));
        add(panel);


        leftPanel.setLayout(new GridBagLayout());
        leftPanel.add(selectUserListView, new GBC(0, 0).setFill(GBC.BOTH).setWeight(1, 1).setInsets(0,0,5,0));


        buttonPanel.add(cancelButton, new GBC(0, 0).setWeight(1, 1));
        buttonPanel.add(okButton, new GBC(1, 0).setWeight(1, 1));

        rightPanel.setLayout(new GridBagLayout());
        rightPanel.add(selectedUserListView, new GBC(0, 0).setFill(GBC.BOTH).setWeight(1, 60));
        rightPanel.add(buttonPanel, new GBC(0, 1).setFill(GBC.BOTH).setWeight(1, 1).setInsets(5, 0,0,0));


        //leftPanel.add(selectUserListView);
        //rightPanel.add(selectedUserListView);
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
}
