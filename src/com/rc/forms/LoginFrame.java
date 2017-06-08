package com.rc.forms;

import com.rc.components.*;
import com.rc.listener.AbstractMouseListener;
import com.rc.utils.FontUtil;
import com.rc.utils.IconUtil;
import com.rc.utils.OSUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

/**
 * Created by song on 08/06/2017.
 */
public class LoginFrame extends JFrame
{
    private static final int windowWidth = 300;
    private static final int windowHeight = 400;

    private JPanel titlePanel;
    private JLabel closeLabel;
    private JPanel editPanel;
    private RCTextField username;
    private RCPasswordField password;
    private RCButton loginButton;



    private static Point origin = new Point();


    public LoginFrame()
    {
        initComponents();
        initView();
        centerScreen();
        setListeners();
    }


    private void initComponents()
    {
        setBounds(0, 0, windowWidth, windowHeight);

        titlePanel = new JPanel();
        titlePanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        titlePanel.setBounds(0,5, windowWidth, 30);

        closeLabel = new JLabel();
        closeLabel.setIcon(IconUtil.getIcon(this, "/image/close.png"));
        closeLabel.setHorizontalAlignment(JLabel.CENTER);
        //closeLabel.setPreferredSize(new Dimension(30,30));
        closeLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));


        editPanel = new JPanel();
        editPanel.setBounds(10,80, windowWidth - 20, 150);
        editPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP, 0, 5, true, true));

        Dimension textFieldDimension = new Dimension(200, 40);
        username = new RCTextField();
        username.setPlaceholder("用户名");
        username.setPreferredSize(textFieldDimension);
        username.setFont(FontUtil.getDefaultFont(14));
        username.setForeground(Colors.FONT_BLACK);
        username.setMargin(new Insets(0,15,0,0));

        password = new RCPasswordField();
        password.setPreferredSize(textFieldDimension);
        password.setPlaceholder("密码");
        //password.setBorder(new RCBorder(RCBorder.BOTTOM, Colors.LIGHT_GRAY));
        password.setFont(FontUtil.getDefaultFont(14));
        password.setForeground(Colors.FONT_BLACK);
        password.setMargin(new Insets(0,15,0,0));


        loginButton = new RCButton("登 录", Colors.MAIN_COLOR, Colors.MAIN_COLOR_DARKER, Colors.MAIN_COLOR_DARKER);
        loginButton.setFont(FontUtil.getDefaultFont(14));
        loginButton.setPreferredSize(textFieldDimension);
    }

    private void initView()
    {
        setLayout(null);

        titlePanel.add(closeLabel);

        if (OSUtil.getOsType() != OSUtil.Mac_OS)
        {
            setUndecorated(true);
            add(titlePanel);
        }

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        buttonPanel.add(loginButton, new GBC(0,0).setFill(GBC.HORIZONTAL).setWeight(1,1).setInsets(10, 0,0,0));

        editPanel.add(username);
        editPanel.add(password);
        editPanel.add(buttonPanel);

        add(editPanel);
    }

    /**
     * 使窗口在屏幕中央显示
     */
    private void centerScreen()
    {
        Toolkit tk = Toolkit.getDefaultToolkit();
        this.setLocation((tk.getScreenSize().width - windowWidth) / 2,
                (tk.getScreenSize().height - windowHeight) / 2);
    }

    private void setListeners()
    {
        closeLabel.addMouseListener(new AbstractMouseListener()
        {
            @Override
            public void mouseEntered(MouseEvent e)
            {
                super.mouseEntered(e);
            }

            @Override
            public void mouseClicked(MouseEvent e)
            {
                System.exit(1);
                super.mouseClicked(e);
            }
        });

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
                    Point p = LoginFrame.this.getLocation();
                    // 设置窗口的位置
                    LoginFrame.this.setLocation(p.x + e.getX() - origin.x, p.y + e.getY()
                            - origin.y);
                }
            });
        }
    }

}
