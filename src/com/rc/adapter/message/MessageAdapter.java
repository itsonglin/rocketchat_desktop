package com.rc.adapter.message;

import com.rc.adapter.BaseAdapter;
import com.rc.adapter.ViewHolder;
import com.rc.app.Launcher;
import com.rc.db.model.CurrentUser;
import com.rc.db.service.CurrentUserService;
import com.rc.entity.MessageItem;
import com.rc.forms.MainFrame;
import com.rc.forms.UserInfoPopup;
import com.rc.helper.AttachmentIconHelper;
import com.rc.listener.AbstractMouseListener;
import com.rc.utils.AvatarUtil;
import com.rc.utils.IconUtil;
import com.rc.utils.ImageCache;
import com.rc.utils.TimeUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Created by song on 17-6-2.
 */
public class MessageAdapter extends BaseAdapter<BaseMessageViewHolder>
{
    private List<MessageItem> messageItems;
    private AttachmentIconHelper attachmentIconHelper = new AttachmentIconHelper();
    private CurrentUserService currentUserService = Launcher.currentUserService;
    private CurrentUser currentUser;
    private ImageCache imageCache;

    public MessageAdapter(List<MessageItem> messageItems)
    {
        this.messageItems = messageItems;
        currentUser = currentUserService.findAll().get(0);
        imageCache = new ImageCache();
    }

    @Override
    public int getItemViewType(int position)
    {
        return messageItems.get(position).getMessageType();
    }

    @Override
    public BaseMessageViewHolder onCreateViewHolder(int viewType)
    {
        switch (viewType)
        {
            case MessageItem.SYSTEM_MESSAGE:
            {
                return new MessageSystemMessageViewHolder();
            }
            case MessageItem.RIGHT_TEXT:
            {
                return new MessageRightTextViewHolder();
            }
            case MessageItem.LEFT_TEXT:
            {
                return new MessageLeftTextViewHolder();
            }
            case MessageItem.RIGHT_IMAGE:
            {
                return new MessageRightImageViewHolder();
            }
            case MessageItem.LEFT_IMAGE:
            {
                return new MessageLeftImageViewHolder();
            }
            case MessageItem.RIGHT_ATTACHMENT:
            {
                return new MessageRightAttachmentViewHolder();
            }
            case MessageItem.LEFT_ATTACHMENT:
            {
                return new MessageLeftAttachmentViewHolder();
            }
        }

        return null;
    }

    @Override
    public void onBindViewHolder(BaseMessageViewHolder viewHolder, int position)
    {
        if (viewHolder == null)
        {
            return;
        }

        final MessageItem item = messageItems.get(position);
        MessageItem preItem = position == 0 ? null : messageItems.get(position - 1);

        processTimeAndAvatar(item, preItem, viewHolder);

        if (viewHolder instanceof MessageSystemMessageViewHolder)
        {
            processSystemMessage(viewHolder, item);
        }
        else if (viewHolder instanceof MessageRightTextViewHolder)
        {
            processRightTextMessage(viewHolder, item);
        }
        else if (viewHolder instanceof MessageLeftTextViewHolder)
        {
            processLeftTextMessage(viewHolder, item);
        }
        else if (viewHolder instanceof MessageRightImageViewHolder)
        {
            processRightImageMessage(viewHolder, item);
        }
        else if (viewHolder instanceof MessageLeftImageViewHolder)
        {
            processLeftImageMessage(viewHolder, item);
        }
        else if (viewHolder instanceof MessageRightAttachmentViewHolder)
        {
            processRightAttachmentMessage(viewHolder, item);
        }
        else if (viewHolder instanceof MessageLeftAttachmentViewHolder)
        {
            processLeftAttachmentMessage(viewHolder, item);
        }
    }

    private void processSystemMessage(ViewHolder viewHolder, MessageItem item)
    {
        MessageSystemMessageViewHolder holder = (MessageSystemMessageViewHolder) viewHolder;
        holder.text.setText(item.getMessageContent());
    }

    private void processLeftAttachmentMessage(ViewHolder viewHolder, MessageItem item)
    {
        MessageLeftAttachmentViewHolder holder = (MessageLeftAttachmentViewHolder) viewHolder;
        holder.attachmentTitle.setText(item.getMessageContent());
        ImageIcon attachmentTypeIcon = attachmentIconHelper.getImageIcon(item.getFileAttachment().getTitle());
        holder.attachmentIcon.setIcon(attachmentTypeIcon);
        holder.sender.setText(item.getSenderUsername());
    }

    private void processRightAttachmentMessage(ViewHolder viewHolder, MessageItem item)
    {
        MessageRightAttachmentViewHolder holder = (MessageRightAttachmentViewHolder) viewHolder;
        holder.attachmentTitle.setText(item.getMessageContent());
        ImageIcon attachmentTypeIcon = attachmentIconHelper.getImageIcon(item.getFileAttachment().getTitle());
        holder.attachmentIcon.setIcon(attachmentTypeIcon);
    }

