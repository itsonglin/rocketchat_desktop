package com.rc.websocket.handler;

import com.rc.app.Launcher;
import com.rc.db.model.FileAttachment;
import com.rc.db.model.ImageAttachment;
import com.rc.db.model.Message;
import com.rc.db.model.Room;
import com.rc.db.service.FileAttachmentService;
import com.rc.db.service.ImageAttachmentService;
import com.rc.db.service.MessageService;
import com.rc.db.service.RoomService;
import com.rc.forms.ChatPanel;
import com.rc.forms.MainFrame;
import com.rc.forms.RoomsPanel;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by song on 27/03/2017.
 */

public class StreamRoomMessagesHandler implements CollectionHandler
{
    private MessageService messageService = Launcher.messageService;
    private RoomService roomService = Launcher.roomService;
    private ImageAttachmentService imageAttachmentService = Launcher.imageAttachmentService;
    private FileAttachmentService fileAttachmentService = Launcher.fileAttachmentService;
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
            // TODO
            boolean inChatRoom = false;
            /*boolean inChatRoom = MainFrameActivity.CHAT_ROOM_OPEN_ID != null
                    && MainFrameActivity.CHAT_ROOM_OPEN_ID.equals(message.getRoomId());*/

            // 附件消息
            if (obj.has("attachments") && !obj.getString("msg").startsWith("[ ]("))
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
                        //Realm realm = Realm.getDefaultInstance();
                        //Message tempMsg = messageService.findById(realm, imageAttachment.getId());
                        Message tempMsg = messageService.findById(imageAttachment.getId());

                        if (tempMsg != null)
                        {
                           /* myUploadFile = true;
                            message.getImageAttachments().get(0).setTitle(tempMsg.getImageAttachments().get(0).getTitle());
                            message.getImageAttachments().get(0).setImageUrl(tempMsg.getImageAttachments().get(0).getImageUrl());
                            message.getImageAttachments().get(0).setDescription(tempMsg.getImageAttachments().get(0).getDescription());
                            message.setNeedToResend(false);
                            message.setTimestamp(tempMsg.getTimestamp());*/

                            myUploadFile = true;
                            ImageAttachment ia = imageAttachmentService.findById(tempMsg.getImageAttachmentId());
                            imageAttachment.setTitle(ia.getTitle());
                            imageAttachment.setImageUrl(ia.getImageUrl());
                            imageAttachment.setDescription(ia.getDescription());
                            message.setNeedToResend(false);
                            message.setTimestamp(tempMsg.getTimestamp());


                            // 删除临时文件消息
                            //messageService.delete(Realm.getDefaultInstance(), imageAttachment.getId());
                            messageService.delete(imageAttachment.getId());
                        }

