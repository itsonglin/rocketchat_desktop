package com.rc.components;

import com.rc.adapter.BaseAdapter;
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
        contentPanel.setLayout(new GridBagLayout());
        this.setViewportView(contentPanel);
        this.setBorder(null);
        this.getVerticalScrollBar().setUnitIncrement(17);
        //this.setBackground(Colors.DARK);

        this.getVerticalScrollBar().setUI(new ScrollUI());

    }

    public void initView()
    {
        for (int i = 0; i < adapter.getCount(); i++)
        {
            int viewType = adapter.getItemViewType(i);
            ViewHolder holder = adapter.onCreateViewHolder(viewType);
            adapter.onBindViewHolder(holder, i);

            contentPanel.add(holder, new GBC(0, i).setFill(GBC.BOTH).setWeight(1, 1));
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
}
