package com.rc.adapter;

import com.rc.entity.RoomItem;

import java.util.List;

/**
 * Created by song on 17-5-30.
 */
public class RoomItemsAdapter extends BaseAdapter
{
    private List<RoomItem> roomItems;

    public RoomItemsAdapter(List<RoomItem> roomItems)
    {
        this.roomItems = roomItems;
    }

    @Override
    public int getCount()
    {
        return roomItems.size();
    }
}
