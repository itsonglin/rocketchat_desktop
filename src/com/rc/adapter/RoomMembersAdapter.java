package com.rc.adapter;

import com.rc.components.Colors;
import com.rc.components.message.MessagePopupMenu;
import com.rc.forms.UserInfoPopup;
import com.rc.listener.AbstractMouseListener;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicPopupMenuUI;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by song on 07/06/2017.
 */
public class RoomMembersAdapter extends BaseAdapter<RoomMembersItemViewHolder>
{
    private List<String> members;
    private List<RoomMembersItemViewHolder> viewHolders = new ArrayList<>();

    public RoomMembersAdapter(List<String> members)
    {

        this.members = members;
    }

    @Override
    public RoomMembersItemViewHolder onCreateViewHolder(int viewType)
    {
        return new RoomMembersItemViewHolder();
    }

    @Override
    public void onBindViewHolder(RoomMembersItemViewHolder viewHolder, int position)
    {
        viewHolders.add(viewHolder);

        String name = members.get(position);
        viewHolder.roomName.setText(name);

        if (name.equals("添加成员") || name.equals("删除成员"))
        {
            viewHolder.setCursor(new Cursor(Cursor.HAND_CURSOR));

            ImageIcon imageIcon = new ImageIcon(getClass().getResource("/image/add_member.png"));
            imageIcon.setImage(imageIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
            viewHolder.avatar.setIcon(imageIcon);

            viewHolder.addMouseListener(new AbstractMouseListener()
            {
                @Override
                public void mouseClicked(MouseEvent e)
                {
                    System.out.println("添加/刪除用戶");
                }

                @Override
                public void mouseEntered(MouseEvent e)
                {
                    viewHolder.setBackground(Colors.ITEM_SELECTED_LIGHT);
                    super.mouseEntered(e);
                }

                @Override
                public void mouseExited(MouseEvent e)
                {
                    viewHolder.setBackground(Colors.WINDOW_BACKGROUND_LIGHT);

                }
            });
        }
        else
        {
            ImageIcon imageIcon = new ImageIcon(getClass().getResource("/image/avatar.jpg"));
            imageIcon.setImage(imageIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
            viewHolder.avatar.setIcon(imageIcon);

            UserInfoPopup userInfoPopup = new UserInfoPopup();


            viewHolder.addMouseListener(new AbstractMouseListener()
            {
                @Override
                public void mouseClicked(MouseEvent e)
                {
                    viewHolder.setBackground(Colors.ITEM_SELECTED_LIGHT);

                    // 弹出用户信息面板
                    if (e.getButton() == MouseEvent.BUTTON1)
                    {
                        userInfoPopup.show(e.getComponent(), e.getX(), e.getY());
                    }


                    for (RoomMembersItemViewHolder holder : viewHolders)
                    {
                        if (holder != viewHolder)
                        {
                            holder.setBackground(Colors.WINDOW_BACKGROUND_LIGHT);
                        }
                    }

                }
            });
        }

    }

    @Override
    public int getCount()
    {
        return members.size();
    }
}