                        imageAttachmentService.insertOrUpdate(imageAttachment);
                    }
                    else
                    {
                        FileAttachment fileAttachment = new FileAttachment();
                        fileAttachment.setId(obj.getJSONObject("file").getString("_id"));
                        fileAttachment.setTitle(attachment.getString("title"));
                        fileAttachment.setDescription(attachment.getString("description"));
                        fileAttachment.setLink(attachment.getString("title_link"));
                        //message.getFileAttachments().add(fileAttachment);
                        message.setFileAttachmentId(fileAttachment.getId());
                        message.setMessageContent(fileAttachment.getTitle().replace("File Uploaded:", ""));

                        // 查找是否有临时以FildId作为MessageId的消息
                        //Realm realm = Realm.getDefaultInstance();
                        //Message tempMsg = messageService.findById(realm, fileAttachment.getId());
                        Message tempMsg = messageService.findById(fileAttachment.getId());

                        if (tempMsg != null)
                        {
                            /*myUploadFile = true;
                            message.getFileAttachments().get(0).setTitle(tempMsg.getFileAttachments().get(0).getTitle());
                            message.getFileAttachments().get(0).setLink(tempMsg.getFileAttachments().get(0).getLink());
                            message.getFileAttachments().get(0).setDescription(tempMsg.getFileAttachments().get(0).getDescription());
                            message.setNeedToResend(false);
                            message.setTimestamp(tempMsg.getTimestamp());*/

                            myUploadFile = true;
                            FileAttachment fa = fileAttachmentService.findById(tempMsg.getFileAttachmentId());
                            fileAttachment.setTitle(fa.getTitle());
                            fileAttachment.setLink(fa.getLink());
                            fileAttachment.setDescription(fa.getDescription());
                            message.setNeedToResend(false);
                            message.setTimestamp(tempMsg.getTimestamp());


                            // 删除临时文件消息
                            //messageService.delete(Realm.getDefaultInstance(), fileAttachment.getId());
                            messageService.delete(fileAttachment.getId());
                        }

                        fileAttachmentService.insertOrUpdate(fileAttachment);

                        // realm.close();
                    }
                }
            }

            // 如果是我上传的文件，则消息的发送时间以本地发送时间为准，其他消息以服务器的时间为准
            if (!myUploadFile)
            {
                message.setTimestamp(obj.getJSONObject("ts").getLong("$date"));
            }

            //messageService.insertOrUpdate(Realm.getDefaultInstance(), message);
            messageService.insertOrUpdate(message);

            // 更新Room相关信息
            //Realm realm = Realm.getDefaultInstance();
            //Room room = roomService.findById(realm, message.getRoomId());
            Room room = roomService.findById(message.getRoomId());
            //roomService.updateMessageSum(Realm.getDefaultInstance(), room, room.getMsgSum() + 1);
            room.setMsgSum(room.getMsgSum() + 1);

            if (!myUploadFile && !inChatRoom)
            {
                //roomService.updateUnreadCount(Realm.getDefaultInstance(), room.getRoomId(), room.getUnreadCount() + 1);
                room.setUnreadCount(room.getUnreadCount() + 1);
            }

           // roomService.updateLastMessage(Realm.getDefaultInstance(), room, message.getMessageContent(), message.getTimestamp());
            room.setLastMessage(message.getMessageContent());
            room.setLastChatAt(message.getTimestamp());
            roomService.update(room);
            //realm.close();

            // 通知UI更新
            Map<String, String> param = new HashMap();
            param.put("roomId", message.getRoomId());
            param.put("message", message.getMessageContent());
            param.put("time", message.getTimestamp() + "");
            param.put("unreadCount", room.getUnreadCount() + "");

            // 如果是刚刚自己上传的文件，提示UI不要再把这条消息加入到消息列表中，防止消息重复出现
            if (myUploadFile)
            {
                param.put("doNotAddToMessageList", "true");

                // 继续上传后面的文件
                /*((WebSocketService) context).sendBroadcast(MainFrameActivity.WEBSOCKET_TO_ACTIVITY_ACTION,
                        WebSocketService.DEQUEUE_AND_UPLOAD, param);*/
            }







            MainFrame context = MainFrame.getContext();
            // 更新房间列表
            RoomsPanel.getContext().notifyDataSetChanged();

            context.playMessageSound();

            // 如果主窗口没有显示，则任务栏闪动
            if (!context.isVisible())
            {
                if (!context.isTrayFlashing())
                {
                    context.setTrayFlashing();
                }

                context.playMessageSound();
            }
            // 主窗口已显示
            else
            {
                // 窗体打开，但没有被激活，则任务栏图标高亮
                if (!context.isActive())
                {
                    System.out.println("要播放声音");
                    context.playMessageSound();
                    context.setVisible(true);
                    context.toFront();
                }

                // 如果是当前打开的房间，更新消息列表
                if (message.getRoomId().equals(ChatPanel.CHAT_ROOM_OPEN_ID))
                {
                    // 如果是刚刚自己上传的文件，提示UI不要再把这条消息加入到消息列表中，防止消息重复出现
                    if (!myUploadFile)
                    {
                        ChatPanel.getContext().addOrUpdateMessageItem();
                    }
                }
            }


            /*((WebSocketService) context).sendBroadcast(MainFrameActivity.WEBSOCKET_TO_ACTIVITY_ACTION,
                    WebSocketService.EVENT_NEW_ROOM_MESSAGE_RECEIVED, param);*/


        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
}
