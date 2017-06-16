package com.rc.adapter.message;

import com.rc.adapter.ViewHolder;
import com.rc.components.*;
import com.rc.components.message.RCLeftImageMessageBubble;
import com.rc.forms.MainFrame;
import com.rc.utils.FontUtil;

import javax.swing.*;
import java.awt.*;

/**
 * Created by song on 17-6-2.
 */
public class MessageLeftAttachmentViewHolder extends MessageAttachmentViewHolder
{
    //public SizeAutoAdjustTextArea attachmentTitle;
    public JLabel sender = new JLabel();
    //public RCProgressBar progressBar = new RCProgressBar(); // 进度条
    //public JLabel attachmentIcon = new JLabel(); // 附件类型icon

    private RCLeftImageMessageBubble messageBubble = new RCLeftImageMessageBubble();
    //private JPanel timePanel = new JPanel();
    //private JPanel messageAvatarPanel = new JPanel();
    //private JPanel attachmentPanel = new JPanel(); // 附件面板


    public MessageLeftAttachmentViewHolder()
    {
        initComponents();
        initView();
    }

    private void initComponents()
    {
        //messageBubble.add(image);

        int maxWidth = (int) (MainFrame.getContext().currentWindowWidth * 0.427);
        attachmentTitle = new SizeAutoAdjustTextArea(maxWidth);


        time.setForeground(Colors.FONT_GRAY);
        time.setFont(FontUtil.getDefaultFont(12));

        sender.setFont(FontUtil.getDefaultFont(12));
        sender.setForeground(Colors.FONT_GRAY);
        //sender.setVisible(false);

        attachmentPanel.setOpaque(false);

        progressBar.setMaximum(100);
        progressBar.setMinimum(0);
        progressBar.setValue(100);
        progressBar.setUI(new GradientProgressBarUI());
        progressBar.setVisible(false);

        messageBubble.setCursor(new Cursor(Cursor.HAND_CURSOR));

        sizeLabel.setFont(FontUtil.getDefaultFont(12));
        sizeLabel.setForeground(Colors.FONT_GRAY);

    }

    private void initView()
    {
        setLayout(new BorderLayout());

        timePanel.add(time);

        attachmentPanel.setLayout(new GridBagLayout());
        attachmentPanel.add(attachmentIcon, new GBC(0, 0).setWeight(1, 1).setInsets(5,5,5,0));
        attachmentPanel.add(attachmentTitle, new GBC(1, 0).setWeight(100, 1).setAnchor(GBC.NORTH)
                .setInsets(5, 8, 5, 5));
        attachmentPanel.add(progressBar, new GBC(1, 1).setWeight(1, 1).setFill(GBC.HORIZONTAL)
                .setAnchor(GBC.SOUTH).setInsets(0, 8, 5, 5));

        attachmentPanel.add(sizeLabel, new GBC(1, 1).setWeight(1, 1).setFill(GBC.HORIZONTAL).setAnchor(GBC.SOUTH).setInsets(-20,8,5,0));


        messageBubble.add(attachmentPanel);


        JPanel senderMessagePanel = new JPanel();
        senderMessagePanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP, 0,2,true, false));
        senderMessagePanel.add(sender);
        senderMessagePanel.add(messageBubble);

        messageAvatarPanel.setLayout(new GridBagLayout());
        messageAvatarPanel.add(avatar, new GBC(1, 0).setWeight(1, 1).setAnchor(GBC.NORTH).setInsets(4, 0,0,0));
        messageAvatarPanel.add(senderMessagePanel, new GBC(2, 0)
                .setWeight(1000, 1)
                .setAnchor(GBC.WEST)
                .setInsets(0,5,5,0));

        add(timePanel, BorderLayout.NORTH);
        add(messageAvatarPanel, BorderLayout.CENTER);
    }
}
