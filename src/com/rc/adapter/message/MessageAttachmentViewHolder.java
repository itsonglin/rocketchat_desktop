package com.rc.adapter.message;

import com.rc.components.RCProgressBar;
import com.rc.components.SizeAutoAdjustTextArea;
import com.rc.components.message.RCAttachmentMessageBubble;
import com.rc.forms.MainFrame;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by song on 16/06/2017.
 */
public class MessageAttachmentViewHolder extends BaseMessageViewHolder
{
    public SizeAutoAdjustTextArea attachmentTitle;
    public RCProgressBar progressBar = new RCProgressBar(); // 进度条
    public JPanel timePanel = new JPanel(); // 时间面板
    public JPanel messageAvatarPanel = new JPanel(); // 消息 + 头像组合面板
    public JPanel attachmentPanel = new JPanel(); // 附件面板
    public JLabel attachmentIcon = new JLabel(); // 附件类型icon
    public JLabel sizeLabel = new JLabel();
    public RCAttachmentMessageBubble messageBubble;

    public MessageAttachmentViewHolder()
    {
        initComponents();
        setListeners();
    }

    private void setListeners()
    {
        MouseAdapter listener = new MouseAdapter()
        {
            @Override
            public void mouseEntered(MouseEvent e)
            {
                messageBubble.setActiveStatus(true);
                super.mouseEntered(e);
            }

            @Override
            public void mouseExited(MouseEvent e)
            {
                messageBubble.setActiveStatus(false);
                super.mouseExited(e);
            }
        };

        attachmentPanel.addMouseListener(listener);
        attachmentTitle.addMouseListener(listener);

    }

    private void initComponents()
    {
        int maxWidth = (int) (MainFrame.getContext().currentWindowWidth * 0.427);
        attachmentTitle = new SizeAutoAdjustTextArea(maxWidth);
    }
}
