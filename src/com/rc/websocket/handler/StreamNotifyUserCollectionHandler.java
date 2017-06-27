package com.rc.websocket.handler;


import com.rc.app.Launcher;
import com.rc.db.model.Room;
import com.rc.db.service.ContactsUserService;
import com.rc.db.service.CurrentUserService;
import com.rc.db.service.MessageService;
import com.rc.db.service.RoomService;
import com.rc.panels.ChatPanel;
import com.rc.frames.CreateGroupDialog;
import com.rc.panels.RoomsPanel;
import com.rc.websocket.SubscriptionHelper;
import com.rc.websocket.WebSocketClient;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by song on 10/03/2017.
 */

public class StreamNotifyUserCollectionHandler implements CollectionHandler
{
    private final WebSocketClient webSocketService;
    private RoomService roomService = Launcher.roomService;
    //private LastUpdateService lastUpdateService = new LastUpdateService();
    private SubscriptionHelper subscriptionHelper;
    private CurrentUserService currentUserService = Launcher.currentUserService;
    private MessageService messageService = Launcher.messageService;
    private ContactsUserService contactsUserService = Launcher.contactsUserService;

    private Logger logger;

    public StreamNotifyUserCollectionHandler(WebSocketClient webSocketService)
    {
        logger = Logger.getLogger(this.getClass());

        this.webSocketService = webSocketService;
    }

    @Override
    public void handle(String eventName, Object data)
    {

        try
        {
            if (eventName.equals("notification"))
            {
                logger.debug("stream-notify-user  ==== notification");
                processNotification(data);
            }
            else if (eventName.equals("message"))
            {
                logger.debug("stream-notify-user  ==== message");
                processRoomMessage(data);
            }
            else if (eventName.equals("webrtc"))
            {
                logger.debug("stream-notify-user  ==== webrtc");
            }
            else if (eventName.equals("otr"))
            {
                logger.debug("stream-notify-user  ==== otr");
            }
            else if (eventName.equals("rooms-changed"))
            {
                logger.debug("stream-notify-user  ==== rooms-changed");
                processRoomChanged(data);
            }
            else if (eventName.equals("subscriptions-changed")) // 创建群、发起直接聊天、被邀请入群
            {
                logger.debug("stream-notify-user  ==== subscriptions-changed");
                processSubscriptionsChange(data);
            }
        }
        catch (JSONException ee)
        {
            ee.printStackTrace();
        }
    }

    private void processRoomMessage(Object data) throws JSONException
    {
        JSONArray jsonArray = (JSONArray) data;
        JSONObject obj = jsonArray.getJSONObject(0);
        String roomId = obj.getString("rid");
        String message = obj.getString("msg");

        if (message.equals("This room is blocked"))
        {
            Map<String, String> param = new HashMap<>();
            param.put("roomId", roomId);
            //this.webSocketService.sendBroadcast(MainFrameActivity.WEBSOCKET_TO_ACTIVITY_ACTION, WebSocketService.EVENT_ROOM_BLOCK, param);
        }
    }

    private void processNotification(Object data) throws JSONException
    {
        JSONArray jsonArray = (JSONArray) data;
        JSONObject obj = jsonArray.getJSONObject(0);
        String text = obj.getString("text");
        JSONObject payload = obj.getJSONObject("payload");
        String sender = payload.getJSONObject("sender").getString("username");
        String roomName = sender;
        String content = text;
        if (payload.has("name"))
        {
            roomName = payload.getString("name");
            content = sender + ": " + text;
        }

        String roomId = payload.getString("rid");
        String roomType = payload.getString("type");

        // 发通知
        logger.debug("这里要发通知 --- " + content);
        /*boolean isMainFrameActivityBackground = (!isForeground(WebSocketService.context, MainFrameActivity.class.getName()));
        boolean isChatRoomActivityBackground = (!isForeground(WebSocketService.context, ChatRoomActivity.class.getName()));
        boolean isCurrentRoom = roomId.equals(MainFrameActivity.CHAT_ROOM_OPEN_ID);
        Log.e("NOTIFY", "isMainFrameActivityBackground = " + isMainFrameActivityBackground + ", isChatRoomActivityBackground = " + isChatRoomActivityBackground + ", isCurrentRoom = " + isCurrentRoom);
        if ((isMainFrameActivityBackground && isChatRoomActivityBackground && !isCurrentRoom) || MainFrameActivity.SCREEN_OFF)
        {
            showDefaultNotification(roomId, roomType, name, content);
        }
        else if (!isCurrentRoom)
        {
            vibrator();
        }*/
    }

