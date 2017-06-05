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
    boolean scrollToButtom = true;

    public RCListView()
    {
        this(0, 0);
    }

    public RCListView(int hGap, int vGap)
    {
        this.vGap = vGap;
        this.hGap = hGap;

        initComponents();
        //initView();
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


        getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener()
        {
            public void adjustmentValueChanged(AdjustmentEvent evt)
            {
                //int lastVisibleCount = getLastVisibleItemCount();

                //System.out.println(contentPanel.getComponent(0));
                //int max = contentPanel.getComponentCount() * 2 + ((contentPanel.getComponentCount() - (7 + (getLastVisibleItemCount() - 4) * 2)) * 2);

                System.out.println(getLastVisibleItemCount());
                //System.out.println(contentPanel.getComponentCount() + ", " + max  + ", " + isNeedBottom);
                /*if (evt.getAdjustmentType() == AdjustmentEvent.TRACK && (isNeedBottom < max))
                {
                    getVerticalScrollBar().setValue(getVerticalScrollBar().getModel().getMaximum()
                            - getVerticalScrollBar().getModel().getExtent());
                    isNeedBottom++;
                    //System.out.println(isNeedBottom + ", val = " + (getVerticalScrollBar().getModel().getMaximum() - getVerticalScrollBar().getModel().getExtent()) + ", max = " + getVerticalScrollBar().getModel().getMaximum());
                    //System.out.println("max = " + max + ", isNeeded = " + isNeedBottom);
                }
                else
                {

                }*/

                if (evt.getAdjustmentType() == AdjustmentEvent.TRACK && scrollToButtom)
                {
                    getVerticalScrollBar().setValue(getVerticalScrollBar().getModel().getMaximum()
                            - getVerticalScrollBar().getModel().getExtent());
                }
                else
                {

                }
            }
        });

        addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseEntered(MouseEvent e)
            {
                scrollToButtom = false;
                super.mouseEntered(e);
            }
        });

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
                rectangleList.add(headerViewHolder.getBounds());
            }

            ViewHolder holder = adapter.onCreateViewHolder(viewType);
            adapter.onBindViewHolder(holder, i);
            // contentPanel.add(holder);
            contentPanel.add(holder);
        }


       /* for (int i = 0; i < 5; i++)
        {
            JPanel panel = new MessageRightTextViewHolder();
            panel.setPreferredSize(new Dimension(500, 100));
            panel.setBackground(new Color(30 * i, 50 * i, 15 * i));
            panel.setBorder(new LineBorder(Colors.RED));
            contentPanel.add(panel);

        }*/

    }


    public BaseAdapter getAdapter()
    {
        return adapter;
    }

    public void setAdapter(BaseAdapter adapter)
    {
        this.adapter = adapter;

        initView();
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
}
