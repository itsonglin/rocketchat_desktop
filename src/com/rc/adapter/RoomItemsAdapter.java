package com.rc.adapter;

import com.rc.components.Colors;
import com.rc.components.GBC;
import com.rc.components.RCBorder;
import com.rc.entity.RoomItem;
import com.rc.utils.FontUtil;

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

        //viewHolder.panelItem = new JPanel();
        viewHolder.setPreferredSize(new Dimension(100, 64));
        viewHolder.setBackground(Colors.DARK);
        viewHolder.setBorder(new RCBorder(RCBorder.BOTTOM));
        viewHolder.setOpaque(true);
        viewHolder.setForeground(Colors.FONT_WHITE);

        // 头像
        viewHolder.avatar = new JLabel();
        ImageIcon imageIcon = new ImageIcon(getClass().getResource("/image/avatar.jpg"));
        imageIcon.setImage(imageIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));
        viewHolder.avatar.setIcon(imageIcon);

        // 名字
        viewHolder.roomName = new JLabel();
        viewHolder.roomName.setText("aaa");
        viewHolder.roomName.setForeground(Colors.FONT_WHITE);

        // 消息
        viewHolder.brief = new JLabel();
        viewHolder.brief.setText("这是一条消息");
        viewHolder.brief.setForeground(Colors.FONT_GRAY);
        viewHolder.brief.setFont(FontUtil.getDefaultFont(12));

        viewHolder.nameBrief = new JPanel();
        viewHolder.nameBrief.setLayout(new BorderLayout());
        viewHolder.nameBrief.setBackground(Colors.DARK);
        viewHolder.nameBrief.add(viewHolder.roomName, BorderLayout.NORTH);
        viewHolder.nameBrief.add(viewHolder.brief, BorderLayout.CENTER);

        // 时间
        viewHolder.time = new JLabel();
        viewHolder.time.setText("14:51");
        viewHolder.time.setForeground(Colors.FONT_GRAY);

        // 未读消息数
        viewHolder.unreadCount = new JLabel();
        viewHolder.unreadCount.setIcon(new ImageIcon(getClass().getResource("/image/count_bg.png")));
        viewHolder.unreadCount.setPreferredSize(new Dimension(10,10));
        viewHolder.unreadCount.setForeground(Colors.FONT_WHITE);
        viewHolder.unreadCount.setText("2");
        viewHolder.unreadCount.setHorizontalTextPosition(SwingConstants.CENTER);
        viewHolder.unreadCount.setHorizontalAlignment(SwingConstants.CENTER);
        viewHolder.unreadCount.setVerticalAlignment(SwingConstants.CENTER);
        viewHolder.unreadCount.setVerticalTextPosition(SwingConstants.CENTER);


        viewHolder.timeUnread = new JPanel();
        viewHolder.timeUnread.setLayout(new BorderLayout());
        viewHolder.timeUnread.setBackground(Colors.DARK);
        viewHolder.timeUnread.add(viewHolder.time, BorderLayout.NORTH);
        viewHolder.timeUnread.add(viewHolder.unreadCount, BorderLayout.CENTER);



        viewHolder.setLayout(new GridBagLayout());
        viewHolder.add(viewHolder.avatar, new GBC(0, 0).setWeight(2, 1).setFill(GBC.BOTH).setInsets(0,5,0,0));
        viewHolder.add(viewHolder.nameBrief, new GBC(1, 0).setWeight(9, 1).setFill(GBC.BOTH));
        viewHolder.add(viewHolder.timeUnread, new GBC(2, 0).setWeight(1, 1).setFill(GBC.BOTH));


        viewHolder.addMouseListener(new MouseListener()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                for (RoomItemViewHolder holder : viewHolders)
                {
                    if (holder != viewHolder)
                    {
                        setBackground(holder, Colors.DARK);
                    }
                }
                setBackground(viewHolder, Colors.ITEM_SELECTED);
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
