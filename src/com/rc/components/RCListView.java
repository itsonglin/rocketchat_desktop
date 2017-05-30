package com.rc.components;

import com.rc.adapter.BaseAdapter;
import com.rc.adapter.HeaderViewHolder;
import com.rc.adapter.ViewHolder;

import javax.swing.*;
import java.awt.*;

/**
 * Created by song on 17-5-30.
 */
public class RCListView extends JScrollPane
{
    private BaseAdapter adapter;
    private JPanel contentPanel;

    public RCListView()
    {
        initComponents();
        //initView();
    }

    private void initComponents()
    {
        contentPanel = new JPanel();
        contentPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP, true, false));

        this.setViewportView(contentPanel);
        this.setBorder(null);
        this.getVerticalScrollBar().setUnitIncrement(17);
        this.getVerticalScrollBar().setUI(new ScrollUI());

    }

    public void initView()
    {
        for (int i = 0; i < adapter.getCount(); i++)
        {
            int viewType = adapter.getItemViewType(i);
            HeaderViewHolder headerViewHolder = adapter.onCreateHeaderViewHolder(viewType, i);
            if (headerViewHolder != null)
            {
                adapter.onBindHeaderViewHolder(headerViewHolder, i);
                contentPanel.add(headerViewHolder);
            }

            ViewHolder holder = adapter.onCreateViewHolder(viewType);
            adapter.onBindViewHolder(holder, i);
            contentPanel.add(holder);
        }
    }


    public BaseAdapter getAdapter()
    {
        return adapter;
    }

    public void setAdapter(BaseAdapter adapter)
    {
        this.adapter = adapter;
        initView();
    }

    public void setContentPanelBackground(Color color)
    {
        contentPanel.setOpaque(true);
        contentPanel.setBackground(color);
    }
}
