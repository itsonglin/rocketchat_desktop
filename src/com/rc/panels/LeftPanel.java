package com.rc.panels;

import com.rc.components.Colors;
import com.rc.components.GBC;

import javax.swing.*;
import java.awt.*;

/**
 * Created by song on 17-5-29.<br/>
 *
 * <P>下图 #LeftPanel# 对应的位置</P>
 *
 * 包含 用户信息面板、搜索面板、功能TAB以及房间列表等
 *
 * <P>推荐使用Menlo或Consolas字体</P>
 * ┌────────────────────────┬────────────────────────────────────────────────────────┐
 * │                        │  Room Title                                         ≡  │
 * │                        ├────────────────────────────────────────────────────────┤
 * │                        │                                                        │
 * │                        │                     message time                       │
 * │                        │  ┌──┐ ┌────────────┐                                   │
 * │                        │  └──┘ │  message   │                                   │
 * │                        │       └────────────┘                                   │
 * │                        │                                                        │
 * │                        │                                                        │
 * │     #LeftPanel#        │                     message time                       │
 * │                        │                                    ┌────────────┐ ┌──┐ │
 * │                        │                                    │  message   │ └──┘ │
 * │                        │                                    └────────────┘      │
 * │                        │                                                        │
 * │                        │                                                        │
 * │                        ├────────────────────────────────────────────────────────┤
 * │                        │  ▆   ▆   ▆                                             │
 * │                        │                                                        │
 * │                        │                                                        │
 * │                        │                                                ┌─────┐ │
 * │                        │                                                └─────┘ │
 * └────────────────────────┴────────────────────────────────────────────────────────┘
 */
public class LeftPanel extends JPanel
{
    private MyInfoPanel myInfoPanel;
    private SearchPanel searchPanel;
    private TabOperationPanel mainOperationPanel;
    private ListPanel listPanel;

    public LeftPanel()
    {

        initComponents();
        initView();
    }

    private void initComponents()
    {
        myInfoPanel = new MyInfoPanel(this);

        searchPanel = new SearchPanel(this);

        mainOperationPanel = new TabOperationPanel(this);
        //mainOperationPanel.setBackground(Color.blue);

        listPanel = new ListPanel(this);
        listPanel.setBackground(Colors.DARK);
    }

    private void initView()
    {
        this.setBackground(Colors.DARK);
        this.setLayout(new GridBagLayout());

        add(myInfoPanel, new GBC(0, 0).setAnchor(GBC.CENTER).setFill(GBC.BOTH).setWeight(1, 7));
        add(searchPanel, new GBC(0, 1).setAnchor(GBC.CENTER).setFill(GBC.HORIZONTAL).setWeight(1, 1));
        add(mainOperationPanel, new GBC(0, 2).setAnchor(GBC.CENTER).setFill(GBC.BOTH).setWeight(1, 1));
        add(listPanel, new GBC(0, 3).setAnchor(GBC.CENTER).setFill(GBC.BOTH).setWeight(1, 60));

    }

    public ListPanel getListPanel()
    {
        return this.listPanel;
    }


}
