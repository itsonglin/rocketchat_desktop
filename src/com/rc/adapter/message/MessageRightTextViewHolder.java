package com.rc.adapter.message;

import com.rc.adapter.ViewHolder;
import com.rc.components.*;
import com.rc.utils.FontUtil;
import com.rc.utils.TimeUtil;

import javax.imageio.plugins.jpeg.JPEGHuffmanTable;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.Time;

/**
 * Created by song on 17-6-2.
 */
public class MessageRightTextViewHolder extends ViewHolder
{
    public JLabel avatar = new JLabel();
    public JLabel time = new JLabel();
    public RCMessageBubble text = new RCMessageBubble();
    private JPanel timePanel = new JPanel();
    private NinePatchImageIcon messageImageIcon;

    public MessageRightTextViewHolder()
    {
        initComponents();
        initView();
    }

    private void initComponents()
    {
        messageImageIcon = new NinePatchImageIcon(this.getClass().getResource("/image/right.9.png"));
        text.setBackgroundIcon(messageImageIcon);
        ImageIcon imageIcon = new ImageIcon(getClass().getResource("/image/avatar.jpg"));
        imageIcon.setImage(imageIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));
        avatar.setIcon(imageIcon);

        addComponentListener(new ComponentAdapter()
        {
            @Override
            public void componentResized(ComponentEvent e)
            {
                text.setText(text.getText());
            }
        });

        time.setForeground(Colors.FONT_GRAY);
        time.setFont(FontUtil.getDefaultFont(12));
    }

    private void initView()
    {
        setLayout(new GridBagLayout());

        timePanel.add(time);
        add(timePanel, new GBC(1, 0).setWeight(1, 1).setFill(GBC.HORIZONTAL));
        add(text, new GBC(1, 1).setWeight(100, 1).setAnchor(GBC.EAST));
        add(avatar, new GBC(2, 1).setWeight(5, 1).setAnchor(GBC.NORTH));
    }
}
