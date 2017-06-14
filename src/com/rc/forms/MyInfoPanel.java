package com.rc.forms;

import com.rc.app.Launcher;
import com.rc.components.Colors;
import com.rc.components.GBC;
import com.rc.components.ImagePanel;
import com.rc.components.message.MainOperationPopupMenu;
import com.rc.db.service.CurrentUserService;
import com.rc.listener.AbstractMouseListener;
import com.rc.utils.AvatarUtil;
import com.rc.utils.FontUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.IOException;

/**
 * Created by song on 17-5-29.
 */
public class MyInfoPanel extends ParentAvailablePanel
{
    private ImagePanel avatar;
    private JLabel username;
    private JLabel menuIcon;
    private CurrentUserService currentUserService = Launcher.currentUserService;

    MainOperationPopupMenu mainOperationPopupMenu;


    public MyInfoPanel(JPanel parent)
    {
        super(parent);

        initComponents();
        setListeners();
        initView();
    }


    private void initComponents()
    {

        //GImage.setBorder(new SubtleSquareBorder(true));

        Image image = AvatarUtil.createOrLoadUserAvatar("song");

        //avatar = new ImagePanel(ImageIO.read(getClass().getResourceAsStream("/image/avatar.jpg")));
        avatar = new ImagePanel(image);

        avatar.setPreferredSize(new Dimension(50, 50));
        avatar.setCursor(new Cursor(Cursor.HAND_CURSOR));


        username = new JLabel();
        username.setText("松");
        username.setFont(FontUtil.getDefaultFont(16));
        username.setForeground(Colors.FONT_WHITE);


        menuIcon = new JLabel();
        menuIcon.setIcon(new ImageIcon(getClass().getResource("/image/options.png")));
        menuIcon.setForeground(Colors.FONT_WHITE);
        menuIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));


        mainOperationPopupMenu = new MainOperationPopupMenu();
    }

    private void setListeners()
    {
        menuIcon.addMouseListener(new AbstractMouseListener()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                Component component = e.getComponent();
                mainOperationPopupMenu.show(component, -112, 50);
                super.mouseClicked(e);
            }
        });
    }

    private void initView()
    {
        this.setBackground(Colors.DARK);
        this.setLayout(new GridBagLayout());

        add(avatar, new GBC(0, 0).setFill(GBC.NONE).setWeight(2, 1));
        add(username, new GBC(1, 0).setFill(GBC.BOTH).setWeight(7, 1));
        add(menuIcon, new GBC(2, 0).setFill(GBC.BOTH).setWeight(1, 1));
    }
}
