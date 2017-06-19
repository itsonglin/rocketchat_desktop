package com.rc.forms;

import com.rc.adapter.SelectUserItemViewHolder;
import com.rc.adapter.SelectUserItemsAdapter;
import com.rc.adapter.SelectedUserItemsAdapter;
import com.rc.components.*;
import com.rc.entity.ContactsItem;
import com.rc.listener.AbstractMouseListener;
import com.rc.utils.IconUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by song on 19/06/2017.
 */
public class SelectUserPanel extends JPanel
{
    private JPanel leftPanel;
    private JPanel rightPanel;
    private RCListView selectUserListView;
    private RCListView selectedUserListView;

    /*private JPanel buttonPanel;
    private JButton cancelButton;
    private JButton okButton;*/

    public static final int DIALOG_WIDTH = 600;
    public static final int DIALOG_HEIGHT = 500;

    private List<String> leftUserList;
    private List<String> selectedUserList = new ArrayList<>();
    private SelectUserItemsAdapter selectUserItemsAdapter;
    private SelectedUserItemsAdapter selectedUserItemsAdapter;
    private ImageIcon checkIcon;
    private ImageIcon uncheckIcon;
    private List<SelectUserItemViewHolder> selectedHolders = new ArrayList<>();



    public SelectUserPanel(List<String> leftUserList)
    {
        this.leftUserList = leftUserList;
        initComponents();
        initView();
    }


    private void initComponents()
    {
        checkIcon = IconUtil.getIcon(this, "/image/check.png");
        uncheckIcon = IconUtil.getIcon(this, "/image/uncheck.png");

        leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(DIALOG_WIDTH / 2 - 1, DIALOG_HEIGHT - 13));
        leftPanel.setBorder(new RCBorder(RCBorder.RIGHT, Colors.LIGHT_GRAY));

        rightPanel = new JPanel();
        rightPanel.setPreferredSize(new Dimension(DIALOG_WIDTH / 2 - 1, DIALOG_HEIGHT - 13));


        // 选择用户列表
        selectUserListView = new RCListView();
        getContacts();

        selectUserItemsAdapter = new SelectUserItemsAdapter(leftUserList);
        selectUserItemsAdapter.setMouseListener(new AbstractMouseListener()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                SelectUserItemViewHolder holder = (SelectUserItemViewHolder) e.getSource();

                String username = holder.username.getText();
                if (unSelectUser(username))
                {
                    holder.icon.setIcon(uncheckIcon);
                    selectedHolders.remove(holder);
                }
                else
                {
                    selectUser(username);
                    holder.icon.setIcon(checkIcon);
                    selectedHolders.add(holder);
                }




            }
        });
        selectUserListView.setScrollBarColor(Colors.SCROLL_BAR_THUMB, Colors.WINDOW_BACKGROUND);
        selectUserListView.setAdapter(selectUserItemsAdapter);

        // 已选中用户列表
        selectedUserListView = new RCListView();
        selectedUserItemsAdapter = new SelectedUserItemsAdapter(selectedUserList);
        selectedUserItemsAdapter.setItemRemoveListener(new SelectedUserItemsAdapter.ItemRemoveListener()
        {
            @Override
            public void onRemove(String username)
            {
                if (unSelectUser(username))
                {
                    for (SelectUserItemViewHolder holder : selectedHolders)
                    {
                        if (holder.username.getText().equals(username))
                        {
                            holder.icon.setIcon(uncheckIcon);
                            break;
                        }
                    }
                }
            }
        });
        selectedUserListView.setScrollBarColor(Colors.SCROLL_BAR_THUMB, Colors.WINDOW_BACKGROUND);
        selectedUserListView.setAdapter(selectedUserItemsAdapter);
    }

    private void initView()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 10));
        panel.add(leftPanel);
        panel.add(rightPanel);
        add(panel);


        leftPanel.setLayout(new GridBagLayout());
        leftPanel.add(selectUserListView, new GBC(0, 0).setFill(GBC.BOTH).setWeight(1, 1).setInsets(0, 0, 5, 0));

        rightPanel.setLayout(new GridBagLayout());
        rightPanel.add(selectedUserListView, new GBC(0, 0).setFill(GBC.BOTH).setWeight(1, 60));
    }

    private void getContacts()
    {
        leftUserList.add("阿哥");

        leftUserList.add("讨论组");

        leftUserList.add("波哥");

        leftUserList.add("不好");


        for (int i = 0; i < 10; i++)
        {
            leftUserList.add("User " + i);
        }
    }

    /**
     * 选择一位用户
     *
     * @param username
     */
    private void selectUser(String username)
    {
        for (String user : leftUserList)
        {
            if (user.equals(username))
            {
                selectedUserList.add(user);
                selectedUserListView.notifyDataSetChanged(false);

            }
        }
    }

    private boolean unSelectUser(String username)
    {
        Iterator<String> itemIterator = selectedUserList.iterator();
        boolean dataChanged = false;
        while (itemIterator.hasNext())
        {
            String user = itemIterator.next();
            if (user.equals(username))
            {
                dataChanged = true;
                itemIterator.remove();
                break;
            }
        }

        if (dataChanged)
        {
            selectedUserListView.notifyDataSetChanged(false);
        }

        return dataChanged;
    }

    public List<String> getSelectedUser()
    {
        return selectedUserList;
    }

}
