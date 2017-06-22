package com.rc.adapter.search;

import com.rc.adapter.BaseAdapter;
import com.rc.app.Launcher;
import com.rc.components.Colors;
import com.rc.db.model.Message;
import com.rc.db.model.Room;
import com.rc.db.service.MessageService;
import com.rc.db.service.RoomService;
import com.rc.entity.SearchResultItem;
import com.rc.forms.ChatPanel;
import com.rc.forms.ListPanel;
import com.rc.forms.SearchPanel;
import com.rc.listener.AbstractMouseListener;
import com.rc.utils.AvatarUtil;
import com.rc.utils.IconUtil;
import com.rc.utils.TimeUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 搜索结果适配器
 * Created by song on 17-5-30.
 */
public class SearchResultItemsAdapter extends BaseAdapter<SearchResultItemViewHolder>
{
    private List<SearchResultItem> searchResultItems;
    private String keyWord;
    private RoomService roomService = Launcher.roomService;
    private SearchMessageOrFileListener searchMessageOrFileListener;

    public static final int VIEW_TYPE_CONTACTS_ROOM = 0;
    public static final int VIEW_TYPE_MESSAGE = 1;
    public static final int VIEW_TYPE_FILE = 2;
    private MessageService messageService = Launcher.messageService;


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
    public int getItemViewType(int position)
    {
        // return super.getItemViewType(position);
        String type = searchResultItems.get(position).getType();
        if (type.equals("d") || type.equals("c") || type.equals("p") || type.equals("searchMessage") || type.equals("searchFile"))
        {
            return VIEW_TYPE_CONTACTS_ROOM;
        }
        else if (type.equals("message"))
        {
            return VIEW_TYPE_MESSAGE;
        }
        else if (type.equals("file"))
        {
            return VIEW_TYPE_FILE;
        }
        else
        {
            throw new RuntimeException("ViewType 不正确");
        }
    }


    @Override
    public SearchResultItemViewHolder onCreateViewHolder(int viewType)
    {
        switch (viewType)
        {
            case VIEW_TYPE_CONTACTS_ROOM:
            {
                return new SearchResultUserItemViewHolder();
            }
            case VIEW_TYPE_MESSAGE:
            {
                return new SearchResultMessageItemViewHolder();
            }
            default:
            {
                return null;
            }
        }
    }

    @Override
    public void onBindViewHolder(SearchResultItemViewHolder viewHolder, int position)
    {
        SearchResultItem item = searchResultItems.get(position);

        if (viewHolder instanceof SearchResultUserItemViewHolder)
        {
            processContactsOrRoomsResult(viewHolder, item);
        }
        else if (viewHolder instanceof SearchResultMessageItemViewHolder)
        {
            processMessageResult(viewHolder, item);
        }

//        if (!viewHolders.contains(viewHolder))
//        {
//            viewHolders.add(viewHolder);
//        }

        //viewHolder.setCursor(new Cursor(Cursor.HAND_CURSOR));

        //SearchResultItem item = searchResultItems.get(position);

    }

    /**
     * 处理消息搜索结果
     *
     * @param viewHolder
     * @param item
     */
    private void processMessageResult(SearchResultItemViewHolder viewHolder, SearchResultItem item)
    {
        SearchResultMessageItemViewHolder holder = (SearchResultMessageItemViewHolder) viewHolder;
        Map map = (Map) item.getTag();
        Room room = roomService.findById((String) map.get("roomId"));
        Message message = messageService.findById((String) map.get("messageId"));

        holder.avatar.setIcon(new ImageIcon(getRoomAvatar(room.getType(), room.getName())));
        holder.brief.setKeyWord(keyWord);
        holder.brief.setText(item.getName());
        holder.roomName.setText(room.getName());
        holder.time.setText(TimeUtil.diff(message.getTimestamp()));

        holder.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseReleased(MouseEvent e)
            {
                if (e.getButton() == MouseEvent.BUTTON1)
                {
                    enterRoom(room.getRoomId(), message.getTimestamp());
                    clearSearchText();
                }
                super.mouseReleased(e);
            }
        });
    }

    private void processMouseListeners(SearchResultItemViewHolder viewHolder, SearchResultItem item)
    {
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
                        enterRoom(roomId, 0L);
                        clearSearchText();
                    }
                    else if (item.getType().equals("c") || item.getType().equals("p"))
                    {
                        enterRoom(item.getId(), 0L);
                        clearSearchText();
                    }
                    else if (item.getType().equals("searchMessage"))
                    {
                        if (searchMessageOrFileListener != null)
                        {
                            searchMessageOrFileListener.onSearchMessage();
                        }
                    }
                    else if (item.getType().equals("searchFile"))
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

    private void clearSearchText()
    {
        ListPanel.getContext().showPanel(ListPanel.CHAT);
        SearchPanel.getContext().clearSearchText();
    }

    /**
     * 处理通讯录或群组探索结果
     *
     * @param viewHolder
     * @param item
     */
    private void processContactsOrRoomsResult(SearchResultItemViewHolder viewHolder, SearchResultItem item)
    {
        SearchResultUserItemViewHolder holder = (SearchResultUserItemViewHolder) viewHolder;

        holder.name.setKeyWord(this.keyWord);
        holder.name.setText(item.getName());

        ImageIcon icon = new ImageIcon();
        // 群组头像
        String type = item.getType();

        if (type.equals("c") || type.equals("p") || type.equals("d"))
        {
            icon.setImage(getRoomAvatar(type, item.getName()));
        }
        else
        {
            if (type.equals("searchMessage"))
            {
                icon.setImage(IconUtil.getIcon(this, "/image/message.png").getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH));
            }
            else if (type.equals("searchFile"))
            {
                icon.setImage(IconUtil.getIcon(this, "/image/file_icon.png").getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH));
            }
            else if (type.equals("message"))
            {
                Room room = roomService.findById((String) ((Map) item.getTag()).get("roomId"));
                if (room != null)
                {
                    icon.setImage(getRoomAvatar(room.getType(), room.getName()));
                }
            }
        }
        holder.avatar.setIcon(icon);

        processMouseListeners(viewHolder, item);
    }


    /**
     * 根据房间类型获取对应的头像
     *
     * @param type
     * @param name
     * @return
     */
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
        if (holder instanceof SearchResultUserItemViewHolder)
        {
            ((SearchResultUserItemViewHolder) holder).name.setBackground(color);
        }
        else if (holder instanceof SearchResultMessageItemViewHolder)
        {
            ((SearchResultMessageItemViewHolder) holder).nameBrief.setBackground(color);
        }
    }

    public void setKeyWord(String keyWord)
    {
        this.keyWord = keyWord;
    }

    private void enterRoom(String roomId, long firstMessageTimestamp)
    {
        ChatPanel.getContext().enterRoom(roomId, firstMessageTimestamp);
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