    /**
     * 对方发送的图片
     *
     * @param viewHolder
     * @param item
     */
    private void processLeftImageMessage(ViewHolder viewHolder, MessageItem item)
    {
        MessageLeftImageViewHolder holder = (MessageLeftImageViewHolder) viewHolder;
        holder.sender.setText(item.getSenderUsername());

        processImage(item, holder.image, holder);

        /*ImageIcon imageIcon = new ImageIcon(getClass().getResource(item.getImageAttachment().getImageUrl()));
        preferredImageSize(imageIcon);
        holder.image.setIcon(imageIcon);*/
    }

    /**
     * 我发送的图片
     *
     * @param viewHolder
     * @param item
     */
    private void processRightImageMessage(ViewHolder viewHolder, MessageItem item)
    {
        MessageRightImageViewHolder holder = (MessageRightImageViewHolder) viewHolder;

        processImage(item, holder.image, holder);
    }

    private void processImage(MessageItem item, JLabel imageLabel, ViewHolder holder)
    {
        String imageUrl = item.getImageAttachment().getImageUrl();
        String url;
        if (imageUrl.startsWith("/file-upload"))
        {
            url = Launcher.HOSTNAME + imageUrl + ".jpg?rc_uid=" + currentUser.getUserId() + "&rc_token=" + currentUser.getAuthToken();
        }
        else
        {
            url = "file://" + imageUrl;
        }


        ImageIcon imageIcon = imageCache.tryGetThumbCache(item.getImageAttachment().getId());

        if (imageIcon == null)
        {
            imageLabel.setIcon(IconUtil.getIcon(this, "/image/image_loading.gif"));

            imageCache.requestThumbAsynchronously(item.getImageAttachment().getId(), url, new ImageCache.CacheRequestListener()
            {
                @Override
                public void onSuccess(ImageIcon icon)
                {
                    preferredImageSize(icon);
                    imageLabel.setIcon(icon);
                    holder.revalidate();
                    holder.repaint();
                }

                @Override
                public void onFailed(String why)
                {

                }
            });
        }
        else
        {
            preferredImageSize(imageIcon);
            imageLabel.setIcon(imageIcon);
        }
    }

    /**
     * 根据图片尺寸大小调整图片显示的大小
     *
     * @param imageIcon
     * @return
     */
    public ImageIcon preferredImageSize(ImageIcon imageIcon)
    {
        int width = imageIcon.getIconWidth();
        int height = imageIcon.getIconHeight();
        float scale = width * 1.0F / height;

        // 限制图片显示大小
        int maxImageWidth = (int) (MainFrame.getContext().currentWindowWidth * 0.2);
        if (width > maxImageWidth)
        {
            width = maxImageWidth;
            height = (int) (width / scale);
        }
        imageIcon.setImage(imageIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));

        return imageIcon;
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
        holder.sender.setText("Song");
    }

    /**
     * 处理消息发送时间 以及 消息发送者头像
     *
     * @param item
     * @param preItem
     * @param holder
     */
    private void processTimeAndAvatar(MessageItem item, MessageItem preItem, BaseMessageViewHolder holder)
    {
        // 如果当前消息的时间与上条消息时间相差大于1分钟，则显示当前消息的时间
        if (preItem != null)
        {
            if (TimeUtil.inTheSameMinute(item.getTimestamp(), preItem.getTimestamp()))
            {
                holder.time.setVisible(false);
            }
            else
            {
                holder.time.setVisible(true);
                holder.time.setText(TimeUtil.diff(item.getTimestamp(), true));
            }
        }
        else
        {
            holder.time.setVisible(true);
            holder.time.setText(TimeUtil.diff(item.getTimestamp(), true));
        }

        if (holder.avatar != null)
        {
            ImageIcon icon = new ImageIcon();
            // 群组头像
            icon.setImage(AvatarUtil.createOrLoadUserAvatar(item.getSenderUsername()));
            holder.avatar.setIcon(icon);

            bindAvatarAction(holder.avatar);
        }


        /*
        {
            holder.avatar.setImageBitmap(AvatarUtil.createOrLoadUserAvatar(this.activity, item.getSenderUsername()));
        }*/
    }


    private void bindAvatarAction(JLabel avatarLabel)
    {
        avatarLabel.addMouseListener(new AbstractMouseListener()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                UserInfoPopup popup = new UserInfoPopup();
                popup.show(e.getComponent(), e.getX(), e.getY());

                super.mouseClicked(e);
            }
        });
    }

    @Override
    public int getCount()
    {
        return messageItems.size();
    }
}
