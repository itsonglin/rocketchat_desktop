package com.rc.adapter.message;

import com.rc.adapter.BaseAdapter;
import com.rc.adapter.ViewHolder;
import com.rc.entity.MessageItem;
import com.rc.forms.MainFrame;
import com.rc.utils.TimeUtil;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Created by song on 17-6-2.
 */
public class MessageAdapter extends BaseAdapter<ViewHolder>
{
    private List<MessageItem> messageItems;

    public MessageAdapter(List<MessageItem> messageItems)
    {
        this.messageItems = messageItems;
    }

    @Override
    public int getItemViewType(int position)
    {
        return messageItems.get(position).getMessageType();
    }

    @Override
    public ViewHolder onCreateViewHolder(int viewType)
    {
        switch (viewType)
        {
            case MessageItem.RIGHT_TEXT:{
                return new MessageRightTextViewHolder();
            }
            case MessageItem.LEFT_TEXT:{
                return new MessageLeftTextViewHolder();
            }
            case MessageItem.RIGHT_IMAGE:{
                return new MessageRightImageViewHolder();
            }
        }

        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position)
    {
        if (viewHolder == null)
        {
            return;
        }

        final MessageItem item = messageItems.get(position);
        MessageItem preItem = position == 0 ? null : messageItems.get(position - 1);

        if (viewHolder instanceof MessageRightTextViewHolder)
        {
            processRightTextMessage(viewHolder, item);
        }
        if (viewHolder instanceof MessageLeftTextViewHolder)
        {
            processLeftTextMessage(viewHolder, item);
        }
        if (viewHolder instanceof MessageRightImageViewHolder)
        {
            processRightImageMessage(viewHolder, item);
        }
    }

    /**
     * 我发送的图片
     * @param viewHolder
     * @param item
     */
    private void processRightImageMessage(ViewHolder viewHolder, MessageItem item)
    {
        MessageRightImageViewHolder holder = (MessageRightImageViewHolder) viewHolder;
        holder.time.setText(TimeUtil.diff(item.getTimestamp()));

        ImageIcon imageIcon = new ImageIcon(getClass().getResource(item.getImageAttachments().get(0).getImageUrl()));
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
        holder.image.setIcon(imageIcon);
    }

    @Override
    public int getCount()
    {
        return messageItems.size();
    }

    /**
     * 处理 我发送的文本消息
     *
     * @param viewHolder
     * @param item
     */
    private void processRightTextMessage(ViewHolder viewHolder, final MessageItem item)
    {
        MessageRightTextViewHolder holder = (MessageRightTextViewHolder) viewHolder;
        holder.text.setText(item.getMessageContent());
        holder.time.setText(TimeUtil.diff(item.getTimestamp()));
        //processMessageContent(holder.messageText, item);
        //registerMessageTextListener(holder.messageText, item);

        // 判断是否显示重发按钮
        if (item.isNeedToResend())
        {
           /* holder.resendButton.setVisibility(View.VISIBLE);
            holder.messageSendingProgressBar.setVisibility(View.GONE);
            holder.resendButton.setTag(R.id.message_id, item.getId());*/
        }
        else
        {
            //holder.resendButton.setVisibility(View.GONE);
            // 如果是刚发送的消息，显示正在发送进度条
            if (item.getUpdatedAt() < 1)
            {
                //holder.messageSendingProgressBar.setVisibility(View.VISIBLE);
            }
            else
            {
                //holder.messageSendingProgressBar.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 处理 对方 发送的文本消息
     *
     * @param viewHolder
     * @param item
     */
    private void processLeftTextMessage(ViewHolder viewHolder, final MessageItem item)
    {
        MessageLeftTextViewHolder holder = (MessageLeftTextViewHolder) viewHolder;
        holder.text.setText(item.getMessageContent());
        holder.time.setText(TimeUtil.diff(item.getTimestamp()));
        holder.sender.setText("Song");
    }
}
