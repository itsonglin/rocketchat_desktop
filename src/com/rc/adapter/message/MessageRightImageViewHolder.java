package com.rc.adapter.message;

import com.rc.adapter.ViewHolder;
import com.rc.components.message.RCRightTextMessageBubble;

import javax.swing.*;
import java.awt.*;

/**
 * Created by song on 17-6-3.
 */
public class MessageRightImageViewHolder extends ViewHolder
{
    private RCRightTextMessageBubble image = new RCRightTextMessageBubble();

    public MessageRightImageViewHolder()
    {
        initComponents();
        initView();
    }

    private void initComponents()
    {
        //ImageIcon imageIcon = new ImageIcon(getClass().getResource("/image/avatar.jpg"));
        //image.(imageIcon);
    }

    private void initView()
    {
        add(image);
    }
}
