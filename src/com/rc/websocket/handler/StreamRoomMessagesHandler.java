package com.rc.websocket.handler;

import com.rc.app.Launcher;
import com.rc.db.model.*;
import com.rc.db.service.FileAttachmentService;
import com.rc.db.service.ImageAttachmentService;
import com.rc.db.service.MessageService;
import com.rc.db.service.RoomService;
import com.rc.panels.ChatPanel;
import com.rc.frames.MainFrame;
import com.rc.panels.RoomsPanel;
import com.rc.utils.AvatarUtil;
import com.rc.utils.MacNotificationUtil;
import com.rc.utils.OSUtil;
import com.rc.utils.ShellUtil;
import com.sun.jna.platform.FileUtils;
import com.vdurmont.emoji.EmojiParser;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Created by song on 27/03/2017.
 */

public class StreamRoomMessagesHandler implements CollectionHandler
{
    private MessageService messageService = Launcher.messageService;
    private RoomService roomService = Launcher.roomService;
    private ImageAttachmentService imageAttachmentService = Launcher.imageAttachmentService;
    private FileAttachmentService fileAttachmentService = Launcher.fileAttachmentService;
    private CurrentUser currentUser = Launcher.currentUserService.findAll().get(0);
    private Logger logger;


    public StreamRoomMessagesHandler()
    {
        logger = Logger.getLogger(this.getClass());
    }

