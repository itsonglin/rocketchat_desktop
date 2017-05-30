package com.rc.forms;

import com.rc.components.Colors;
import com.rc.components.GBC;
import com.rc.components.ImagePanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Created by song on 17-5-29.
 */
public class MyInfoPanel extends ParentAvailablePanel
{
    private ImagePanel avatar;
    private JLabel username;
    private JLabel menuIcon;

    public MyInfoPanel(JPanel parent)
    {
        super(parent);

        initComponents();
        initView();
    }

    private void initComponents()
    {

        //GImage.setBorder(new SubtleSquareBorder(true));

        try
        {
            avatar = new ImagePanel(ImageIO.read(getClass().getResourceAsStream("/image/avatar.jpg")));
            avatar.setPreferredSize(new Dimension(50,50));
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        username = new JLabel();
        username.setText("Song");
        username.setForeground(Colors.FONT_WHITE);


        menuIcon = new JLabel();
        menuIcon.setText("+");
        menuIcon.setForeground(Colors.FONT_WHITE);
    }

    private void initView()
    {
        this.setBackground(Colors.DARK);
        this.setLayout(new GridBagLayout());

        add(avatar, new GBC(0, 0).setFill(GBC.NONE).setWeight(2, 1));
        add(username, new GBC(1, 0).setFill(GBC.BOTH).setWeight(7, 1));
        add (menuIcon, new GBC(2, 0).setFill(GBC.BOTH).setWeight(1, 1));
    }



}
