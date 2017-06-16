package com.rc.adapter.message;

import com.rc.adapter.BaseAdapter;
import com.rc.adapter.ViewHolder;
import com.rc.app.Launcher;
import com.rc.db.model.CurrentUser;
import com.rc.db.model.Message;
import com.rc.db.service.CurrentUserService;
import com.rc.db.service.MessageService;
import com.rc.entity.MessageItem;
import com.rc.forms.ChatPanel;
import com.rc.forms.MainFrame;
import com.rc.forms.UserInfoPopup;
import com.rc.helper.AttachmentIconHelper;
import com.rc.listener.AbstractMouseListener;
import com.rc.utils.AvatarUtil;
import com.rc.utils.IconUtil;
import com.rc.utils.ImageCache;
import com.rc.utils.TimeUtil;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
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
    private MessageService messageService = Launcher.messageService;
    private Logger logger = Logger.getLogger(this.getClass());


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

        setAttachmentClickListener(holder, item);
    }

    private void processRightAttachmentMessage(ViewHolder viewHolder, MessageItem item)
    {
        MessageRightAttachmentViewHolder holder = (MessageRightAttachmentViewHolder) viewHolder;
        holder.attachmentTitle.setText(item.getMessageContent());
        ImageIcon attachmentTypeIcon = attachmentIconHelper.getImageIcon(item.getFileAttachment().getTitle());
        holder.attachmentIcon.setIcon(attachmentTypeIcon);

        if (item.getProgress() != 0 && item.getProgress() != 100)
        {
            Message msg = messageService.findById(item.getId());
            if (msg != null)
            {
                item.setProgress(msg.getProgress());

                holder.progressBar.setVisible(true);
                holder.progressBar.setValue(item.getProgress());

                if (item.getProgress() == 100)
                {
                    holder.progressBar.setVisible(false);
                }
                else
                {
                    if (!ChatPanel.getContext().uploadingOrDownloadingFiles.contains(item.getFileAttachment().getId()))
                    {
                        item.setNeedToResend(true);
                    }
                }
            }
        }
        else
        {
            holder.progressBar.setVisible(false);
        }


        // 判断是否显示重发按钮
        if (item.isNeedToResend())
        {
            holder.resend.setVisible(true);
        }
        else
        {
            holder.resend.setVisible(false);
        }

        holder.resend.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                //System.out.println(item.getMessageContent() + "正在重发");
                ChatPanel.getContext().resendFileMessage(item.getId(), "file");
                super.mouseClicked(e);
            }
        });

        setAttachmentClickListener(holder, item);
    }

    private void setAttachmentClickListener(MessageAttachmentViewHolder viewHolder, MessageItem item)
    {
        MouseAdapter listener = new MouseAdapter()
        {
            @Override
            public void mouseReleased(MouseEvent e)
            {
                ChatPanel.getContext().downloadOrOpenFile(item.getId());
            }
        };

        viewHolder.attachmentPanel.addMouseListener(listener);
        viewHolder.attachmentTitle.addMouseListener(listener);
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

        if (item.getProgress() != 0 && item.getProgress() != 100)
        {
            Message msg = messageService.findById(item.getId());
            if (msg != null)
            {
                item.setProgress(msg.getProgress());

                if (item.getProgress() == 100)
                {
                    holder.sendingProgress.setVisible(false);
                }
                else
                {
                    if (!ChatPanel.getContext().uploadingOrDownloadingFiles.contains(item.getImageAttachment().getId()))
                    {
                        item.setNeedToResend(true);
                    }
                }
            }
        }
        else
        {
            if (item.getUpdatedAt() < 1)
            {
                holder.sendingProgress.setVisible(true);
            }
            else
            {
                holder.sendingProgress.setVisible(false);
            }
        }


        // 判断是否显示重发按钮
        if (item.isNeedToResend())
        {
            holder.resend.setVisible(true);
        }
        else
        {
            holder.resend.setVisible(false);
        }

        holder.resend.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                //System.out.println(item.getMessageContent() + "正在重发");
                ChatPanel.getContext().resendFileMessage(item.getId(), "image");
                super.mouseClicked(e);
            }
        });
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

            imageCache.requestThumbAsynchronously(item.getImageAttachment().getId(), url, new ImageCache.ImageCacheRequestListener()
            {
                @Override
                public void onSuccess(ImageIcon icon, String path)
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

        // 当点击图片时，使用默认程序打开图片
        imageLabel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                imageCache.requestOriginalAsynchronously(item.getImageAttachment().getId(), item.getImageAttachment().getImageUrl(), new ImageCache.ImageCacheRequestListener()
                {
                    @Override
                    public void onSuccess(ImageIcon icon, String path)
                    {
                        try
                        {
                            Desktop.getDesktop().open(new File(path));
                        }
                        catch (IOException e1)
                        {
                            JOptionPane.showMessageDialog(null, "图像不存在", "图像不存在", JOptionPane.ERROR_MESSAGE);
                            e1.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed(String why)
                    {

                    }
                });
                super.mouseClicked(e);
            }
        });
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
        boolean needToUpdateResendStatus = !item.isNeedToResend() && item.getUpdatedAt() < 1 && System.currentTimeMillis() - item.getTimestamp() > 10 * 1000;

        if (item.isNeedToResend() || needToUpdateResendStatus)
        {
            if (needToUpdateResendStatus)
            {
                messageService.updateNeedToResend(item.getId(), true);
            }

            logger.debug("显示重发按钮");

            holder.sendingProgress.setVisible(false);
            holder.resend.setVisible(true);
        }
        else
        {
            holder.resend.setVisible(false);
            // 如果是刚发送的消息，显示正在发送进度条
            if (item.getUpdatedAt() < 1)
            {
                holder.sendingProgress.setVisible(true);
            }
            else
            {
                holder.sendingProgress.setVisible(false);
            }
        }

        holder.resend.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                //System.out.println(item.getMessageContent() + "正在重发");
                ChatPanel.getContext().sendTextMessage(item.getId(), null);
                super.mouseClicked(e);
            }
        });
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
        holder.sender.setText(item.getSenderUsername());
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
            Image image = AvatarUtil.createOrLoadUserAvatar(item.getSenderUsername()).getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            icon.setImage(image);
            holder.avatar.setIcon(icon);

            if (item.getMessageType() == MessageItem.LEFT_ATTACHMENT
                    || item.getMessageType() == MessageItem.LEFT_IMAGE
                    || item.getMessageType() == MessageItem.LEFT_TEXT)
            {
                bindAvatarAction(holder.avatar, item.getSenderUsername());
            }
        }


        /*
        {
            holder.avatar.setImageBitmap(AvatarUtil.createOrLoadUserAvatar(this.activity, item.getSenderUsername()));
        }*/
    }


    private void bindAvatarAction(JLabel avatarLabel, String username)
    {
        avatarLabel.addMouseListener(new AbstractMouseListener()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                UserInfoPopup popup = new UserInfoPopup(username);
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
