package com.rc.components;

import com.rc.adapter.BaseAdapter;

/**
 * Created by song on 17-5-30.
 */
public class RCListView
{
    private BaseAdapter adapter;

    public RCListView()
    {
        initView();
    }

    private void initView()
    {
    }


    public BaseAdapter getAdapter()
    {
        return adapter;
    }

    public void setAdapter(BaseAdapter adapter)
    {
        this.adapter = adapter;
    }
}