    @Override
    public void handle(String eventName, Object data)
    {
        JSONArray jsonArray = (JSONArray) data;
        try
        {
            JSONObject obj = jsonArray.getJSONObject(0);
            String type = "";
            if (obj.has("t"))
            {
                type = obj.getString("t");
            }

            Message message = new Message();
            message.setId(obj.getString("_id"));
            message.setRoomId(obj.getString("rid"));
            // 处理消息内容
            if (obj.getString("msg").startsWith("[ ]("))
            {
                message.setMessageContent(obj.getString("msg").replaceAll("\\[ \\]\\(.*\\)\\s*", ""));
            }
            else
            {
                message.setMessageContent(obj.getString("msg"));
            }

            if (type.equals("au") || type.equals("uj"))
            {
                message.setMessageContent(obj.getString("msg") + " 加入群聊");
                message.setSystemMessage(true);
            }
            else if (type.equals("r"))
            {
                String creator = obj.getJSONObject("u").getString("username");
                message.setMessageContent(creator + " 更改群名称为：" + obj.getString("msg"));
                message.setSystemMessage(true);
            }
            else if (type.equals("ru"))
            {
                message.setMessageContent(obj.getString("msg") + " 被移出群聊");
                message.setSystemMessage(true);
            }
            else if (type.equals("ul"))
            {
                message.setMessageContent(obj.getString("msg") + " 退出群聊");
                message.setSystemMessage(true);
            }
            else if (type.equals("user-muted"))
            {
                message.setMessageContent(obj.getString("msg") + " 被禁言");
                message.setSystemMessage(true);
            }
            else if (type.equals("user-unmuted"))
            {
                message.setMessageContent(obj.getString("msg") + " 被取消禁言");
                message.setSystemMessage(true);
            }
            else if (type.equals("subscription-role-added"))
            {
                if (obj.getString("role").equals("owner"))
                {
                    message.setMessageContent(obj.getString("msg") + " 被赋予了 所有者 角色");
                }
                else if (obj.getString("role").equals("moderator"))
                {
                    message.setMessageContent(obj.getString("msg") + " 被赋予了 主持 角色");
                }

                message.setSystemMessage(true);
            }
            else if (type.equals("subscription-role-removed"))
            {
                if (obj.getString("role").equals("owner"))
                {
                    message.setMessageContent(obj.getString("msg") + " 被移除了 所有者 角色");
                }
                else if (obj.getString("role").equals("moderator"))
                {
                    message.setMessageContent(obj.getString("msg") + " 被移除了 主持 角色");
                }
                message.setSystemMessage(true);
            }

            //message.setTimestamp(Long.parseLong(obj.getJSONObject("ts").getString("$date")));
            message.setSenderId(obj.getJSONObject("u").getString("_id"));
            message.setSenderUsername(obj.getJSONObject("u").getString("username"));
            message.setUpdatedAt(obj.getJSONObject("_updatedAt").getLong("$date"));
            message.setNeedToResend(false);

            if (obj.has("groupable"))
            {
                message.setGroupable(obj.getBoolean("groupable"));
            }


            // 是否是我刚刚上传的文件
            boolean myUploadFile = false;

            // 当前是否打开了对应的聊天窗口
            boolean inChatRoom = message.getRoomId().equals(ChatPanel.CHAT_ROOM_OPEN_ID);

            /*boolean inChatRoom = MainFrameActivity.CHAT_ROOM_OPEN_ID != null
                    && MainFrameActivity.CHAT_ROOM_OPEN_ID.equals(message.getRoomId());*/

            // 附件消息
            if (obj.has("attachments") && !obj.getString("msg").startsWith("[ ]("))
            {
                if (obj.has("file") && obj.get("attachments") instanceof JSONArray)
                {
                    JSONArray attachments = obj.getJSONArray("attachments");
                    for (int j = 0; j < attachments.length(); j++)
                    {
                        JSONObject attachment = attachments.getJSONObject(j);
                        if (attachment.has("image_url"))
                        {
                            ImageAttachment imageAttachment = new ImageAttachment();
                            imageAttachment.setId(obj.getJSONObject("file").getString("_id"));
                            imageAttachment.setTitle(attachment.getString("title"));
                            imageAttachment.setDescription(attachment.getString("description"));
                            imageAttachment.setImageUrl(attachment.getString("image_url"));
                            imageAttachment.setImagesize(attachment.getLong("image_size"));
                            imageAttachment.setWidth(attachment.getJSONObject("image_dimensions").getInt("width"));
                            imageAttachment.setHeight(attachment.getJSONObject("image_dimensions").getInt("height"));

                            //message.getImageAttachments().add(imageAttachment);
                            message.setImageAttachmentId(imageAttachment.getId());
                            message.setMessageContent("[图片]");

                            // 查找是否有临时以FildId作为MessageId的消息
                            Message tempMsg = messageService.findById(imageAttachment.getId());

                            if (tempMsg != null)
                            {
                                myUploadFile = true;
                                ImageAttachment ia = imageAttachmentService.findById(tempMsg.getImageAttachmentId());
                                imageAttachment.setTitle(ia.getTitle());
                                //imageAttachment.setImageUrl(ia.getImageUrl());
                                imageAttachment.setDescription(ia.getDescription());

                                message.setNeedToResend(false);
                                message.setTimestamp(tempMsg.getTimestamp());


                                // 删除临时文件消息
                                messageService.delete(imageAttachment.getId());
                            }

                            imageAttachmentService.insertOrUpdate(imageAttachment);
                        }
                        else
                        {
                            FileAttachment fileAttachment = new FileAttachment();
                            fileAttachment.setId(obj.getJSONObject("file").getString("_id"));
                            fileAttachment.setTitle(attachment.getString("title").substring(15));
                            fileAttachment.setDescription(attachment.getString("description"));
                            fileAttachment.setLink(attachment.getString("title_link"));
                            message.setFileAttachmentId(fileAttachment.getId());
                            message.setMessageContent(fileAttachment.getTitle());

                            // 查找是否有临时以FildId作为MessageId的消息
                            Message tempMsg = messageService.findById(fileAttachment.getId());

                            if (tempMsg != null)
                            {
                                myUploadFile = true;
                                FileAttachment fa = fileAttachmentService.findById(tempMsg.getFileAttachmentId());
                                fileAttachment.setTitle(fa.getTitle());
                                fileAttachment.setLink(fa.getLink());
                                fileAttachment.setDescription(fa.getDescription());

                                message.setNeedToResend(false);
                                message.setTimestamp(tempMsg.getTimestamp());


                                // 删除临时文件消息
                                messageService.delete(fileAttachment.getId());
                            }

                            fileAttachmentService.insertOrUpdate(fileAttachment);
                        }
                    }
                }
            }

            // 如果是我上传的文件，则消息的发送时间以本地发送时间为准，其他消息以服务器的时间为准
            if (!myUploadFile)
            {
                message.setTimestamp(obj.getJSONObject("ts").getLong("$date"));
            }

            messageService.insertOrUpdate(message);

            // 更新Room相关信息
            Room room = roomService.findById(message.getRoomId());
            room.setMsgSum(room.getMsgSum() + 1);

            // 如果没有打开房间，则需要更新未读消息数，如果已经在房间中了，则无需更新未读消息数
            if (inChatRoom || myUploadFile)
            {
                room.setUnreadCount(0);
                room.setTotalReadCount(room.getMsgSum());
            }
            else
            {
                room.setUnreadCount(room.getUnreadCount() + 1);
            }

            room.setLastMessage(message.getMessageContent());
            room.setLastChatAt(message.getTimestamp());
            roomService.update(room);

            // 通知UI更新
            /*Map<String, String> param = new HashMap();
            param.put("roomId", message.getRoomId());
            param.put("message", message.getMessageContent());
            param.put("size", message.getTimestamp() + "");
            param.put("unreadCount", room.getUnreadCount() + "");*/

            if (myUploadFile)
            {
                // 继续上传后面的文件
                ChatPanel.getContext().dequeueAndUpload();
            }

            // 更新主程序窗口状态
            notifyMainFrame(message, myUploadFile);


        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 通知程序主窗口进行相应的提示，如发送声音、图标闪动等
     */
    private void notifyMainFrame(Message message, boolean myUploadFile)
    {
        // 更新房间列表
        //RoomsPanel.getContext().notifyDataSetChanged(true);
        RoomsPanel.getContext().updateRoomsList(message);
        boolean inChatRoom = message.getRoomId().equals(ChatPanel.CHAT_ROOM_OPEN_ID);


        MainFrame context = MainFrame.getContext();
        // 如果主窗口没有显示
        if (!context.isVisible())
        {
            // 发送通知
            if (!message.getSenderId().equals(currentUser.getUserId()))
            {
                // 苹果系统
                if (OSUtil.getOsType() == OSUtil.Mac_OS)
                {
                    sendNotification(message, OSUtil.Mac_OS);
                }
                else if (OSUtil.getOsType() == OSUtil.Linux)
                {
                    sendNotification(message, OSUtil.Linux);
                }
                else
                {
                    if (!context.isTrayFlashing())
                    {
                        context.setTrayFlashing();
                    }

                    context.playMessageSound();
                }
            }
        }
        // 主窗口已显示
        else
        {
            // 窗体打开，但没有被激活
            if (!context.isActive())
            {
                // 发送通知
                if (!message.getSenderId().equals(currentUser.getUserId()))
                {
                    if (OSUtil.getOsType() == OSUtil.Mac_OS)
                    {
                        sendNotification(message, OSUtil.Mac_OS);
                    }
                    else if (OSUtil.getOsType() == OSUtil.Linux)
                    {
                        sendNotification(message, OSUtil.Linux);
                    }
                    else
                    {
                        // 任务栏图标高亮
                        context.playMessageSound();
                        context.setVisible(true);
                        context.toFront();
                    }
                }
            }
        }

        // 如果是当前打开的房间，更新消息列表
        if (inChatRoom)
        {
            // 如果是刚刚自己上传的文件，提示UI不要再把这条消息加入到消息列表中，防止消息重复出现
            if (!myUploadFile)
            {
                ChatPanel.getContext().addOrUpdateMessageItem();
            }
        }


        // 更新总消息数，MAC系统中dock中的Badge
        if (OSUtil.getOsType() == OSUtil.Mac_OS)
        {
            ChatPanel.getContext().updateTotalUnreadCount();
        }
    }

    /**
     * 发送消息气泡通知
     *
     * @param message
     * @param osType
     */
    private void sendNotification(Message message, int osType)
    {
        // 发送通知
        if (!message.getSenderId().equals(currentUser.getUserId()))
        {
            String t = EmojiParser.parseToUnicode(message.getMessageContent());

            if (osType == OSUtil.Mac_OS)
            {
                MacNotificationUtil.sendNotification(message.getSenderUsername(), "", t, 1);
            }
            else if (osType == OSUtil.Linux)
            {
                try
                {
                    File iconFile;
                    String iconPath = AvatarUtil.CUSTOM_AVATAR_CACHE_ROOT + "/" + message.getSenderUsername() + ".png";
                    iconFile = new File(iconPath);
                    if (!iconFile.exists())
                    {
                        iconPath = AvatarUtil.AVATAR_CACHE_ROOT + "/" + message.getSenderUsername() + ".png";
                        iconFile = new File(iconPath);
                        if (!iconFile.exists())
                        {
                            iconPath = ShellUtil.ICON_PATH;
                        }
                    }

                    ShellUtil.executeShell("notify-send " + message.getSenderUsername() + " \"" + t + "\" -i " + iconPath);
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
