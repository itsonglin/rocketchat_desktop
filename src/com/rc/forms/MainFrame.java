package com.rc.forms;


import com.rc.utils.FontUtil;
import com.rc.utils.IconUtil;
import com.rc.utils.OSUtil;
import com.rc.websocket.WebSocketClient;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import javax.swing.*;
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

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
    private Image normalTrayIcon; // 正常时的任务栏图标
    private Image emptyTrayIcon; // 闪动时的任务栏图标
    private TrayIcon trayIcon;
    private boolean trayFlashing = false;
    private AudioStream messageSound; //消息到来时候的提示间


    public MainFrame()
    {
        context = this;
        initComponents();
        initView();
        initResource();

        // 连接WebSocket
        startWebSocket();
        test();
    }

    private void initResource()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                initTray();
                //initSound();
            }
        }).start();

    }

    /**
     * 初始化提示声音
     */
    private void initSound()
    {
        try
        {
            InputStream inputStream = getClass().getResourceAsStream("/wav/msg.wav");
            messageSound = new AudioStream(inputStream);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 播放消息提示间
     */
    public void playMessageSound()
    {
        try
        {
            InputStream inputStream = getClass().getResourceAsStream("/wav/msg.wav");
            messageSound = new AudioStream(inputStream);
            AudioPlayer.player.start(messageSound);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    /**
     * 初始化系统托盘图标
     */
    private void initTray()
    {
        SystemTray systemTray = SystemTray.getSystemTray();//获取系统托盘
        try
        {
            if (OSUtil.getOsType() == OSUtil.Mac_OS)
            {
                normalTrayIcon = IconUtil.getIcon(this, "/image/ic_launcher_dark.png", 20, 20).getImage();
            }
            else
            {
                normalTrayIcon = IconUtil.getIcon(this, "/image/ic_launcher.png", 20, 20).getImage();
            }

            emptyTrayIcon = IconUtil.getIcon(this, "/image/ic_launcher_empty.png", 20, 20).getImage();

            trayIcon = new TrayIcon(normalTrayIcon, "和理通");
            trayIcon.setImageAutoSize(true);
            trayIcon.addMouseListener(new MouseAdapter()
            {
                @Override
                public void mouseClicked(MouseEvent e)
                {
                    // 显示主窗口
                    setVisible(true);

                    // 任务栏图标停止闪动
                    if (trayFlashing)
                    {
                        trayFlashing = false;
                        trayIcon.setImage(normalTrayIcon);
                    }

                    super.mouseClicked(e);
                }
            });


            systemTray.add(trayIcon);
        } catch (AWTException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 设置任务栏图标闪动
     */
    public void setTrayFlashing()
    {
        trayFlashing = true;
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (trayFlashing)
                {
                    try
                    {
                        trayIcon.setImage(emptyTrayIcon);
                        Thread.sleep(800);

                        trayIcon.setImage(normalTrayIcon);
                        Thread.sleep(800);

                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }

    public boolean isTrayFlashing()
    {
        return trayFlashing;
    }


    public static MainFrame getContext()
    {
        return context;
    }


    private void initComponents()
    {
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        UIManager.put("Label.font", FontUtil.getDefaultFont());
        UIManager.put("Panel.font", FontUtil.getDefaultFont());
        UIManager.put("TextArea.font", FontUtil.getDefaultFont());

        leftPanel = new LeftPanel();
        leftPanel.setPreferredSize(new Dimension(260, currentWindowHeight));

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
        this.setLocation((tk.getScreenSize().width - currentWindowWidth) / 2,
                (tk.getScreenSize().height - currentWindowHeight) / 2);
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

    private void startWebSocket()
    {
        WebSocketClient webSocketClient = new WebSocketClient();
        webSocketClient.startClient();
    }
}

