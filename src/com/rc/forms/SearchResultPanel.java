package com.rc.forms;

import com.rc.adapter.RoomItemViewHolder;
import com.rc.adapter.search.SearchResultItemsAdapter;
import com.rc.app.Launcher;
import com.rc.components.Colors;
import com.rc.components.GBC;
import com.rc.components.RCListView;
import com.rc.db.service.RoomService;
import com.rc.entity.SearchResultItem;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 左侧搜索结果列表
 * Created by song on 17-6-21.
 */
public class SearchResultPanel extends ParentAvailablePanel
{
    private static SearchResultPanel context;
    private final SearchResultItemsAdapter searchResultItemsAdapter;

    private RCListView resultItemsListView;
    private List<SearchResultItem> searchResultItems = new ArrayList<>();
    private RoomService roomService = Launcher.roomService;


    public SearchResultPanel(JPanel parent)
    {
        super(parent);
        context = this;

        initComponents();
        initView();
        //initData();
        searchResultItemsAdapter = new SearchResultItemsAdapter(searchResultItems);
        resultItemsListView.setAdapter(searchResultItemsAdapter);
    }

    private void initComponents()
    {
        resultItemsListView = new RCListView();
    }

    private void initView()
    {
        setLayout(new GridBagLayout());
        resultItemsListView.setContentPanelBackground(Colors.DARK);
        add(resultItemsListView, new GBC(0, 0).setFill(GBC.BOTH).setWeight(1, 1));
    }

    public void setData(List<SearchResultItem> data)
    {
        searchResultItems.clear();
        searchResultItems.addAll(data);
        /*List<Room> rooms = roomService.findAll();

        for (Room room : rooms)
        {
            SearchResultItem item = new SearchResultItem(room.getRoomId(), room.getName(), room.getType());
            searchResultItems.add(item);
        }*/
    }

    /**
     * 重绘整个列表
     */
    public void notifyDataSetChanged(boolean keepSize)
    {
        //initData();
        resultItemsListView.notifyDataSetChanged(keepSize);
    }


    /**
     * 设置每个房间项目的背影色
     * @param holder
     * @param color
     */
    private void setItemBackground(RoomItemViewHolder holder, Color color)
    {
        holder.setBackground(color);
        holder.nameBrief.setBackground(color);
        holder.timeUnread.setBackground(color);
    }



    public static SearchResultPanel getContext()
    {
        return context;
    }

    public void setKeyWord(String keyWord)
    {
        this.searchResultItemsAdapter.setKeyWord(keyWord);
    }

    public void setSearchMessageOrFileListener(SearchResultItemsAdapter.SearchMessageOrFileListener searchMessageOrFileListener)
    {
        if (this.searchResultItemsAdapter == null)
        {
            throw new RuntimeException("请先设置adapter!");
        }

        this.searchResultItemsAdapter.setSearchMessageOrFileListener(searchMessageOrFileListener);
    }
}