    private void processSubscriptionsChange(Object data) throws JSONException
    {
        JSONArray jsonArray = (JSONArray) data;
        String type = jsonArray.get(0).toString();
        JSONObject obj = jsonArray.getJSONObject(1);

        // 新房间加入
        if (type.equals("inserted"))
        {
            String t = obj.getString("t");
            // 直接聊天
            if (t.equals("d"))
            {
                Room room = new Room();
                room.setLastChatAt(0);
                room.setName(obj.getString("name"));
                room.setType(obj.getString("t"));
                room.setRoomId(obj.getString("rid"));
                // room.setCreatorId(obj.getJSONObject("u").getString("_id"));
                String username = obj.getJSONObject("u").getString("username");
                // 我主动创建
                if (SubscriptionHelper.lastCreateDirectChatUsername != null
                        && SubscriptionHelper.lastCreateDirectChatUsername.equals(room.getName()))
                {
                    room.setCreatorName(username);
                    SubscriptionHelper.lastCreateDirectChatUsername = null;
                }
                // 对方主动创建
                else
                {
                    room.setCreatorName(room.getName());
                }

                room.setUpdatedAt(String.valueOf(obj.getJSONObject("_updatedAt").getLong("$date")));

                //roomService.insertOrUpdate(Realm.getDefaultInstance(), room);
                roomService.insertOrUpdate(room);

                // 订阅相关消息
                subscription(room);

                // 如果是我创建的群聊，打打开窗口
                checkAndOpenChatRoomActivity(room);


                // 通知UI更新Rooms列表
                notifyUpdateRoomsUI(false);
            }
        }
        // 从房间移除
        else if (type.equals("removed"))
        {
            String roomId = obj.getString("rid");
            removeRoom(roomId);
            notifyUpdateRoomsUI(false);

            // 如果是IM
            if (obj.getString("t").equals("d"))
            {
                String name = obj.getString("name");
                //contactsUserService.deleteByUsername(Realm.getDefaultInstance(), name);
                contactsUserService.deleteByUsername(name);
                //this.webSocketService.sendBroadcast(MainFrameActivity.WEBSOCKET_TO_ACTIVITY_ACTION, WebSocketService.EVENT_UPDATE_CONTACTS);
                // 更新通讯录:
                //ContactsPanel.getContext().notifyDataSetChanged();
                webSocketService.updateContactsUser();
                logger.debug("删除用户：" + name);
            }
        }
    }

    private void processRoomChanged(Object data) throws JSONException
    {
        JSONArray jsonArray = (JSONArray) data;
        String type = jsonArray.get(0).toString();

        // 新的房间
        if (type.equals("inserted"))
        {
            JSONObject obj = jsonArray.getJSONObject(1);
            String t = obj.getString("t");

            // 不处理直接聊天
            if (t.equals("d"))
            {
                return;
            }

            String roomId = obj.getString("_id");
            String roomName = obj.getString("name");
            String creatorId = obj.getJSONObject("u").getString("_id");
            String creatorUsername = obj.getJSONObject("u").getString("username");
            boolean ro = obj.getBoolean("ro");

            Room room = new Room();
            room.setName(roomName);
            room.setType(t);
            room.setRoomId(roomId);
            room.setCreatorId(creatorId);
            room.setCreatorName(creatorUsername);
            room.setUpdatedAt("0");
            room.setLastChatAt(0);
            room.setReadOnly(ro);
            room.setLastChatAt(System.currentTimeMillis());

            //roomService.insertOrUpdate(Realm.getDefaultInstance(), room);
            roomService.insertOrUpdate(room);

            // 订阅相关消息
            subscription(room);

            // 如果是我创建的群聊，打打开窗口
            checkAndOpenChatRoomActivity(room);

            // 通知UI更新Rooms列表
            notifyUpdateRoomsUI(false);

            /*Realm realm = Realm.getDefaultInstance();
            if (!room.getCreatorName().equals(currentUserService.find(realm).getUsername()))
            {
                showDefaultNotification(room.getRoomId(), room.getType(), room.getName(), creatorUsername + "邀请你进入群聊：" + room.getName());
            }
            realm.close();*/

            if (!room.getCreatorName().equals(currentUserService.findAll().get(0).getUsername()))
            {
                showDefaultNotification(room.getRoomId(), room.getType(), room.getName(), creatorUsername + "邀请你进入群聊：" + room.getName());
            }
        }

        // 房间信息更新
        else if (type.equals("updated"))
        {
            JSONObject obj = jsonArray.getJSONObject(1);
            boolean needToUpdate = obj.has("muted") || obj.has("ro");

            if (needToUpdate)
            {
                //Realm realm = Realm.getDefaultInstance();
                String roomId = obj.getString("_id");
                //Room room = realm.copyFromRealm(roomService.findById(realm, roomId));
                Room room = roomService.findById(roomId);
                if (room != null)
                {
                    if (obj.has("muted"))
                    {
                        JSONArray mutedUser = obj.getJSONArray("muted");
                        if (mutedUser.length() > 0)
                        {
                            if (roomId.equals("GENERAL"))
                            {
                                handleNewUserAdded(mutedUser);
                            }
                            room.setMuted(mutedUser.join(","));
                        }
                        else
                        {
                            room.setMuted(null);
                        }
                    }
                    else
                    {
                        room.setMuted(null);
                    }

                    if (obj.has("ro"))
                    {
                        room.setReadOnly(obj.getBoolean("ro"));
                    }

                    //roomService.insertOrUpdate(Realm.getDefaultInstance(), room);
                    roomService.insertOrUpdate(room);
                    //this.webSocketService.sendBroadcast(MainFrameActivity.WEBSOCKET_TO_ACTIVITY_ACTION, WebSocketService.EVENT_MUTED);

                    if (room.getRoomId().equals(ChatPanel.CHAT_ROOM_OPEN_ID))
                    {
                        ChatPanel.getContext().checkIsMuted();
                    }

                    // 重新获取群成员
                    //ChatPanel.getContext().loadRemoteRoomMembers();
                }
                //realm.close();
            }
        }
    }

