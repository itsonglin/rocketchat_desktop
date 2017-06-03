package com.rc.adapter.message;

import com.rc.adapter.ViewHolder;
import com.rc.components.Colors;
import com.rc.components.GBC;
import com.rc.components.VerticalFlowLayout;
import com.rc.components.message.NinePatchImageIcon;
import com.rc.components.message.RCRightTextMessageBubble;
import com.rc.utils.FontUtil;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Created by song on 17-6-2.
 */
public class MessageRightTextViewHolder extends ViewHolder
{
    public JLabel avatar = new JLabel();
    public JLabel time = new JLabel();
    public RCRightTextMessageBubble text = new RCRightTextMessageBubble();
    private JPanel timePanel = new JPanel();
    private JPanel messageAvatarPanel = new JPanel();

    public MessageRightTextViewHolder()
    {
        initComponents();
        initView();
    }

    private void initComponents()
    {
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
        setLayout(new BorderLayout());
        timePanel.add(time);

        messageAvatarPanel.setLayout(new GridBagLayout());
        messageAvatarPanel.add(text, new GBC(1, 0)
                .setWeight(1000, 1)
                .setAnchor(GBC.EAST)
                .setInsets(0,0,0,5));
        messageAvatarPanel.add(avatar, new GBC(2, 0).setWeight(1, 1).setAnchor(GBC.NORTH));


        add(timePanel, BorderLayout.NORTH);
        add(messageAvatarPanel, BorderLayout.CENTER);
    }
}
