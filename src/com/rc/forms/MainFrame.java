package com.rc.forms;


import com.rc.components.GBC;
import com.rc.utils.FontUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

/**
 * Created by song on 17-5-28.
 */
public class MainFrame extends JFrame
{
    private int DEFAULT_WIDTH = 900;
    private int DEFAULT_HEIGHT = 650;

    private LeftPanel leftPanel;
    private RightPanel rightPanel;
    private static Point origin = new Point();

    private static MainFrame context;

    public MainFrame()
    {
        context = this;
        initComponents();
        initView();
    }

    public static MainFrame getContext()
    {
        return context;
    }


    private void initComponents()
    {
        UIManager.put("Label.font", FontUtil.getDefaultFont());
        UIManager.put("Panel.font", FontUtil.getDefaultFont());

        leftPanel = new LeftPanel();
        rightPanel = new RightPanel();
    }

    private void initView()
    {
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setMinimumSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));

        // 隐藏标题栏
        setUndecorated(true);
        addListener();

        setLayout(new GridBagLayout());
        add(leftPanel, new GBC(0, 0).setAnchor(GBC.CENTER).setWeight(1, 1).setFill(GBC.BOTH));
        add(rightPanel, new GBC(1, 0).setAnchor(GBC.CENTER).setWeight(7, 1).setFill(GBC.BOTH));

        /*leftPanel.setPreferredSize(new Dimension(250, DEFAULT_HEIGHT));
        rightPanel.setPreferredSize(new Dimension(650, DEFAULT_HEIGHT));
        setLayout(new FlowLayout(FlowLayout.LEFT, 0,0));
        add(leftPanel);
        add(rightPanel);*/
    }

    private void addListener()
    {
        addMouseListener(new MouseAdapter()
        {
            public void mousePressed(MouseEvent e)
            {
                // 当鼠标按下的时候获得窗口当前的位置
                origin.x = e.getX();
                origin.y = e.getY();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter()
        {
            public void mouseDragged(MouseEvent e)
            {
                // 当鼠标拖动时获取窗口当前位置
                Point p = MainFrame.this.getLocation();
                // 设置窗口的位置
                MainFrame.this.setLocation(p.x + e.getX() - origin.x, p.y + e.getY()
                        - origin.y);
            }
        });
    }
}

