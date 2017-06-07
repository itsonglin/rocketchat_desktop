package com.rc.forms;


import com.rc.components.Colors;
import com.rc.components.GBC;
import com.rc.components.RCButton;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Created by song on 07/06/2017.
 */
public class UserInfoPopup extends JPopupMenu
{
    private JPanel contentPanel;
    private JLabel avatar;
    private JLabel username;
    private JButton sendButton;

    public UserInfoPopup()
    {
        initComponents();
        initView();
    }

    private void initComponents()
    {
        setBackground(Colors.WINDOW_BACKGROUND_LIGHT);


        contentPanel = new JPanel();
        contentPanel.setPreferredSize(new Dimension(200, 200));
        contentPanel.setBackground(Colors.WINDOW_BACKGROUND_LIGHT);

        avatar = new JLabel();
        ImageIcon imageIcon = new ImageIcon(getClass().getResource("/image/avatar.jpg"));
        imageIcon.setImage(imageIcon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH));
        avatar.setIcon(imageIcon);

        username = new JLabel();
        username.setText("Song");

        sendButton = new RCButton("发消息");
        sendButton.setPreferredSize(new Dimension(180, 40));
        sendButton.setForeground(Colors.FONT_BLACK);
    }

    private void initView()
    {
        add(contentPanel);

        contentPanel.setLayout(new GridBagLayout());

        JPanel avatarUsernamePanel = new JPanel();
        avatarUsernamePanel.setBackground(Colors.WINDOW_BACKGROUND_LIGHT);

        avatarUsernamePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        avatarUsernamePanel.add(avatar);
        avatarUsernamePanel.add(username);
        avatarUsernamePanel.setPreferredSize(new Dimension(180, 80));


        JPanel sendButtonPanel = new JPanel();
        sendButtonPanel.setBackground(Colors.WINDOW_BACKGROUND_LIGHT);
        sendButtonPanel.add(sendButton);

        contentPanel.add(avatarUsernamePanel, new GBC(0,0).setWeight(1,1));
        contentPanel.add(sendButtonPanel, new GBC(0,1).setWeight(1,1));

    }

}