    /**
     * 新添加用户
     *
     * @param users
     * @throws JSONException
     */
    private void handleNewUserAdded(JSONArray users) throws JSONException
    {
        //Realm realm = Realm.getDefaultInstance();
        //Room room = roomService.findById(realm, "GENERAL");
        Room room = roomService.findById("GENERAL");

        for (int i = 0; i < users.length(); i++)
        {
            if (room.getMuted().indexOf("\"" + users.getString(i) + "\"") < 0)
            {
                //String newUsername = users.getString(i);
                webSocketService.updateContactsUser();
            }
        }

        //realm.close();
    }


    private void removeRoom(String roomId)
    {
        //roomService.delete(Realm.getDefaultInstance(), roomId);
        //messageService.deleteByRoomId(Realm.getDefaultInstance(), roomId);

        roomService.delete(roomId);
        messageService.deleteByRoomId(roomId);
        //notifyUpdateRoomsUI(false);
    }

    private void subscription(Room room)
    {
        try
        {
            subscriptionHelper.subscriptionStreamNotifyRoomDeleteMessage(room.getRoomId());
            subscriptionHelper.subscriptionStreamNotifyRoomTyping(room.getRoomId());
            subscriptionHelper.subscriptionStreamNotifyRoomWebrtc(room.getRoomId());
            subscriptionHelper.subscriptionStreamRoomMessages(room.getRoomId());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    /**
     * 检查是否符合打开聊天窗口的条件，如果条件符合，打开聊天窗口
     *
     * @param room
     */
    private void checkAndOpenChatRoomActivity(Room room)
    {
        // 如果已打开创建群聊窗口，关闭
        if (CreateGroupDialog.getContext() != null && CreateGroupDialog.getContext().isVisible())
        {
            CreateGroupDialog.getContext().setVisible(false);
        }
        // 如果是我创建的群聊，打打开窗口
        if (room.getCreatorName().equals(currentUserService.findAll().get(0).getUsername()))
        {
            // 通知UI打开聊天窗口
            logger.debug("通知UI打开聊天窗口");
            ChatPanel.getContext().enterRoom(room.getRoomId());

            // 激活左侧房间列表
            //RoomsPanel.getContext().activeItem(0);
        }

    }

    /**
     * 通知UI更新Rooms列表
     */
    private void notifyUpdateRoomsUI(boolean keepSize)
    {
        //((WebSocketService) WebSocketService.context).sendBroadcast(MainFrameActivity.WEBSOCKET_TO_ACTIVITY_ACTION, WebSocketService.EVENT_UPDATE_ROOM_ITEMS);
        RoomsPanel.getContext().notifyDataSetChanged(keepSize);
    }


    private void showDefaultNotification(String roomId, String roomType, String roomName, String content)
    {
        logger.debug("发送通知：roomId = " + roomId
                + ", roomType = " + roomType
                + ", name = " + roomName
                + ", content = " + content);
    }

    /**
     * 震动
     */
    private void vibrator()
    {
    }


    private boolean isForeground()
    {
        return false;
    }

    public void setSubscriptionHelper(SubscriptionHelper subscriptionHelper)
    {
        this.subscriptionHelper = subscriptionHelper;
    }
}
