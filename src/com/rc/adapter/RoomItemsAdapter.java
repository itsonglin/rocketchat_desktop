package com.rc.adapter;

import com.rc.components.Colors;
import com.rc.components.GBC;
import com.rc.components.RCBorder;
import com.rc.entity.RoomItem;
import com.rc.utils.AvatarUtil;
import com.rc.utils.FontUtil;
import com.rc.utils.TimeUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by song on 17-5-30.
 */
public class RoomItemsAdapter extends BaseAdapter<RoomItemViewHolder>
{
    private List<RoomItem> roomItems;
    private List<RoomItemViewHolder> viewHolders = new ArrayList<>();

    public RoomItemsAdapter(List<RoomItem> roomItems)
    {
        this.roomItems = roomItems;
    }

    @Override
    public int getCount()
    {
        return roomItems.size();
    }

    @Override
    public RoomItemViewHolder onCreateViewHolder(int viewType)
    {
        return new RoomItemViewHolder();
    }

    @Override
    public void onBindViewHolder(RoomItemViewHolder viewHolder, int position)
    {
        viewHolders.add(position, viewHolder);

        RoomItem item = roomItems.get(position);
        viewHolder.roomName.setText(item.getTitle());

        ImageIcon icon = new ImageIcon();
        icon.setImage(AvatarUtil.createAvatar(item.getTitle(), item.getTitle()));
        viewHolder.avatar.setIcon(icon);


        // 消息
        viewHolder.brief.setText(item.getLastMessage());
        viewHolder.brief.setText("这是一条消息");

        // 时间
        viewHolder.time.setText(TimeUtil.diff(item.getTimestamp()));
        viewHolder.time.setText("星期一 14:30");


        // 未读消息数
        if (item.getUnreadCount() > 0)
        {
            viewHolder.unreadCount.setVisible(true);
            viewHolder.unreadCount.setText(item.getUnreadCount() + "");
        }
        else
        {
            viewHolder.unreadCount.setVisible(false);
        }
        viewHolder.unreadCount.setVisible(true);
        viewHolder.unreadCount.setText(item.getUnreadCount() + "1");



        viewHolder.addMouseListener(new MouseListener()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                setBackground(viewHolder, Colors.ITEM_SELECTED);

                for (RoomItemViewHolder holder : viewHolders)
                {
                    if (holder != viewHolder)
                    {
                        setBackground(holder, Colors.DARK);
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e)
            {

            }

            @Override
            public void mouseReleased(MouseEvent e)
            {

            }

            @Override
            public void mouseEntered(MouseEvent e)
            {

            }

            @Override
            public void mouseExited(MouseEvent e)
            {

            }
        });
    }

    private void setBackground(RoomItemViewHolder holder, Color color)
    {
        holder.setBackground(color);
        holder.nameBrief.setBackground(color);
        holder.timeUnread.setBackground(color);
    }
}
