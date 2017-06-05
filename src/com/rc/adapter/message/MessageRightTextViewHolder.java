package com.rc.adapter.message;

import com.rc.adapter.ViewHolder;
import com.rc.components.Colors;
import com.rc.components.GBC;
import com.rc.components.SizeAutoAdjustTextArea;
import com.rc.components.message.RCRightImageMessageBubble;
import com.rc.forms.MainFrame;
import com.rc.utils.FontUtil;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by song on 17-6-2.
 */
public class MessageRightTextViewHolder extends ViewHolder
{
    public JLabel avatar = new JLabel();
    public JLabel time = new JLabel();
    public SizeAutoAdjustTextArea text;
    public RCRightImageMessageBubble messageBubble = new RCRightImageMessageBubble();
    //public RCRightTextMessageBubble text = new RCRightTextMessageBubble();
    public JLabel resend = new JLabel(); // 重发按钮
    public JLabel sendingProgress = new JLabel(); // 正在发送

    private JPanel timePanel = new JPanel();
    private JPanel messageAvatarPanel = new JPanel();

    public MessageRightTextViewHolder()
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

        ImageIcon resendIcon = new ImageIcon(getClass().getResource("/image/resend.png"));
        resendIcon.setImage(resendIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        resend.setIcon(resendIcon);
        resend.setVisible(false);

        ImageIcon sendingIcon = new ImageIcon(getClass().getResource("/image/sending.gif"));
        sendingProgress.setIcon(sendingIcon);
        sendingProgress.setVisible(false);


        text.setCaretPosition(text.getDocument().getLength());
        text.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseReleased(MouseEvent e)
            {
                messageBubble.setBackgroundIcon(messageBubble.getBackgroundNormalIcon());
                super.mouseReleased(e);
            }

            @Override
            public void mouseEntered(MouseEvent e)
            {
                messageBubble.setBackgroundIcon(messageBubble.getBackgroundActiveIcon());
                super.mouseEntered(e);
            }
        });

    }

    private void initView()
    {
        setLayout(new BorderLayout());
        timePanel.add(time);

        messageBubble.add(text, BorderLayout.CENTER);

        JPanel resendTextPanel = new JPanel();
        resendTextPanel.add(resend, BorderLayout.WEST);
        resendTextPanel.add(sendingProgress, BorderLayout.WEST);
        resendTextPanel.add(messageBubble, BorderLayout.CENTER);

        messageAvatarPanel.setLayout(new GridBagLayout());
        messageAvatarPanel.add(resendTextPanel, new GBC(1, 0).setWeight(1000, 1).setAnchor(GBC.EAST).setInsets(0, 0, 5, 0));
        messageAvatarPanel.add(avatar, new GBC(2, 0).setWeight(1, 1).setAnchor(GBC.NORTH).setInsets(5, 0, 0, 0));

        add(timePanel, BorderLayout.NORTH);
        add(messageAvatarPanel, BorderLayout.CENTER);
    }
}
