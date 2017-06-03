package com.rc.adapter.message;

import com.rc.adapter.ViewHolder;
import com.rc.components.Colors;
import com.rc.components.GBC;
import com.rc.components.message.RCRightImageMessageBubble;
import com.rc.forms.MainFrame;
import com.rc.utils.FontUtil;

import javax.swing.*;
import java.awt.*;

/**
 * Created by song on 17-6-3.
 */
public class MessageRightImageViewHolder extends ViewHolder
{
    public JLabel image = new JLabel();
    public JLabel avatar = new JLabel();
    public JLabel time = new JLabel();
    public JLabel resend = new JLabel(); // 重发按钮
    public JLabel sendingProgress = new JLabel(); // 正在发送

    private RCRightImageMessageBubble imageBubble = new RCRightImageMessageBubble();
    private JPanel timePanel = new JPanel();
    private JPanel messageAvatarPanel = new JPanel();

    public MessageRightImageViewHolder()
    {
        initComponents();
        initView();
    }

    private void initComponents()
    {
/*        ImageIcon imageIcon = new ImageIcon(getClass().getResource("/image/avatar.jpg"));
        int width = imageIcon.getIconWidth();
        int height = imageIcon.getIconHeight();
        float scale = width / height * 1.0F;

        // 限制图片显示大小
        int maxImageWidth = (int) (MainFrame.getContext().currentWindowWidth * 0.2);
        if (width > maxImageWidth)
        {
            width = maxImageWidth;
            height = (int) (width / scale);
        }
        imageIcon.setImage(imageIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
        image.setIcon(imageIcon);*/
        imageBubble.add(image);


        ImageIcon avatarIcon = new ImageIcon(getClass().getResource("/image/avatar.jpg"));
        avatarIcon.setImage(avatarIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));
        avatar.setIcon(avatarIcon);

        time.setForeground(Colors.FONT_GRAY);
        time.setFont(FontUtil.getDefaultFont(12));

        ImageIcon resendIcon = new ImageIcon(getClass().getResource("/image/resend.png"));
        resendIcon.setImage(resendIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        resend.setIcon(resendIcon);
        resend.setVisible(false);

        ImageIcon sendingIcon = new ImageIcon(getClass().getResource("/image/sending.gif"));
        sendingProgress.setIcon(sendingIcon);
        sendingProgress.setVisible(false);
    }

    private void initView()
    {
        setLayout(new BorderLayout());
        timePanel.add(time);

        JPanel resendImagePanel = new JPanel();
        resendImagePanel.add(resend, BorderLayout.WEST);
        resendImagePanel.add(sendingProgress, BorderLayout.WEST);
        resendImagePanel.add(imageBubble, BorderLayout.CENTER);

        messageAvatarPanel.setLayout(new GridBagLayout());
        messageAvatarPanel.add(resendImagePanel, new GBC(1, 0).setWeight(1000, 1).setAnchor(GBC.EAST).setInsets(0, 0, 0, 5));
        messageAvatarPanel.add(avatar, new GBC(2, 0).setWeight(1, 1).setAnchor(GBC.NORTH).setInsets(5, 0, 0, 0));

        add(timePanel, BorderLayout.NORTH);
        add(messageAvatarPanel, BorderLayout.CENTER);
    }
}
