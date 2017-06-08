package com.rc.forms;

import com.rc.components.*;
import com.rc.listener.AbstractMouseListener;
import com.rc.utils.FontUtil;
import com.rc.utils.HttpUtil;
import com.rc.utils.IconUtil;
import com.rc.utils.OSUtil;
import org.json.JSONObject;
import tasks.HttpGetTask;
import tasks.HttpPostTask;
import tasks.HttpResponseListener;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by song on 08/06/2017.
 */
public class LoginFrame extends JFrame
{
    private static final int windowWidth = 300;
    private static final int windowHeight = 400;

    private JPanel controlPanel;
    private JLabel closeLabel;
    private JPanel editPanel;
    private RCTextField username;
    private RCPasswordField password;
    private RCButton loginButton;
    private JLabel statusLabel;
    private JLabel titleLabel;


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
        Dimension windowSize = new Dimension(windowWidth, windowHeight);
        setMinimumSize(windowSize);
        setMaximumSize(windowSize);


        controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        //controlPanel.setBounds(0,5, windowWidth, 30);

        closeLabel = new JLabel();
        closeLabel.setIcon(IconUtil.getIcon(this, "/image/close.png"));
        closeLabel.setHorizontalAlignment(JLabel.CENTER);
        //closeLabel.setPreferredSize(new Dimension(30,30));
        closeLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        titleLabel = new JLabel();
        titleLabel.setText("登  录");
        titleLabel.setFont(FontUtil.getDefaultFont(16));


        editPanel = new JPanel();
        editPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP, 0, 5, true, false));

        Dimension textFieldDimension = new Dimension(200, 35);
        username = new RCTextField();
        username.setPlaceholder("用户名");
        username.setPreferredSize(textFieldDimension);
        username.setFont(FontUtil.getDefaultFont(14));
        username.setForeground(Colors.FONT_BLACK);
        username.setMargin(new Insets(0, 15, 0, 0));

        password = new RCPasswordField();
        password.setPreferredSize(textFieldDimension);
        password.setPlaceholder("密码");
        //password.setBorder(new RCBorder(RCBorder.BOTTOM, Colors.LIGHT_GRAY));
        password.setFont(FontUtil.getDefaultFont(14));
        password.setForeground(Colors.FONT_BLACK);
        password.setMargin(new Insets(0, 15, 0, 0));


        loginButton = new RCButton("登 录", Colors.MAIN_COLOR, Colors.MAIN_COLOR_DARKER, Colors.MAIN_COLOR_DARKER);
        loginButton.setFont(FontUtil.getDefaultFont(14));
        loginButton.setPreferredSize(new Dimension(200, 40));

        statusLabel = new JLabel();
        statusLabel.setForeground(Colors.RED);
        statusLabel.setText("密码不正确");
        statusLabel.setVisible(false);
    }

    private void initView()
    {
        JPanel contentPanel = new JPanel();
        contentPanel.setBorder(new LineBorder(Colors.LIGHT_GRAY));
        contentPanel.setLayout(new GridBagLayout());

        controlPanel.add(closeLabel);

        if (OSUtil.getOsType() != OSUtil.Mac_OS)
        {
            setUndecorated(true);
            contentPanel.add(controlPanel, new GBC(0, 0).setFill(GBC.BOTH).setWeight(1, 1).setInsets(5, 0, 0, 0));
        }

        JPanel titlePanel = new JPanel();
        titlePanel.add(titleLabel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        buttonPanel.add(loginButton, new GBC(0, 0).setFill(GBC.HORIZONTAL).setWeight(1, 1).setInsets(10, 0, 0, 0));

        editPanel.add(username);
        editPanel.add(password);
        editPanel.add(statusLabel);
        editPanel.add(buttonPanel);


        add(contentPanel);
        contentPanel.add(titlePanel, new GBC(0, 1).setFill(GBC.BOTH).setWeight(1, 1).setInsets(10, 10, 0, 10));
        contentPanel.add(editPanel, new GBC(0, 2).setFill(GBC.BOTH).setWeight(1, 10).setInsets(10, 10, 0, 10));
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
            public void mouseClicked(MouseEvent e)
            {
                System.exit(1);
                super.mouseClicked(e);
            }

            @Override
            public void mouseEntered(MouseEvent e)
            {
                closeLabel.setBackground(Colors.LIGHT_GRAY);
                super.mouseEntered(e);
            }

            @Override
            public void mouseExited(MouseEvent e)
            {
                closeLabel.setBackground(Colors.WINDOW_BACKGROUND);
                super.mouseExited(e);
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

        loginButton.addMouseListener(new AbstractMouseListener()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                super.mouseClicked(e);
                doLogin();
            }
        });

        KeyListener keyListener = new KeyListener()
        {
            @Override
            public void keyTyped(KeyEvent e)
            {
            }

            @Override
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    doLogin();
                }
            }

            @Override
            public void keyReleased(KeyEvent e)
            {

            }
        };
        username.addKeyListener(keyListener);
        password.addKeyListener(keyListener);
    }

    private void doLogin()
    {
        String name = username.getText();
        String pwd = new String(password.getPassword());

        if (name == null || name.isEmpty())
        {
            showMessage("请输入用户，注意首字母可能为大写");
        }
        else if (pwd == null || pwd.isEmpty())
        {
            showMessage("请输入密码");
        }
        else
        {
            loginButton.setEnabled(false);
            HttpPostTask task = new HttpPostTask();
            task.setListener(new HttpResponseListener()
            {
                @Override
                public void onResult(JSONObject ret)
                {
                    processLoginResult(ret);
                }
            });

            task.addRequestParam("username", username.getText());
            task.addRequestParam("password", new String(password.getPassword()));
            task.execute("https://rc.shls-leasing.com/api/v1/login");
        }
    }

    private void processLoginResult(JSONObject ret)
    {
        if (ret.get("status").equals("success"))
        {
            MainFrame frame = new MainFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);


            this.dispose();
        }
        else
        {
            showMessage("用户不存在或密码错误");
            loginButton.setEnabled(true);
        }

    }

    private void showMessage(String message)
    {
        if (!statusLabel.isVisible())
        {
            statusLabel.setVisible(true);
        }

        statusLabel.setText(message);
    }
}
