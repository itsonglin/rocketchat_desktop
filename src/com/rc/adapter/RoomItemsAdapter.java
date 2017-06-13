package com.rc.adapter;

import com.rc.components.Colors;
import com.rc.entity.RoomItem;
import com.rc.forms.ChatPanel;
import com.rc.forms.TitlePanel;
import com.rc.listener.AbstractMouseListener;
import com.rc.utils.AvatarUtil;
import com.rc.utils.TimeUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by song on 17-5-30.
 */
public class RoomItemsAdapter extends BaseAdapter<RoomItemViewHolder>
{
    private List<RoomItem> roomItems;
    private List<RoomItemViewHolder> viewHolders = new ArrayList<>();
    private RoomItemViewHolder selectedViewHolder; // 当前选中的viewHolder

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
        viewHolders.add(viewHolder);

        RoomItem item = roomItems.get(position);
        viewHolder.roomName.setText(item.getTitle());

        ImageIcon icon = new ImageIcon();
        // 群组头像
        String type = item.getType();
        if (type.equals("c"))
        {
            icon.setImage(AvatarUtil.createAvatar("##", item.getTitle()));
        } else if (type.equals("p"))
        {
            icon.setImage(AvatarUtil.createAvatar("#", item.getTitle()));
        }
        // 私聊头像
        else if (type.equals("d"))
        {
            icon.setImage(AvatarUtil.createAvatar(item.getTitle(), item.getTitle()));
        }
        viewHolder.avatar.setIcon(icon);


        // 消息
        viewHolder.brief.setText(item.getLastMessage());
        //viewHolder.brief.setText("这是一条消息");
        if (item.getLastMessage() != null && item.getLastMessage().length() > 15)
        {
            viewHolder.brief.setText(item.getLastMessage().substring(0, 15) + "...");
        }else
        {
            viewHolder.brief.setText(item.getLastMessage());
        }

        // 时间
        viewHolder.time.setText(TimeUtil.diff(item.getTimestamp()));
        //viewHolder.time.setText("14:30");


        // 未读消息数
        if (item.getUnreadCount() > 0)
        {
            viewHolder.unreadCount.setVisible(true);
            viewHolder.unreadCount.setText(item.getUnreadCount() + "");
        } else
        {
            viewHolder.unreadCount.setVisible(false);
        }
        //viewHolder.unreadCount.setVisible(true);
        //viewHolder.unreadCount.setText(item.getUnreadCount() + "1");


        viewHolder.addMouseListener(new AbstractMouseListener()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                if (selectedViewHolder != viewHolder)
                {
                    for (RoomItemViewHolder holder : viewHolders)
                    {
                        if (holder != viewHolder)
                        {
                            setBackground(holder, Colors.DARK);
                        }
                    }

                    // 加载房间消息
                    ChatPanel.getContext().setRoomId(item.getRoomId());
                    ChatPanel.getContext().notifyDataSetChanged();

                   // 更新房间标题
                    TitlePanel.getContext().updateRoomTitle(item.getRoomId());

                    setBackground(viewHolder, Colors.ITEM_SELECTED);
                    selectedViewHolder = viewHolder;
                }

                /*ChatPanel.getContext().setRoomId(item.getRoomId());
                ChatPanel.getContext().notifyDataSetChanged();*/

            }


            @Override
            public void mouseEntered(MouseEvent e)
            {
                if (selectedViewHolder != viewHolder)
                {
                    setBackground(viewHolder, Colors.ITEM_SELECTED_DARK);
                }
            }

            @Override
            public void mouseExited(MouseEvent e)
            {
                if (selectedViewHolder != viewHolder)
                {
                    setBackground(viewHolder, Colors.DARK);
                }
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
