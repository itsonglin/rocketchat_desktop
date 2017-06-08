package com.rc.adapter;

import com.rc.entity.ContactsItem;
import com.rc.listener.AbstractMouseListener;
import com.rc.utils.CharacterParser;
import com.rc.utils.IconUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

/**
 * Created by song on 17-5-30.
 */
public class SelectedUserItemsAdapter extends BaseAdapter<SelectedUserItemViewHolder>
{
    private List<ContactsItem> contactsItems;
    Map<Integer, String> positionMap = new HashMap<>();
    private ItemRemoveListener itemRemoveListener;

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
    public SelectedUserItemViewHolder onCreateViewHolder(int viewType)
    {
        return new SelectedUserItemViewHolder();
    }

    @Override
    public void onBindViewHolder(SelectedUserItemViewHolder viewHolder, int position)
    {

        ContactsItem item = contactsItems.get(position);

        // 头像
        ImageIcon imageIcon = new ImageIcon(getClass().getResource("/image/avatar.jpg"));
        imageIcon.setImage(imageIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
        viewHolder.avatar.setIcon(imageIcon);

        // 名字
        viewHolder.username.setText(item.getName());

        /*viewHolder.icon.setIcon(IconUtil.getIcon(this, "/image/remove.png", 18, 18));
        viewHolder.icon.setToolTipText("移除");*/

        viewHolder.icon.addMouseListener(new AbstractMouseListener()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                if (itemRemoveListener != null)
                {
                    itemRemoveListener.onRemove(viewHolder.username.getText());
                }
                super.mouseClicked(e);
            }
        });
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

    public void setItemRemoveListener(ItemRemoveListener itemRemoveListener)
    {
        this.itemRemoveListener = itemRemoveListener;
    }


    public interface ItemRemoveListener
    {
        void onRemove(String username);
    }

}
