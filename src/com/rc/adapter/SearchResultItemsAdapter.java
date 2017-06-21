package com.rc.adapter;

import com.rc.app.Launcher;
import com.rc.components.Colors;
import com.rc.db.model.Room;
import com.rc.db.service.RoomService;
import com.rc.entity.SearchResultItem;
import com.rc.forms.ChatPanel;
import com.rc.forms.ListPanel;
import com.rc.forms.SearchPanel;
import com.rc.listener.AbstractMouseListener;
import com.rc.utils.AvatarUtil;
import com.rc.utils.IconUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * 搜索结果适配器
 * Created by song on 17-5-30.
 */
public class SearchResultItemsAdapter extends BaseAdapter<SearchResultItemViewHolder>
{
    private List<SearchResultItem> searchResultItems;
    private List<SearchResultItemViewHolder> viewHolders = new ArrayList<>();
    private String keyWord;
    private RoomService roomService = Launcher.roomService;
    private SearchMessageOrFileListener searchMessageOrFileListener;

    public SearchResultItemsAdapter(List<SearchResultItem> searchResultItems)
    {
        this.searchResultItems = searchResultItems;
    }

    @Override
    public int getCount()
    {
        return searchResultItems.size();
    }

    @Override
    public SearchResultItemViewHolder onCreateViewHolder(int viewType)
    {
        return new SearchResultItemViewHolder();
    }

    @Override
    public void onBindViewHolder(SearchResultItemViewHolder viewHolder, int position)
    {
        if (!viewHolders.contains(viewHolder))
        {
            viewHolders.add(viewHolder);
        }
        //viewHolder.setCursor(new Cursor(Cursor.HAND_CURSOR));

        SearchResultItem item = searchResultItems.get(position);
        viewHolder.name.setKeyWord(this.keyWord);
        viewHolder.name.setText(item.getName());

        ImageIcon icon = new ImageIcon();
        // 群组头像
        String type = item.getType();

        if (type.equals("c") || type.equals("p") || type.equals("d"))
        {
            icon.setImage(getRoomAvatar(type, item.getName()));
        }
        else
        {
            if (type.equals("message"))
            {
                icon.setImage(IconUtil.getIcon(this, "/image/message.png").getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH));
            }
            else if (type.equals("file"))
            {
                icon.setImage(IconUtil.getIcon(this, "/image/file_icon.png").getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH));
            }
            // 此时的type记录着房间Id
            else
            {
                Room room = roomService.findById(type);
                if (room != null)
                {
                    icon.setImage(getRoomAvatar(room.getType(), room.getName()));
                }
            }
        }
        viewHolder.avatar.setIcon(icon);


        viewHolder.addMouseListener(new AbstractMouseListener()
        {
            @Override
            public void mouseReleased(MouseEvent e)
            {
                if (e.getButton() == MouseEvent.BUTTON1)
                {

                    if (item.getType().equals("d"))
                    {
                        String roomId = roomService.findRelativeRoomIdByUserId(item.getId()).getRoomId();
                        enterRoom(roomId);
                        ListPanel.getContext().showPanel(ListPanel.CHAT);
                        SearchPanel.getContext().clearSearchText();
                    }
                    else if (item.getType().equals("c") || item.getType().equals("p"))
                    {
                        enterRoom(item.getId());
                        ListPanel.getContext().showPanel(ListPanel.CHAT);
                        SearchPanel.getContext().clearSearchText();
                    }
                    else if (item.getType().equals("message"))
                    {
                        if (searchMessageOrFileListener != null)
                        {
                            searchMessageOrFileListener.onSearchMessage();
                        }
                    }
                    else if (item.getType().equals("file"))
                    {
                        if (searchMessageOrFileListener != null)
                        {
                            searchMessageOrFileListener.onSearchFile();
                        }
                    }
                }
            }


            @Override
            public void mouseEntered(MouseEvent e)
            {
                setBackground(viewHolder, Colors.ITEM_SELECTED_DARK);
            }

            @Override
            public void mouseExited(MouseEvent e)
            {
                setBackground(viewHolder, Colors.DARK);
            }
        });
    }

    private Image getRoomAvatar(String type, String name)
    {
        if (type.equals("c"))
        {
            return AvatarUtil.createOrLoadGroupAvatar("##", name).getScaledInstance(35, 35, Image.SCALE_SMOOTH);
        }
        else if (type.equals("p"))
        {
            return AvatarUtil.createOrLoadGroupAvatar("#", name).getScaledInstance(35, 35, Image.SCALE_SMOOTH);
        }
        // 私聊头像
        else if (type.equals("d"))
        {
            return AvatarUtil.createOrLoadUserAvatar(name).getScaledInstance(35, 35, Image.SCALE_SMOOTH);
        }

        return null;
    }

    private void setBackground(SearchResultItemViewHolder holder, Color color)
    {
        holder.setBackground(color);
        holder.name.setBackground(color);
    }

    public void setKeyWord(String keyWord)
    {
        this.keyWord = keyWord;
    }

    private void enterRoom(String roomId)
    {
        ChatPanel.getContext().enterRoom(roomId);
    }

    public void setSearchMessageOrFileListener(SearchMessageOrFileListener searchMessageOrFileListener)
    {
        this.searchMessageOrFileListener = searchMessageOrFileListener;
    }


    public interface SearchMessageOrFileListener
    {
        void onSearchMessage();

        void onSearchFile();
    }
}
