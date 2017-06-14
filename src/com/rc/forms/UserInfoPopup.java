package com.rc.forms;


import com.rc.components.Colors;
import com.rc.components.GBC;
import com.rc.components.RCButton;
import com.rc.utils.AvatarUtil;

import javax.swing.*;
import java.awt.*;

/**
 * Created by song on 07/06/2017.
 */
public class UserInfoPopup extends JPopupMenu
{
    private JPanel contentPanel;
    private JLabel avatarLabel;
    private JLabel usernameLabel;
    private JButton sendButton;
    private String username;

    public UserInfoPopup(String username)
    {
        this.username = username;
        initComponents();
        initView();
    }

    private void initComponents()
    {
        setBackground(Colors.WINDOW_BACKGROUND_LIGHT);


        contentPanel = new JPanel();
        contentPanel.setPreferredSize(new Dimension(200, 200));
        contentPanel.setBackground(Colors.WINDOW_BACKGROUND_LIGHT);

        avatarLabel = new JLabel();
        ImageIcon imageIcon = new ImageIcon();
        imageIcon.setImage(AvatarUtil.createOrLoadUserAvatar(username).getScaledInstance(60, 60, Image.SCALE_SMOOTH));
        avatarLabel.setIcon(imageIcon);

        usernameLabel = new JLabel();
        usernameLabel.setText("Song");

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
        avatarUsernamePanel.add(avatarLabel);
        avatarUsernamePanel.add(usernameLabel);
        avatarUsernamePanel.setPreferredSize(new Dimension(180, 80));


        JPanel sendButtonPanel = new JPanel();
        sendButtonPanel.setBackground(Colors.WINDOW_BACKGROUND_LIGHT);
        sendButtonPanel.add(sendButton);

        contentPanel.add(avatarUsernamePanel, new GBC(0,0).setWeight(1,1));
        contentPanel.add(sendButtonPanel, new GBC(0,1).setWeight(1,1));

    }

}
