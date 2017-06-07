package com.rc.components;

import com.rc.adapter.BaseAdapter;
import com.rc.adapter.HeaderViewHolder;
import com.rc.adapter.ViewHolder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * Created by song on 17-5-30.
 */
public class RCListView extends JScrollPane
{
    private BaseAdapter adapter;
    private JPanel contentPanel;
    private int vGap;
    private int hGap;
    private java.util.List<Rectangle> rectangleList = new ArrayList<>();
    boolean scrollToBottom = true;
    private AdjustmentListener adjustmentListener;

    public RCListView()
    {
        this(0, 0);
    }

    public RCListView(int hGap, int vGap)
    {
        this.vGap = vGap;
        this.hGap = hGap;

        initComponents();
        //fillComponents();
    }

    /**
     * 设置滚动条的颜色，此方法必须在setAdapter()方法之前执行
     *
     * @param thumbColor
     * @param trackColor
     */
    public void setScrollBarColor(Color thumbColor, Color trackColor)
    {
        this.getVerticalScrollBar().setUI(new ScrollUI(thumbColor, trackColor));
    }

    private void initComponents()
    {
        contentPanel = new JPanel();
        contentPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP, hGap, vGap, true, false));
        //contentPanel.setLayout(new GridLayout(4, 1));

        this.setViewportView(contentPanel);
        this.setBorder(null);
        this.getVerticalScrollBar().setUnitIncrement(17);
        this.getVerticalScrollBar().setUI(new ScrollUI());

    }

    public void setAutoScrollToBottom()
    {
        adjustmentListener = new AdjustmentListener()
        {
            public void adjustmentValueChanged(AdjustmentEvent evt)
            {
                if (evt.getAdjustmentType() == AdjustmentEvent.TRACK && scrollToBottom)
                {
                    getVerticalScrollBar().setValue(getVerticalScrollBar().getModel().getMaximum()
                            - getVerticalScrollBar().getModel().getExtent());
                }
                else
                {
                    getVerticalScrollBar().removeAdjustmentListener(adjustmentListener);
                }
            }
        };

        getVerticalScrollBar().addAdjustmentListener(adjustmentListener);


        addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseEntered(MouseEvent e)
            {
                scrollToBottom = false;
                super.mouseEntered(e);
            }
        });
    }

    public void fillComponents()
    {
        if (adapter == null)
        {
            return;
        }

        for (int i = 0; i < adapter.getCount(); i++)
        {
            int viewType = adapter.getItemViewType(i);
            HeaderViewHolder headerViewHolder = adapter.onCreateHeaderViewHolder(viewType, i);
            if (headerViewHolder != null)
            {
                adapter.onBindHeaderViewHolder(headerViewHolder, i);
                contentPanel.add(headerViewHolder);
                rectangleList.add(headerViewHolder.getBounds());
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

        fillComponents();
        scrollToPosition(0);
    }

    public void setContentPanelBackground(Color color)
    {
        contentPanel.setOpaque(true);
        contentPanel.setBackground(color);
    }

    public void scrollToPosition(int position)
    {
    }

    /**
     * 获取滚动条在底部时显示的条目数
     */
    private int getLastVisibleItemCount()
    {
        int height = getHeight();

        int elemHeight = 0;
        int count = 0;
        for (int i = contentPanel.getComponentCount() - 1; i >= 0; i--)
        {
            count++;
            int h = contentPanel.getComponent(i).getHeight() + 20;
            elemHeight += h;

            if (elemHeight >= height)
            {
                break;
            }
        }

        return count;
    }

    public void notifyDataSetChange()
    {
        contentPanel.removeAll();
        fillComponents();
    }
}
