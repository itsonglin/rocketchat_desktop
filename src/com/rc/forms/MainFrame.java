package com.rc.forms;


import com.rc.components.GBC;
import com.rc.utils.FontUtil;
import com.rc.utils.OSUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by song on 17-5-28.
 */
public class MainFrame extends JFrame
{
    private int DEFAULT_WIDTH = 900;
    private int DEFAULT_HEIGHT = 650;

    public int currentWindowWidth = DEFAULT_WIDTH;
    public int currentWindowHeight = DEFAULT_HEIGHT;

    private LeftPanel leftPanel;
    private RightPanel rightPanel;
    private static Point origin = new Point();

    private static MainFrame context;

    public MainFrame()
    {
        context = this;
        initComponents();
        initView();


        test();

    }

    public static MainFrame getContext()
    {
        return context;
    }


    private void initComponents()
    {
        UIManager.put("Label.font", FontUtil.getDefaultFont());
        UIManager.put("Panel.font", FontUtil.getDefaultFont());
        UIManager.put("TextArea.font", FontUtil.getDefaultFont());

        leftPanel = new LeftPanel();
        rightPanel = new RightPanel();

    }

    private void test()
    {


    }

    private void initView()
    {
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setMinimumSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));

        if (OSUtil.getOsType() != OSUtil.Mac_OS)
        {
            // 隐藏标题栏
            setUndecorated(true);
        }

        addListener();

        setLayout(new GridBagLayout());
        add(leftPanel, new GBC(0, 0).setAnchor(GBC.CENTER).setWeight(2, 1).setFill(GBC.BOTH));
        add(rightPanel, new GBC(1, 0).setAnchor(GBC.CENTER).setWeight(8, 1).setFill(GBC.BOTH));

        /*leftPanel.setPreferredSize(new Dimension(250, DEFAULT_HEIGHT));
        rightPanel.setPreferredSize(new Dimension(650, DEFAULT_HEIGHT));
        setLayout(new FlowLayout(FlowLayout.LEFT, 0,0));
        add(leftPanel);
        add(rightPanel);*/
    }

    private void addListener()
    {
        // MAC OS 下拖动JFrame会出现抖动！
        if (OSUtil.getOsType() != OSUtil.Mac_OS)
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


        addComponentListener(new ComponentAdapter()
        {
            @Override
            public void componentResized(ComponentEvent e)
            {
                currentWindowWidth = (int) e.getComponent().getBounds().getWidth();
                currentWindowHeight = (int) e.getComponent().getBounds().getHeight();
            }
        });
    }


}

