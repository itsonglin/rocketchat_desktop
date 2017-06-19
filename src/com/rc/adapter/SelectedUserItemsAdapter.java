package com.rc.adapter;

import com.rc.entity.ContactsItem;
import com.rc.listener.AbstractMouseListener;
import com.rc.utils.AvatarUtil;
import com.rc.utils.CharacterParser;

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
    private List<String> userList;
    Map<Integer, String> positionMap = new HashMap<>();
    private ItemRemoveListener itemRemoveListener;

    public SelectedUserItemsAdapter(List<String> userList)
    {
        this.userList = userList;

        if (userList != null)
        {
            processData();
        }
    }

    @Override
    public int getCount()
    {
        return userList.size();
    }

    @Override
    public SelectedUserItemViewHolder onCreateViewHolder(int viewType)
    {
        return new SelectedUserItemViewHolder();
    }

    @Override
    public void onBindViewHolder(SelectedUserItemViewHolder viewHolder, int position)
    {

        String user = userList.get(position);

        // 头像
        ImageIcon imageIcon = new ImageIcon(AvatarUtil.createOrLoadUserAvatar(user).getScaledInstance(30,30,Image.SCALE_SMOOTH));
        viewHolder.avatar.setIcon(imageIcon);

        // 名字
        viewHolder.username.setText(user);

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
        Collections.sort(userList);

        int index = 0;
        String lastChara = "";
        for (String user : userList)
        {
            String ch = CharacterParser.getSelling(user).substring(0, 1);
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
