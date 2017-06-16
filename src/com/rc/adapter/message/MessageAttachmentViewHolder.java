package com.rc.adapter.message;

import com.rc.components.RCProgressBar;
import com.rc.components.SizeAutoAdjustTextArea;

import javax.swing.*;

/**
 * Created by song on 16/06/2017.
 */
public class MessageAttachmentViewHolder extends BaseMessageViewHolder
{
    protected SizeAutoAdjustTextArea attachmentTitle;
    public RCProgressBar progressBar = new RCProgressBar(); // 进度条
    protected JPanel timePanel = new JPanel(); // 时间面板
    protected JPanel messageAvatarPanel = new JPanel(); // 消息 + 头像组合面板
    protected JPanel attachmentPanel = new JPanel(); // 附件面板
    protected JLabel attachmentIcon = new JLabel(); // 附件类型icon
}
