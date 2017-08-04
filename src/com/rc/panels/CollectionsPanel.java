package com.rc.panels;

import com.rc.components.Colors;

import javax.swing.*;
import java.awt.*;

/**
 * Created by song on 17-5-30.
 *
 * <P>下图 #CollectionsPanel# 对应的位置</P>
 *
 * 显示收藏列表
 *
 * <P>推荐使用Menlo或Consolas字体</P>
 * ┌────────────────────────┬────────────────────────────────────────────────────────┐
 * │ ┌─────┐                │  Room Title                                         ≡  │
 * │ │     │ name         ≡ ├────────────────────────────────────────────────────────┤
 * │ └─────┘                │                                                        │
 * ├────────────────────────┤                     message time                       │
 * │    search              │  ┌──┐ ┌────────────┐                                   │
 * ├────────────────────────┤  └──┘ │  message   │                                   │
 * │  ▆    │    ▆   │   ▆   │       └────────────┘                                   │
 * ├────────────────────────┤                                                        │
 * │                        │                                                        │
 * │                        │                     message time                       │
 * │                        │                                    ┌────────────┐ ┌──┐ │
 * │                        │                                    │  message   │ └──┘ │
 * │                        │                                    └────────────┘      │
 * │   #CollectionsPanel#   │                                                        │
 * │                        │                                                        │
 * │                        ├────────────────────────────────────────────────────────┤
 * │                        │  ▆   ▆   ▆                                             │
 * │                        │                                                        │
 * │                        │                                                        │
 * │                        │                                                ┌─────┐ │
 * │                        │                                                └─────┘ │
 * └────────────────────────┴────────────────────────────────────────────────────────┘
 */
public class CollectionsPanel extends ParentAvailablePanel
{
    private JLabel tipLabel;

    public CollectionsPanel(JPanel parent)
    {
        super(parent);

        initComponents();
        initView();
    }

    private void initComponents()
    {
        tipLabel = new JLabel("暂无收藏");
        tipLabel.setForeground(Colors.FONT_GRAY);
    }

    private void initView()
    {
        this.setBackground(Colors.DARK);
        setLayout(new FlowLayout());
        add(tipLabel);
    }
}
