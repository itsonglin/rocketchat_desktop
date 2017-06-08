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
    public static int DEFAULT_WIDTH = 900;
    public static int DEFAULT_HEIGHT = 650;

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
        leftPanel.setPreferredSize(new Dimension(250, currentWindowHeight));

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

        //getRootPane().setWindowDecorationStyle(JRootPane.NONE );//使frame只剩下标题栏
        addListener();

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);

        centerScreen();
    }


    /**
     * 使窗口在屏幕中央显示
     */
    private void centerScreen()
    {
        Toolkit tk = Toolkit.getDefaultToolkit();
        this.setLocation((tk.getScreenSize().width - currentWindowWidth)/2,
                (tk.getScreenSize().height - currentWindowHeight)/2);
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

