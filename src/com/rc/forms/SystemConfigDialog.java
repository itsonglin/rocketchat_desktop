package com.rc.forms;

import com.rc.app.Launcher;
import com.rc.app.ShadowBorder;
import com.rc.components.*;
import com.rc.db.service.ContactsUserService;
import com.rc.utils.FontUtil;
import com.rc.utils.OSUtil;
import com.sun.awt.AWTUtilities;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by song on 07/06/2017.
 */
public class SystemConfigDialog extends JDialog
{
    private static SystemConfigDialog context;
    private JPanel buttonPanel;
    private JButton cancelButton;
    private JButton okButton;

    private JPanel settingPanel;
    private JPanel settingMenuPanel;
    private JPanel settingAreaPanel;
    private JLabel changeAvatarLabel;
    private JLabel changePasswordLabel;
    private ChangeAvatarPanel changeAvatarPanel;
    private ChangePasswordPanel changePasswordPanel;


    private JLabel selectedLabel;

    public static final String CHANGE_AVATAR = "CHANGE_AVATAR";
    public static final String CHANGE_PASSWORD = "CHANGE_PASSWORD";

    private CardLayout cardLayout = new CardLayout();

    private ContactsUserService contactsUserService = Launcher.contactsUserService;


    public static final int DIALOG_WIDTH = 580;
    public static final int DIALOG_HEIGHT = 500;


    public SystemConfigDialog(Frame owner, boolean modal)
    {
        super(owner, modal);
        context = this;

        initComponents();
        initData();

        initView();
        setListeners();
    }

    private void initData()
    {

    }


    private void initComponents()
    {
        int posX = MainFrame.getContext().getX();
        int posY = MainFrame.getContext().getY();

        posX = posX + (MainFrame.getContext().currentWindowWidth - DIALOG_WIDTH) / 2;
        posY = posY + (MainFrame.getContext().currentWindowHeight - DIALOG_HEIGHT) / 2;
        setBounds(posX, posY, DIALOG_WIDTH, DIALOG_HEIGHT);
        setUndecorated(true);

        getRootPane().setBorder(new LineBorder(Colors.LIGHT_GRAY));

        if (OSUtil.getOsType() != OSUtil.Mac_OS)
        {
            // 边框阴影，但是会导致字体失真
            AWTUtilities.setWindowOpaque(this, false);
            //getRootPane().setOpaque(false);
            getRootPane().setBorder(ShadowBorder.newInstance());
        }

        // 按钮组
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        cancelButton = new RCButton("取消");
        cancelButton.setForeground(Colors.FONT_BLACK);
        okButton = new RCButton("确定", Colors.MAIN_COLOR, Colors.MAIN_COLOR_DARKER, Colors.MAIN_COLOR_DARKER);

        // 设置面板
        settingPanel = new JPanel();

        settingMenuPanel = new JPanel();
        settingAreaPanel = new JPanel();
        settingAreaPanel.setBorder(new RCBorder(RCBorder.LEFT, Colors.SCROLL_BAR_TRACK_LIGHT));


        Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);

        // 设置头像按钮
        changeAvatarLabel = new JLabel("更改头像");
        changeAvatarLabel.setFont(FontUtil.getDefaultFont(13));
        changeAvatarLabel.setForeground(Colors.DARKER);
        changeAvatarLabel.setBorder(new RCBorder(RCBorder.BOTTOM, Colors.SHADOW));
        changeAvatarLabel.setHorizontalAlignment(SwingConstants.CENTER);
        changeAvatarLabel.setPreferredSize(new Dimension(40, 25));
        changeAvatarLabel.setCursor(handCursor);
        changeAvatarLabel.setOpaque(true);

        // 更改密码按钮
        changePasswordLabel = new JLabel("修改密码");
        changePasswordLabel.setFont(FontUtil.getDefaultFont(13));
        changePasswordLabel.setForeground(Colors.DARKER);
        changePasswordLabel.setBorder(new RCBorder(RCBorder.BOTTOM, Colors.SHADOW));
        changePasswordLabel.setHorizontalAlignment(SwingConstants.CENTER);
        changePasswordLabel.setPreferredSize(new Dimension(40, 25));
        changePasswordLabel.setCursor(handCursor);
        changePasswordLabel.setOpaque(true);


        // 更改头像面板
        changeAvatarPanel = new ChangeAvatarPanel();
        // 更改密码面板
        changePasswordPanel = new ChangePasswordPanel();

    }


    private void initView()
    {
        buttonPanel.add(cancelButton, new GBC(0, 0).setWeight(1, 1).setInsets(15, 0, 0, 0));
        buttonPanel.add(okButton, new GBC(1, 0).setWeight(1, 1));

        settingPanel.setLayout(new GridBagLayout());
        settingPanel.add(settingMenuPanel, new GBC(0, 0).setWeight(1, 1).setFill(GBC.BOTH).setInsets(10,0,0,0));
        settingPanel.add(settingAreaPanel, new GBC(1, 0).setWeight(6, 1).setFill(GBC.BOTH).setInsets(10,0,0,0));

        settingMenuPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP, 0, 0, true, false));
        settingMenuPanel.add(changeAvatarLabel);
        settingMenuPanel.add(changePasswordLabel);

        settingAreaPanel.setLayout(cardLayout);
        settingAreaPanel.add(changeAvatarPanel, CHANGE_AVATAR);
        settingAreaPanel.add(changePasswordPanel, CHANGE_PASSWORD);

        add(settingPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        selectedLabel(changeAvatarLabel);
    }

    private void setListeners()
    {
        cancelButton.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                setVisible(false);

                super.mouseClicked(e);
            }
        });

        MouseAdapter itemMouseListener = new MouseAdapter()
        {
            @Override
            public void mouseEntered(MouseEvent e)
            {
                JLabel source = ((JLabel) e.getSource());
                if (source != selectedLabel)
                {
                    source.setBackground(Colors.ITEM_SELECTED_LIGHT);
                }
                super.mouseEntered(e);
            }

            @Override
            public void mouseExited(MouseEvent e)
            {
                JLabel source = ((JLabel) e.getSource());
                if (source != selectedLabel)
                {
                    source.setBackground(Colors.WINDOW_BACKGROUND);
                }
                super.mouseExited(e);
            }

            @Override
            public void mouseClicked(MouseEvent e)
            {
                JLabel source = ((JLabel) e.getSource());

                if (source != selectedLabel)
                {
                    selectedLabel(source);

                    if (source.getText().equals("更改头像"))
                    {
                        cardLayout.show(settingAreaPanel, CHANGE_AVATAR);
                    }
                    else if (source.getText().equals("修改密码"))
                    {
                        cardLayout.show(settingAreaPanel, CHANGE_PASSWORD);
                    }
                }


                super.mouseClicked(e);
            }
        };

        changeAvatarLabel.addMouseListener(itemMouseListener);
        changePasswordLabel.addMouseListener(itemMouseListener);
    }

    private void selectedLabel(JLabel label)
    {
        selectedLabel = label;

        for (Component component : settingMenuPanel.getComponents())
        {
            component.setBackground(Colors.WINDOW_BACKGROUND);
        }

        label.setBackground(Colors.SCROLL_BAR_TRACK_LIGHT);
    }


    public static SystemConfigDialog getContext()
    {
        return context;
    }
}
