package com.rc.adapter;

import com.rc.components.Colors;
import com.rc.components.RCBorder;
import com.rc.entity.ContactsItem;
import com.rc.listener.AbstractMouseListener;
import com.rc.utils.CharacterParser;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by song on 17-5-30.
 */
public class SelectedUserItemsAdapter extends BaseAdapter<SelectUserItemViewHolder>
{
    private List<ContactsItem> contactsItems;
    private List<SelectUserItemViewHolder> viewHolders = new ArrayList<>();
    Map<Integer, String> positionMap = new HashMap<>();
    private AbstractMouseListener mouseListener;

    public SelectedUserItemsAdapter(List<ContactsItem> contactsItems)
    {
        this.contactsItems = contactsItems;

        if (contactsItems != null)
        {
            processData();
        }
    }

    @Override
    public int getCount()
    {
        return contactsItems.size();
    }

    @Override
    public SelectUserItemViewHolder onCreateViewHolder(int viewType)
    {
        return new SelectUserItemViewHolder();
    }

    @Override
    public void onBindViewHolder(SelectUserItemViewHolder viewHolder, int position)
    {
        viewHolders.add(position, viewHolder);
        ContactsItem item = contactsItems.get(position);

        // 头像
        ImageIcon imageIcon = new ImageIcon(getClass().getResource("/image/avatar.jpg"));
        imageIcon.setImage(imageIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
        viewHolder.avatar.setIcon(imageIcon);

        // 名字
        viewHolder.roomName.setText(item.getName());

        viewHolder.addMouseListener(mouseListener);
    }


    private void processData()
    {
        Collections.sort(contactsItems);

        int index = 0;
        String lastChara = "";
        for (ContactsItem item : contactsItems)
        {
            String ch = CharacterParser.getSelling(item.getName()).substring(0, 1);
            if (!ch.equals(lastChara))
            {
                lastChara = ch;
                positionMap.put(index, ch);
            }

            index++;
        }
    }

    public void setMouseListener(AbstractMouseListener mouseListener)
    {

        this.mouseListener = mouseListener;
    }
}
