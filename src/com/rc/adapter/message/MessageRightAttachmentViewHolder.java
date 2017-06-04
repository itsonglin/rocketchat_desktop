package com.rc.adapter.message;

import com.rc.adapter.ViewHolder;
import com.rc.components.*;
import com.rc.components.message.RCRightAttachmentMessageBubble;
import com.rc.components.message.RCRightImageMessageBubble;
import com.rc.forms.MainFrame;
import com.rc.helper.AttachmentIconHelper;
import com.rc.utils.FontUtil;

import javax.swing.*;
import java.awt.*;

/**
 * Created by song on 17-6-3.
 */
public class MessageRightAttachmentViewHolder extends ViewHolder
{
    public SizeAutoAdjustTextArea attachmentTitle;
    public JLabel avatar = new JLabel();
    public JLabel time = new JLabel();
    public JLabel resend = new JLabel(); // 重发按钮
    public RCProgressBar progressBar = new RCProgressBar(); // 进度条

    private RCRightAttachmentMessageBubble attachmentBubble = new RCRightAttachmentMessageBubble();
    private JPanel timePanel = new JPanel(); // 时间面板
    private JPanel messageAvatarPanel = new JPanel(); // 消息 + 头像组合面板
    private JPanel attachmentPanel = new JPanel(); // 附件面板
    public JLabel attachmentIcon = new JLabel(); // 附件类型icon

    public MessageRightAttachmentViewHolder()
    {
        initComponents();
        initView();
    }

    private void initComponents()
    {
        int maxWidth = (int) (MainFrame.getContext().currentWindowWidth * 0.427);
        attachmentTitle = new SizeAutoAdjustTextArea(maxWidth);

        ImageIcon avatarIcon = new ImageIcon(getClass().getResource("/image/avatar.jpg"));
        avatarIcon.setImage(avatarIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));
        avatar.setIcon(avatarIcon);

        time.setForeground(Colors.FONT_GRAY);
        time.setFont(FontUtil.getDefaultFont(12));

        ImageIcon resendIcon = new ImageIcon(getClass().getResource("/image/resend.png"));
        resendIcon.setImage(resendIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        resend.setIcon(resendIcon);
        //resend.setVisible(false);


        attachmentPanel.setOpaque(false);


        //ImageIcon attachmentTypeIcon = new ImageIcon(getClass().getResource("/image/pdf.png"));
        //attachmentIcon.setIcon(attachmentTypeIcon);

        //preferredAttachmentSize();
        //attachmentTitle.setPreferredSize(new Dimension(100, 100));

        progressBar.setMaximum(100);
        progressBar.setMinimum(0);
        progressBar.setValue(100);
        progressBar.setUI(new GradientProgressBarUI());

        attachmentBubble.setCursor(new Cursor(Cursor.HAND_CURSOR));

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

        attachmentBubble.add(attachmentPanel);


        JPanel resendAttachmentPanel = new JPanel();
        resendAttachmentPanel.add(resend, BorderLayout.WEST);
        resendAttachmentPanel.add(attachmentBubble, BorderLayout.CENTER);

        messageAvatarPanel.setLayout(new GridBagLayout());
        messageAvatarPanel.add(resendAttachmentPanel, new GBC(1, 0).setWeight(1000, 1)
                .setAnchor(GBC.EAST));
        messageAvatarPanel.add(avatar, new GBC(2, 0).setWeight(1, 1).setAnchor(GBC.NORTH)
                .setInsets(5, 0, 0, 0));

        add(timePanel, BorderLayout.NORTH);
        add(messageAvatarPanel, BorderLayout.CENTER);
    }

}
