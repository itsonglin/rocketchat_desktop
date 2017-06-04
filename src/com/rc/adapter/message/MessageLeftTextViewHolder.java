package com.rc.adapter.message;

import com.rc.adapter.ViewHolder;
import com.rc.components.Colors;
import com.rc.components.GBC;
import com.rc.components.SizeAutoAdjustTextArea;
import com.rc.components.VerticalFlowLayout;
import com.rc.components.message.RCLeftImageMessageBubble;
import com.rc.forms.MainFrame;
import com.rc.utils.FontUtil;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Created by song on 17-6-2.
 */
public class MessageLeftTextViewHolder extends ViewHolder
{
    public JLabel sender = new JLabel();
    public JLabel avatar = new JLabel();
    public JLabel time = new JLabel();
    //public RCLeftTextMessageBubble text = new RCLeftTextMessageBubble();

    public SizeAutoAdjustTextArea text;
    public RCLeftImageMessageBubble messageBubble = new RCLeftImageMessageBubble();

    private JPanel timePanel = new JPanel();
    private JPanel messageAvatarPanel = new JPanel();

    public MessageLeftTextViewHolder()
    {
        initComponents();
        initView();
    }

    private void initComponents()
    {
        int maxWidth = (int) (MainFrame.getContext().currentWindowWidth * 0.5);
        text = new SizeAutoAdjustTextArea(maxWidth);

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

        sender.setFont(FontUtil.getDefaultFont(12));
        sender.setForeground(Colors.FONT_GRAY);
        //sender.setVisible(false);
    }

    private void initView()
    {
        setLayout(new BorderLayout());
        timePanel.add(time);

        messageBubble.add(text);

        JPanel senderMessagePanel = new JPanel();
        senderMessagePanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP, 0,0,true, false));
        senderMessagePanel.add(sender);
        senderMessagePanel.add(messageBubble);

        messageAvatarPanel.setLayout(new GridBagLayout());
        messageAvatarPanel.add(avatar, new GBC(1, 0).setWeight(1, 1).setAnchor(GBC.NORTH).setInsets(5, 0,0,0));
        messageAvatarPanel.add(senderMessagePanel, new GBC(2, 0)
                .setWeight(1000, 1)
                .setAnchor(GBC.WEST)
                .setInsets(0,5,0,0));

        add(timePanel, BorderLayout.NORTH);
        add(messageAvatarPanel, BorderLayout.CENTER);
    }
}
