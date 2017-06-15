package com.rc.adapter.message;

import com.rc.adapter.ViewHolder;
import com.rc.components.Colors;
import com.rc.components.GBC;
import com.rc.components.SizeAutoAdjustTextArea;
import com.rc.components.message.MessagePopupMenu;
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
public class MessageRightTextViewHolder extends BaseMessageViewHolder
{
    //public JLabel avatar = new JLabel();
    //public JLabel time = new JLabel();
    public SizeAutoAdjustTextArea text;
    public RCRightImageMessageBubble messageBubble = new RCRightImageMessageBubble();
    //public RCRightTextMessageBubble text = new RCRightTextMessageBubble();
    public JLabel resend = new JLabel(); // 重发按钮
    public JLabel sendingProgress = new JLabel(); // 正在发送

    private JPanel timePanel = new JPanel();
    private JPanel messageAvatarPanel = new JPanel();

    private MessagePopupMenu popupMenu = new MessagePopupMenu();

    public MessageRightTextViewHolder()
    {
        initComponents();
        initView();
    }

    private void initComponents()
    {
        int maxWidth = (int) (MainFrame.getContext().currentWindowWidth * 0.5);
        text = new SizeAutoAdjustTextArea(maxWidth);


        time.setForeground(Colors.FONT_GRAY);
        time.setFont(FontUtil.getDefaultFont(12));

        ImageIcon resendIcon = new ImageIcon(getClass().getResource("/image/resend.png"));
        resendIcon.setImage(resendIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        resend.setIcon(resendIcon);
        resend.setVisible(false);
        resend.setToolTipText("消息发送失败，点击重新发送");

        ImageIcon sendingIcon = new ImageIcon(getClass().getResource("/image/sending.gif"));
        sendingProgress.setIcon(sendingIcon);
        sendingProgress.setVisible(false);


        text.setCaretPosition(text.getDocument().getLength());
        text.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseReleased(MouseEvent e)
            {
                /*System.out.println(e);
                System.out.println(e.getX() + ", " + e.getY() + ", " + text.getWidth() + ", " + text.getHeight());

                if (e.getX() > text.getWidth() || e.getY() > text.getHeight())
                {
                    messageBubble.setBackgroundIcon(messageBubble.getBackgroundNormalIcon());
                }*/
                super.mouseReleased(e);
            }

            @Override
            public void mouseEntered(MouseEvent e)
            {
                messageBubble.setBackgroundIcon(messageBubble.getBackgroundActiveIcon());
                super.mouseEntered(e);
            }

            @Override
            public void mouseClicked(MouseEvent e)
            {
                if (e.getButton() == MouseEvent.BUTTON3)
                {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });


    }

    private void initView()
    {
        setLayout(new BorderLayout());
        timePanel.add(time);

        messageBubble.add(text, BorderLayout.CENTER);
        //JTextArea text = new JTextArea("addComponentListener(new Cpter()你好你好啊，。/");
        messageBubble.add(text, BorderLayout.CENTER);

        JPanel resendTextPanel = new JPanel();
        resendTextPanel.add(resend, BorderLayout.WEST);
        resendTextPanel.add(sendingProgress, BorderLayout.WEST);
        resendTextPanel.add(messageBubble, BorderLayout.CENTER);

        messageAvatarPanel.setLayout(new GridBagLayout());
        messageAvatarPanel.add(resendTextPanel, new GBC(1, 0).setWeight(1000, 1).setAnchor(GBC.EAST).setInsets(0, 0, 5, 0));
        messageAvatarPanel.add(avatar, new GBC(2, 0).setWeight(1, 1).setAnchor(GBC.NORTH).setInsets(5, 0, 0, 10));

        add(timePanel, BorderLayout.NORTH);
        add(messageAvatarPanel, BorderLayout.CENTER);
    }
}
