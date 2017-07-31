package com.rc.websocket;

import com.neovisionaries.ws.client.*;
import com.rc.app.Launcher;
import com.rc.db.model.*;
import com.rc.db.service.ContactsUserService;
import com.rc.db.service.CurrentUserService;
import com.rc.db.service.MessageService;
import com.rc.db.service.RoomService;
import com.rc.frames.*;
import com.rc.panels.*;
import com.rc.utils.AvatarUtil;
import com.rc.websocket.handler.StreamNotifyUserCollectionHandler;
import com.rc.websocket.handler.StreamRoomMessagesHandler;
import com.rc.websocket.handler.WebSocketListenerAdapter;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.rc.tasks.HttpGetTask;
import com.rc.tasks.HttpResponseListener;

import javax.net.ssl.SSLContext;
import javax.swing.*;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created by song on 09/06/2017.
 */
public class WebSocketClient
{
    private static WebSocketClient context;

    public static String ConnectionStatus = "disconnected";
    public static long LAST_PING_PONG_TIME; // 上次发送ping或pong消息的时间
    public static long LAST_RECONNECT_TIME; // 上次重新连接的时间
    public static long TIMESTAMP_ONE_MINUTES = 1000 * 60;

    private static final String METHOD_LOGIN_ID = "100";
    private static final String METHOD_RESUME_LOGIN_ID = "101";
    private int LOGIN_RETRIES = 0;
    private static boolean sentPingMessage = false;


    private WebSocket webSocket;
    private SubscriptionHelper subscriptionHelper;
    private String hostname = Launcher.HOSTNAME;
    private Logger logger;
    private CurrentUserService currentUserService = Launcher.currentUserService;
    private RoomService roomService = Launcher.roomService;
    private MessageService messageService = Launcher.messageService;
    private ContactsUserService contactsUserService = Launcher.contactsUserService;

    private CurrentUser currentUser;
    private String currentUserId;

    // 已经更新了最后消息及未读消息数的房间数
    private int updatedUnreadMessageRoomsCount = 0;

    private StreamRoomMessagesHandler streamRoomMessagesHandler;
    private StreamNotifyUserCollectionHandler streamNotifyUserCollectionHandler;
    private String uploadFilename = null;
    public long loginSuccessTime;
    private boolean isLogout = false;


    public WebSocketClient()
    {
        context = this;
        logger = Logger.getLogger(this.getClass());

        streamRoomMessagesHandler = new StreamRoomMessagesHandler();
        streamNotifyUserCollectionHandler = new StreamNotifyUserCollectionHandler(WebSocketClient.this);
    }

    public static WebSocketClient getContext()
    {
        return context;
    }

    public void startClient()
    {
        startWebSocketClient();
    }

    private synchronized void startWebSocketClient()
    {
        TitlePanel.getContext().showStatusLabel("服务器连接中");

        if (System.currentTimeMillis() - LAST_RECONNECT_TIME < TIMESTAMP_ONE_MINUTES / 2)
        {
            System.out.println("两次发送 重新连接 请求的时间间隔小于30秒，放弃连接");
            return;
        }

        if (ConnectionStatus.equals("disconnected"))
        {
            if (webSocket != null)
            {
                webSocket.disconnect();
            }

            ConnectionStatus = "connecting";
            logger.debug("*************重新连接*****************");
            prepareWebSocket();
            webSocket.connectAsynchronously();
            if (subscriptionHelper != null)
            {
                subscriptionHelper.setWebSocket(webSocket);
            }
            else
            {
                subscriptionHelper = new SubscriptionHelper(webSocket);
                streamNotifyUserCollectionHandler.setSubscriptionHelper(subscriptionHelper);
            }

            LAST_RECONNECT_TIME = System.currentTimeMillis();
        }
        else
        {
            logger.debug("*************ConnectionStatus不等于disconnected， 放弃重新连接*****************");
        }
    }

    public WebSocket getWebSocket()
    {
        return webSocket;
    }

    /**
     * 初始化WebSocket客户端
     */
    private void prepareWebSocket()
    {
        WebSocketFactory webSocketFactory = new WebSocketFactory();
        // Create a custom SSL context.
        SSLContext context = null;
        try
        {
            context = NaiveSSLContext.getInstance("TLS");
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }

        webSocketFactory.setSSLContext(context);
        String url = hostname + "/websocket";

        try
        {
            webSocket = null;
            webSocket = webSocketFactory.createSocket(url)
                    .setAutoFlush(true)
                    .addListener(new WebSocketListenerAdapter()
                    {
                        @Override
                        public void onStateChanged(WebSocket websocket, WebSocketState newState) throws Exception
                        {
                            System.out.println("+++++++onStateChanged: " + newState.toString());
                        }

                        @Override
                        public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception
                        {
                            System.out.println("+++++++onConnected: ");
                            subscriptionHelper.sendConnectRequest();
                        }


                        @Override
                        public void onConnectError(WebSocket websocket, WebSocketException cause) throws Exception
                        {
                            ConnectionStatus = "disconnected";
                            System.out.println("+++++++onConnectError: " + cause.getMessage());

                            System.out.println("连接出错");
                            connectPersistently();
                        }

                        @Override
                        public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception
                        {
                            System.out.println("+++++++onDisconnected");
                            ConnectionStatus = "disconnected";
                            TitlePanel.getContext().showStatusLabel("与服务器连接已断开");

                            System.out.println("连接断开");
                            connectPersistently();
                        }

                        @Override
                        public void onTextMessage(WebSocket websocket, String text) throws Exception
                        {
                            handleMessage(text);
                        }
                    });


        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void connectPersistently()
    {

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (ConnectionStatus.equals("disconnected") && !isLogout)
                {
                    System.out.println("自动重连中...");
                    startWebSocketClient();
                    try
                    {
                        Thread.sleep(10000);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * 处理新消息路由
     *
     * @param text
     */
    private void handleMessage(String text)
    {
        //System.out.println(("收到消息  " + text));

        try
        {
            JSONObject jsonText = new JSONObject(text);
            if (jsonText.has("msg"))
            {
                String msg = jsonText.getString("msg");

                // 输出
                String id = "";
                if (jsonText.has("id"))
                {
                    id = jsonText.getString("id");
                }

                if (!msg.equals("ping") && !msg.equals("updated") && !msg.equals("ready") && !id.startsWith("SEND_LOAD_UNREAD_COUNT_AND_LAST_MESSAGE"))
                {

                    //logger.debug("收到消息  " + text);
                    //System.out.println(("收到消息  " + text));
                }


                if (msg.equals("ping"))
                {
                    subscriptionHelper.sendPongMessage();
                    LAST_PING_PONG_TIME = System.currentTimeMillis();
                }
                else if (msg.equals("connected"))
                {
                    //sendBroadcast(MainFrameActivity.WEBSOCKET_TO_ACTIVITY_ACTION, EVENT_CONNECT_SUCCESS);
                    login();
                }
                else if (msg.equals("result"))
                {
                    processResultMessage(jsonText);
                }
                else if (msg.equals("ready"))
                {
                    processSubscriptionResult(jsonText);
                }
                else if (msg.equals("changed"))
                {
                    processChangedMessage(jsonText);
                }
                else if (msg.equals("added"))
                {
                    //processAddedMessage(jsonText);
                }
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    // 发送登录信息
    private void login()
    {

        //sendBroadcast(MainFrameActivity.WEBSOCKET_TO_ACTIVITY_ACTION, EVENT_SEND_LOGIN_REQUEST);

        // 获取本地存储的用户信息
        currentUser = currentUserService.findAll().get(0);
        if (currentUser != null)
        {
            currentUserId = currentUser.getUserId();
            // 如果之前已登录，且未过期，则恢复登录
            if (currentUser.getAuthToken() == null || currentUser.getExpireDate() == null || System.currentTimeMillis() >= Long.parseLong(currentUser.getExpireDate()))
            {
                subscriptionHelper.sendNewLoginMessage(currentUser.getUsername(), currentUser.getPassword());
            }
            else
            {
                subscriptionHelper.sendResumeLoginMessage(currentUser.getAuthToken());
                //subscriptionHelper.sendNewLoginMessage(currentUser.getUsername(), currentUser.getPassword());

            }
        }
    }


    /**
     * 处理“msg”为“result”类型的消息
     *
     * @param jsonText
     */
    private void processResultMessage(JSONObject jsonText) throws JSONException
    {

        String msgId = jsonText.getString("id");

        // 登录结果
        if (msgId.equals(METHOD_LOGIN_ID) || msgId.equals(METHOD_RESUME_LOGIN_ID))
        {
            processLoginResult(jsonText);
        }
        else if (msgId.startsWith(SubscriptionHelper.SEND_LOAD_UNREAD_COUNT_AND_LAST_MESSAGE))
        {
            processLoadUnreadCountAndLastMessageResult(jsonText);
        }
        else if (msgId.equals(SubscriptionHelper.METHOD_UFSCREATE))
        {
            processUsfCreate(jsonText);
        }
        else if (msgId.equals(SubscriptionHelper.METHOD_UFSCOMPLETE))
        {
            processUsfComplete(jsonText);
        }
        else if (msgId.equals(SubscriptionHelper.METHOD_SEND_CHANGE_PASSWORD_MESSAGE))
        {
            //subscriptionHelper.sendLogoutMessage();
        }
        else if (msgId.equals(SubscriptionHelper.METHOD_LOGOUT))
        {
            processLogoutMessage();
            /*currentUserService.delete(Realm.getDefaultInstance());
            contactsUserService.deleteAll(Realm.getDefaultInstance(), ContactsUser.class);
            roomService.deleteAll(Realm.getDefaultInstance(), Room.class);

            sendBroadcast(MainFrameActivity.WEBSOCKET_TO_ACTIVITY_ACTION, EVENT_LOGIN);*/
        }
        else if (msgId.equals(SubscriptionHelper.METHOD_CHANNELS_LIST))
        {
            //processChannelsList(jsonText);
        }
        else if (msgId.equals(SubscriptionHelper.METHOD_SEND_CREATE_CHANNEL_OR_GROUP_MESSAGE))
        {
            processCreateChanelOrGroupMessage(jsonText);
        }
        else if (msgId.equals(SubscriptionHelper.METHOD_SET_AVATAR_FROM_SERVUCE))
        {
            // 用户在本地更新头像后会收到此消息
            processUserAvatar();
        }
    }

    /**
     * 处理退出登录消息
     */
    private void processLogoutMessage()
    {
        currentUserService.deleteAll();
        contactsUserService.deleteAll();
        roomService.deleteAll();

        LAST_RECONNECT_TIME = 0;
        LAST_PING_PONG_TIME = 0;
        LOGIN_RETRIES = 0;

        isLogout = true;
        webSocket.sendClose();

        Launcher.getContext().reLogin(currentUser.getUsername());
    }

    private void processUserAvatar()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

                // 更新本地头像
                ContactsPanel.getContext().getUserAvatar(currentUser.getUsername(), true);

                ChangeAvatarPanel avatarPanel = ChangeAvatarPanel.getContext();
                if (avatarPanel != null && avatarPanel.isVisible())
                {
                    avatarPanel.restoreOKButton();
                    avatarPanel.showSuccessMessage();
                }

            }
        }).start();
    }

    /**
     * 处理创建群聊返回消息
     *
     * @param jsonText
     */
    private void processCreateChanelOrGroupMessage(JSONObject jsonText)
    {
        if (jsonText.has("error"))
        {
            String ret = jsonText.getJSONObject("error").getString("reason");
            String roomName = ret.substring(ret.indexOf("'") + 1, ret.lastIndexOf("'"));
            if (CreateGroupDialog.getContext() != null)
            {
                CreateGroupDialog.getContext().showRoomExistMessage(roomName);
            }
        }
    }

    /**
     * 处理登录请求的返回消息
     *
     * @param jsonObject 如果消息中包含“error”键，则需要重新尝试使用新登录的方式发送消息
     *                   如果无包含“error”表示登录成功，则需要更新本地保存的Token信息
     */
    private void processLoginResult(JSONObject jsonObject)
    {
        System.out.println("收到登录响应");
        if (isErrorResult(jsonObject))
        {
            if (LOGIN_RETRIES <= 3)
            {
                login();
                LOGIN_RETRIES++;
            }
            else
            {
                // 重复登录失败，重新连接
                logger.debug("重复登录失败，需要重新连接");
                ConnectionStatus = "disconnected";
                startWebSocketClient();

                //sendBroadcast(MainFrameActivity.WEBSOCKET_TO_ACTIVITY_ACTION, EVENT_WEBSOCKET_DISCONNECT);
                //sendBroadcast(MainFrameActivity.WEBSOCKET_TO_ACTIVITY_ACTION, EVENT_LOGIN);
                //throw new RuntimeException("WebSocket登录次数达到" + LOGIN_RETRIES + ", 登录失败，重新连接");
            }
        }
        else
        {
            //sendBroadcast(MainFrameActivity.WEBSOCKET_TO_ACTIVITY_ACTION, EVENT_LOGIN_SUCCESS);
            try
            {
                TitlePanel.getContext().hideStatusLabel();
                ChatPanel.getContext().restoreRemoteHistoryLoadedRooms();
                ChatPanel.getContext().enterRoom(ChatPanel.CHAT_ROOM_OPEN_ID);

                loginSuccessTime = System.currentTimeMillis();

                if (!sentPingMessage)
                {
                    subscriptionHelper.sendPingMessage();
                    sentPingMessage = true;
                    LAST_PING_PONG_TIME = System.currentTimeMillis();
                    System.out.println("##############发送ping消息################");
                }

                ConnectionStatus = "connected";
                System.out.println("sendOnLineMessage...");
                subscriptionHelper.sendOnLineMessage();

                // 更新token以及过期时间
                JSONObject result = jsonObject.getJSONObject("result");
                String token = result.getString("token");
                long tokenExpires = result.getJSONObject("tokenExpires").getLong("$date");
                currentUser.setAuthToken(token);
                currentUser.setExpireDate(tokenExpires + "");
                currentUserService.update(currentUser);

                // 更新Rooms列表
                updateRoomList();

                // 更新通讯录
                updateContacts();

                // 订阅消息
                sendSubscriptionUserMessage();

            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void updateRoomList()
    {
        updatedUnreadMessageRoomsCount = 0;

        getRooms("channels");
        getRooms("groups");
        getRooms("ims");
    }

    /**
     * 更新Rooms列表
     */
    private void getRooms(final String type)
    {
        TitlePanel.getContext().showStatusLabel("正在同步消息列表...");

        String api = "";
        String roomType = "";
        if (type.equals("channels"))
        {
            api = "channels.list.joined.base";
            roomType = "c";
        }
        else if (type.equals("groups"))
        {
            api = "groups.list.base";
            roomType = "p";
        }
        else if (type.equals("ims"))
        {
            api = "im.list.base";
            roomType = "d";
        }
        else
        {
            throw new RuntimeException("Room 类型" + type + "不存在");
        }

        final String currentUsername = currentUser.getUsername();
        HttpGetTask task = new HttpGetTask();
        task.addHeader("X-Auth-Token", currentUser.getAuthToken());
        task.addHeader("X-User-Id", currentUser.getUserId());
        final String finalRoomType = roomType;
        task.setListener(new HttpResponseListener<JSONObject>()
        {
            @Override
            public void onSuccess(JSONObject retJson)
            {
                try
                {
                    JSONArray objArray = retJson.getJSONArray(type);
                    List<String> newlyRoomIds = new ArrayList<>();
                    if (objArray != null)
                    {
                        for (int i = 0; i < objArray.length(); i++)
                        {
                            JSONObject obj = objArray.getJSONObject(i);

                            Room room = new Room();
                            if (!obj.has("_id"))
                            {
                                continue;
                            }
                            newlyRoomIds.add(obj.getString("_id"));
                            room.setRoomId(obj.getString("_id"));
                            if (obj.has("ro"))
                            {
                                room.setReadOnly(obj.getBoolean("ro"));

                                if (obj.has("muted"))
                                {
                                    JSONArray mutedUser = obj.getJSONArray("muted");
                                    if (mutedUser.length() > 0)
                                    {
                                        String str = mutedUser.join(",");
                                        room.setMuted(str);
                                    }
                                }
                            }
                            if (obj.has("name"))
                            {
                                room.setName(obj.getString("name"));
                            }
                            room.setUpdatedAt(obj.getString("_updatedAt"));

                            if (obj.has("u"))
                            {
                                room.setCreatorId(obj.getJSONObject("u").getString("_id"));
                                room.setCreatorName(obj.getJSONObject("u").getString("username"));
                            }
                            if (obj.has("usernames"))
                            {
                                JSONArray usernameArr = obj.getJSONArray("usernames");
                                if (usernameArr.get(0).equals(currentUsername))
                                {
                                    room.setName(usernameArr.get(1).toString());
                                }
                                else
                                {
                                    room.setName(usernameArr.get(0).toString());
                                }
                            }
                            room.setType(finalRoomType);

                            Room oldRoom = roomService.findById(room.getRoomId());
                            if (oldRoom != null)
                            {
                                room.setMsgSum(oldRoom.getMsgSum());
                                room.setUnreadCount(oldRoom.getUnreadCount());
                                room.setMember(oldRoom.getMember());
                                room.setCreatorName(oldRoom.getCreatorName());
                                room.setLastChatAt(oldRoom.getLastChatAt());

                                /*if (oldRoom.getTotalReadCount() < 0)
                                {
                                    System.out.println("oldRoom.getTotalReadCount()" + oldRoom.getTotalReadCount());
                                }*/
                                room.setTotalReadCount(oldRoom.getTotalReadCount());
                            }

                            roomService.insertOrUpdate(room);
                        }
                    }

                    // 删除已删除的room
                    //Realm realm1 = Realm.getDefaultInstance();
                    List<Room> dbRooms = roomService.find("type", finalRoomType);
                    for (Room r : dbRooms)
                    {
                        if (!newlyRoomIds.contains(r.getRoomId()))
                        {
                            roomService.delete(r.getRoomId());
                        }
                    }

                   /* List<Room> rooms = roomService.findAll();
                    System.out.println(rooms);*/


                    // 订阅Rooms相关消息
                    sendSubscriptionRoomMessage(finalRoomType);

                    // 获取每个房间的未读消息数以及最后一条消息
                    loadUnreadCountAndLastMessage(finalRoomType);


                    //lastUpdateService.update(Realm.getDefaultInstance());
                    //Log.i(TAG_NAME, "当前更新时间:" + lastUpdateService.find(Realm.getDefaultInstance()));
                    // 通知UI更新Rooms列表
                    //sendBroadcast(MainFrameActivity.WEBSOCKET_TO_ACTIVITY_ACTION, EVENT_UPDATE_ROOM_ITEMS);
                    //RoomsPanel.getContext().notifyDataSetChanged();

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                TitlePanel.getContext().hideStatusLabel();
            }

            @Override
            public void onFailed()
            {
                System.out.println("房间获取失败：" + type);
            }
        });

        task.execute(hostname + "/api/v1/" + api);
    }

    // 判断新接收到的消息中是否有“error”键值
    private boolean isErrorResult(JSONObject jsonObject)
    {
        return jsonObject.has("error");
    }

    /**
     * 发送rooms相关订阅消息
     *
     * @param roomType
     */
    private void sendSubscriptionRoomMessage(String roomType)
    {
        //Realm realm = Realm.getDefaultInstance();
        List<Room> rooms = roomService.find("type", roomType);
        for (Room room : rooms)
        {
            subscriptionHelper.subscriptionStreamNotifyRoomTyping(room.getRoomId());
            subscriptionHelper.subscriptionStreamRoomMessages(room.getRoomId());
            subscriptionHelper.subscriptionUserRoomsChanged(room.getRoomId());
            subscriptionHelper.subscriptionStreamNotifyRoomTyping(room.getRoomId());
            subscriptionHelper.subscriptionStreamNotifyRoomWebrtc(room.getRoomId());
        }
    }

    /**
     * 获取每个房间未读数及最后一条消息
     *
     * @param roomType
     */
    private void loadUnreadCountAndLastMessage(final String roomType)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                List<Room> rooms = roomService.find("type", roomType);
                for (Room r : rooms)
                {
                    //updatedUnreadMessageRoomsCount++;
                    subscriptionHelper.sendLoadUnreadCountAndLastMessage(r.getRoomId());
                }
            }
        }).start();
    }

    /**
     * 处理获取每个房间未读数及最后一条消息回调
     *
     * @param jsonText
     * @throws JSONException
     */
    private void processLoadUnreadCountAndLastMessageResult(JSONObject jsonText) throws JSONException
    {
        if (!jsonText.has("result"))
        {
            return;
        }

        JSONObject result = jsonText.getJSONObject("result");

        JSONArray messages = result.getJSONArray("messages");

        if (messages.length() > 0)
        {


            JSONObject message = messages.getJSONObject(0);

            int unreadNotLoaded = result.getInt("unreadNotLoaded");
            int unreadCount = 0;

            String roomId = message.getString("rid");

            Room room = roomService.findById(roomId);

            if (unreadNotLoaded > 0)
            {
                long messageTotalCount = 0;
                long totalReadCount = 0;

                if (room != null)
                {
                    messageTotalCount = room.getMsgSum();
                    totalReadCount = room.getTotalReadCount();
                }

                if (messageTotalCount == 0)
                {
                    //roomService.updateTotalReadCount(roomId, unreadNotLoaded + 1);
                    room.setTotalReadCount(unreadNotLoaded + 1);
                }
                else
                {
                    unreadCount = (int) ((unreadNotLoaded + 1) - totalReadCount);


                    if (unreadCount < 0)
                    {
                        //System.out.println("unreadCount -- " + unreadCount);
                        unreadCount = 0;
                        //roomService.updateTotalReadCount(Realm.getDefaultInstance(), roomId, unreadNotLoaded + 1);
                        room.setTotalReadCount(unreadNotLoaded + 1);
                    }
                    //roomService.updateUnreadCount(Realm.getDefaultInstance(), roomId, unreadCount);
                    room.setUnreadCount(unreadCount);
                }

                //roomService.updateMessageSum(Realm.getDefaultInstance(), roomId, unreadNotLoaded + 1);
                room.setMsgSum(unreadNotLoaded + 1);

                roomService.update(room);
            }

            String messageContent = message.getString("msg");
            boolean isMessagePinned = false;
            if (message.has("t"))
            {
                String t = message.getString("t");

                if (t.equals("message_pinned"))
                {
                    isMessagePinned = true;
                }
                else if (t.equals("au") || t.equals("uj"))
                {
                    messageContent = messageContent + "加入群聊";
                }
                else if (t.equals("r"))
                {
                    String creator = message.getJSONObject("u").getString("username");
                    messageContent = creator + " 更改群名称为：" + messageContent;
                }
                else if (t.equals("ru"))
                {
                    messageContent = messageContent + " 被移出群聊";
                }
                else if (t.equals("ul"))
                {
                    messageContent = messageContent + " 退出群聊";
                }
                else if (t.equals("user-muted"))
                {
                    messageContent = messageContent + " 被禁言";
                }
                else if (t.equals("user-unmuted"))
                {
                    messageContent = messageContent + " 被取消禁言";
                }
                else if (t.equals("subscription-role-added"))
                {
                    if (message.getString("role").equals("owner"))
                    {
                        messageContent = messageContent + " 被赋予了 所有者 角色";
                    }
                    else if (message.getString("role").equals("moderator"))
                    {
                        messageContent = messageContent + " 被赋予了 主持 角色";
                    }
                }
                else if (t.equals("subscription-role-removed"))
                {
                    if (message.getString("role").equals("owner"))
                    {
                        messageContent = messageContent + " 被移除了 所有者 角色";
                    }
                    else if (message.getString("role").equals("moderator"))
                    {
                        messageContent = messageContent + " 被移除了 主持 角色";
                    }
                }

            }

            if (!isMessagePinned)
            {
                Message dbMessage = new Message();
                dbMessage.setId(message.getString("_id"));
                dbMessage.setRoomId(message.getString("rid"));
                dbMessage.setTimestamp(message.getJSONObject("ts").getLong("$date"));
                dbMessage.setSenderId(message.getJSONObject("u").getString("_id"));
                dbMessage.setSenderUsername(message.getJSONObject("u").getString("username"));
                //dbMessage.setUpdatedAt(message.getString("_updatedAt"));
                dbMessage.setUpdatedAt(message.getJSONObject("_updatedAt").getLong("$date"));

                if (message.has("groupable"))
                {
                    dbMessage.setGroupable(message.getBoolean("groupable"));
                }

                // 处理消息内容
                if (message.getString("msg").startsWith("[ ]("))
                {
                    messageContent = message.getString("msg").replaceAll("\\[ \\]\\(.*\\)\\s*", "");
                }

                boolean isattachmentOrImage = false;
                // 处理附件
                if (message.has("attachments") && message.get("attachments") instanceof JSONArray && message.has("file") && !message.getString("msg").startsWith("[ ]("))
                {
                    isattachmentOrImage = true;

                    //Object obj = message.get("attachments");
                    JSONArray attachments = message.getJSONArray("attachments");
                    for (int j = 0; j < attachments.length(); j++)
                    {
                        JSONObject attachment = attachments.getJSONObject(j);
                        if (attachment.has("image_url"))
                        {
                            ImageAttachment imageAttachment = new ImageAttachment();
                            imageAttachment.setId(message.getJSONObject("file").getString("_id"));
                            imageAttachment.setTitle(attachment.getString("title"));
                            imageAttachment.setDescription(attachment.getString("description"));
                            imageAttachment.setImageUrl(attachment.getString("image_url"));
                            imageAttachment.setImagesize(attachment.getLong("image_size"));
                            imageAttachment.setWidth(attachment.getJSONObject("image_dimensions").getInt("width"));
                            imageAttachment.setHeight(attachment.getJSONObject("image_dimensions").getInt("height"));

                            //dbMessage.getImageAttachments().add(imageAttachment);
                            dbMessage.setImageAttachmentId(imageAttachment.getId());

                            //dbMessage.setMessageContent("[图片]");
                            messageContent = "[图片]";
                        }
                        else if (attachment.has("title_link"))
                        {
                            FileAttachment fileAttachment = new FileAttachment();
                            fileAttachment.setId(message.getJSONObject("file").getString("_id"));
                            fileAttachment.setTitle(attachment.getString("title").substring(15));
                            fileAttachment.setDescription(attachment.getString("description"));
                            fileAttachment.setLink(attachment.getString("title_link"));
                            //dbMessage.getFileAttachments().add(fileAttachment);
                            dbMessage.setFileAttachmentId(fileAttachment.getId());
                            //dbMessage.setMessageContent(fileAttachment.getTitle().replace("File Uploaded:", ""));
                            messageContent = fileAttachment.getTitle();

                        }
                    }
                }

                dbMessage.setMessageContent(messageContent);
                //Message lastMessage = messageService.findLastMessage(realm, roomId);
                Message lastMessage = messageService.findLastMessage(roomId);

                // 有未发送成功消息
                if (lastMessage != null
                        && (lastMessage.getUpdatedAt() < 1 || lastMessage.isNeedToResend())
                        && lastMessage.getSenderId().equals(currentUser.getUserId()))
                {
                    //roomService.updateLastMessage(Realm.getDefaultInstance(), roomId, "[有消息发送失败]", lastMessage.getTimestamp());
                    room.setLastMessage("[有消息发送失败]");
                    room.setLastChatAt(lastMessage.getTimestamp());
                }
                else
                {
                    //roomService.updateLastMessage(Realm.getDefaultInstance(), roomId, dbMessage.getMessageContent(), dbMessage.getTimestamp());
                    room.setLastMessage(dbMessage.getMessageContent());
                    room.setLastChatAt(dbMessage.getTimestamp());
                }
                roomService.update(room);

                // 如果是附件消息（包括图片和文件），为避免最后一条消息为自己发出的附件时，
                // 因时间戳问题而导致最后一条附件消息重复加载，要重新更新该消息的timestamp
                if (isattachmentOrImage)
                {
                    //messageService.updateTimestamp(realm, dbMessage.getId(), dbMessage.getTimestamp());
                    messageService.update(dbMessage);
                }
            }
        }

        //updatedUnreadMessageRoomsCount--;
        updatedUnreadMessageRoomsCount++;


        if (updatedUnreadMessageRoomsCount >= roomService.count())
        {
            logger.debug("通知UI更新未读数及最后一条消息");

            RoomsPanel.getContext().notifyDataSetChanged(true);

            // 更新总未读消息数
            ChatPanel.getContext().updateTotalUnreadCount();

            // 通知UI更新未读数及最后一条消息
            //sendBroadcast(MainFrameActivity.WEBSOCKET_TO_ACTIVITY_ACTION, EVENT_UPDATE_ROOM_ITEMS);
        }
        //realm.close();
    }

    /**
     * 更新通讯录
     */
    private void updateContacts()
    {
        updateContactsUser();
    }


    /**
     * 发送请求更新通讯录中的用户信息
     */
    public void updateContactsUser()
    {
        HttpGetTask task = new HttpGetTask();
        task.addHeader("X-Auth-Token", currentUser.getAuthToken());
        task.addHeader("X-User-Id", currentUser.getUserId());
        task.setListener(new HttpResponseListener<JSONObject>()
        {
            public void onSuccess(JSONObject retJson)
            {
                try
                {
                    JSONArray userArray = retJson.getJSONArray("users");
                    if (userArray != null)
                    {
                        //contactsUserService.deleteAll(Realm.getDefaultInstance(), ContactsUser.class);
                        contactsUserService.deleteAll();
                        for (int i = 0; i < userArray.length(); i++)
                        {
                            JSONObject user = userArray.getJSONObject(i);

                            // 忽略自己
                            if (user.getString("_id").equals(currentUserId))
                            {
                                continue;
                            }

                            ContactsUser contactsUser = new ContactsUser(user.getString("_id"), user.getString("username"), user.getString("name"));
                            //contactsUserService.insertOrUpdate(Realm.getDefaultInstance(), contactsUser);
                            contactsUserService.insert(contactsUser);
                        }
                    }

                    // 通知UI更新通讯录
                    //sendBroadcast(MainFrameActivity.WEBSOCKET_TO_ACTIVITY_ACTION, EVENT_UPDATE_CONTACTS);
                    ContactsPanel.getContext().notifyDataSetChanged();

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed()
            {
                System.out.println("通讯录获取失败");
            }
        });

        task.execute(hostname + "/api/v1/users.list.base?count=1000");
    }

    /**
     * 发送user相关订阅消息
     */
    private void sendSubscriptionUserMessage()
    {
        // 订阅用户消息
        subscriptionHelper.subscriptionUserMessage(currentUserId);
        subscriptionHelper.subscriptionUserWebRtc(currentUserId);
        subscriptionHelper.subscriptionUserNotification(currentUserId);
        subscriptionHelper.subscriptionUserOtr(currentUserId);
        subscriptionHelper.subscriptionUserRoomsChanged(currentUserId);
        subscriptionHelper.subscriptionUsersubscriptionsChanged(currentUserId);
        subscriptionHelper.subscriptionUserData();
    }

    /**
     * 处理成功订阅返回信息
     *
     * @param jsonText
     */
    private void processSubscriptionResult(JSONObject jsonText)
    {
        //Log.d(TAG_NAME, "订阅返回消息: " + jsonText);
    }

    /**
     * 处理msg为changed的消息
     *
     * @param jsonText
     */
    private void processChangedMessage(JSONObject jsonText) throws JSONException
    {
        String collection = jsonText.getString("collection");
        if (jsonText.has("fields"))
        {
            JSONObject fields = jsonText.getJSONObject("fields");
            if (collection.equals("stream-notify-user"))
            {
                String eventName = fields.getString("eventName");
                eventName = eventName.substring(eventName.indexOf("/") + 1);
                streamNotifyUserCollectionHandler.handle(eventName, jsonText.getJSONObject("fields").getJSONArray("args"));
            }
            else if (collection.equals("stream-room-messages"))
            {
                String eventName = fields.getString("eventName");
                eventName = eventName.substring(eventName.indexOf("/") + 1);
                streamRoomMessagesHandler.handle(eventName, jsonText.getJSONObject("fields").getJSONArray("args"));
            }
            else if (collection.equals("users"))
            {
                updateCurrentUser(fields);
            }
        }
        else
        {
            if (jsonText.has("cleared"))
            {

                JSONArray arr = jsonText.getJSONArray("cleared");
                if (arr.get(0).equals("avatarOrigin"))
                {
                    System.out.println("头像被清空");
                    AvatarUtil.deleteCustomAvatar(currentUser.getUsername());
                }
            }
            System.out.println(jsonText);
            //{"msg":"changed","collection":"users","id":"Ni7bJcX3W8yExKSa3","cleared":["avatarOrigin"]}
        }

    }

    private void processUsfCreate(JSONObject jsonText) throws JSONException
    {

        if (uploadFilename == null)
        {
            return;
        }

        if (jsonText.has("error"))
        {
            processUsfCreateErrorMessage(jsonText.getJSONObject("error"));
            return;
        }

        JSONObject res = jsonText.getJSONObject("result");
        final String fileId = res.getString("fileId");
        final String token = res.getString("token");
        String url = res.getString("url");

        // 通知开始上传文件
        /*Map<String, String> data = new HashMap<>();
        data.put("url", url);
        data.put("uploadFilename", uploadFilename);
        data.put("token", token);
        data.put("fileId", fileId);*/

        //sendBroadcast(MainFrameActivity.WEBSOCKET_TO_ACTIVITY_ACTION, EVENT_START_UPLOAD_FILE, data);
        //webSocket.sendBinary(data);

        ChatPanel.getContext().notifyStartUploadFile(url, uploadFilename, fileId, token);
    }

    /**
     * 处理文件上传时UsfCreate消息的错误
     *
     * @param error
     */
    private void processUsfCreateErrorMessage(JSONObject error) throws JSONException
    {
        String errorType = error.getString("error");

        // 上传文件过大
        if (errorType.equals("error-file-too-large"))
        {
            JOptionPane.showMessageDialog(null, "上传单个文件大小不能超过20M", "文件过大", JOptionPane.WARNING_MESSAGE);
        }
        else if (errorType.equals("error-invalid-file-type"))
        {
            JOptionPane.showMessageDialog(null, "不支持的文件类型", "文件类型不正确", JOptionPane.WARNING_MESSAGE);
        }


        Room room = roomService.findById(ChatPanel.CHAT_ROOM_OPEN_ID);
        room.setLastMessage("");
        roomService.update(room);

        // 继续上传后面的文件
        ChatPanel.getContext().dequeueAndUpload();
        RoomsPanel.getContext().updateRoomItem(ChatPanel.CHAT_ROOM_OPEN_ID);
    }

    private void processUsfComplete(JSONObject jsonText) throws JSONException
    {
        JSONObject res = jsonText.getJSONObject("result");
        String roomId = res.getString("rid");
        String fileId = res.getString("_id");
        String url = res.getString("url");
        String name = res.getString("name");
        long size = res.getLong("size");
        String identify = null;
        if (res.has("identify"))
        {
            identify = res.getJSONObject("identify").toString();
        }

        String type = res.getString("type");
        subscriptionHelper.sendFileMessage(roomId, fileId, url, name, size, type, identify);
    }

    /**
     * 更新当前用户的信息
     *
     * @param fields
     */
    private void updateCurrentUser(JSONObject fields)
    {
        try
        {
            //Realm realm = Realm.getDefaultInstance();
            //CurrentUser user = realm.copyFromRealm(currentUserService.find(realm));
            CurrentUser user = currentUserService.findAll().get(0);

            if (fields.has("services"))
            {
                String bcript = fields.getJSONObject("services").getJSONObject("password").getString("bcrypt");

                // 修改了密码，要重新登录
                if (!bcript.equals(user.getBcrypt()) && user.getBcrypt() != null)
                {

                    // 如果已打开修改窗口
                    if (ChangePasswordPanel.getContext() != null && ChangePasswordPanel.getContext().isVisible())
                    {
                        ChangePasswordPanel.getContext().showSuccessMessage();
                        Thread.sleep(1500);
                    }


                    currentUserService.deleteAll();
                    LAST_RECONNECT_TIME = 0;
                    LAST_PING_PONG_TIME = 0;
                    LOGIN_RETRIES = 0;
                    isLogout = true;
                    webSocket.sendClose();
                    Launcher.getContext().reLogin(currentUser.getUsername());
                    return;
                }
                user.setBcrypt(bcript);

            }
            else if (fields.has("name"))
            {
                String name = fields.getString("name");
                user.setRealName(name);

                if (fields.has("avatarOrigin"))
                {
                    String avatarOrigin = fields.getString("avatarOrigin");
                    user.setAvatarOrigin(avatarOrigin);
                }

                //currentUserService.insertOrUpdate(Realm.getDefaultInstance(), user);
            }
            // 其他客户端更改了头像
            else if (fields.has("avatarOrigin"))
            {
                ContactsPanel.getContext().getUserAvatar(currentUser.getUsername(), true);
            }

            currentUserService.insertOrUpdate(user);
            //realm.close();

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public boolean loginAndInitFinish()
    {
        return System.currentTimeMillis() - loginSuccessTime > 5000;
    }


    /**
     * 发送文本消息
     */
    public void sendTextMessage(String roomId, String messageId, String content)
    {
        subscriptionHelper.sendTextMessage(roomId, messageId, content);
    }

    public void sendFileMessage(String roomId, String uploadFilePath)
    {
        uploadFilename = uploadFilePath;
        subscriptionHelper.sendUfsCreateMessage(roomId, uploadFilePath);
    }

    public void sendUfsCompleteMessage(String fileId, String token)
    {
        subscriptionHelper.sendUfsCompleteMessage(fileId, token);
        uploadFilename = null;
    }

    public void sendReadMessage(String roomId)
    {
        subscriptionHelper.sendReadMessages(roomId);
    }

    public void createDirectChat(String username)
    {
        subscriptionHelper.sendCreateDirectMessage(username);
    }

    public void createChannelOrGroup(String name, String members, boolean privateGroup)
    {
        subscriptionHelper.sendCreateChannelOrGroupMessage(name, members, privateGroup, false);
    }

    public void setAvatar(String base64Data)
    {
        subscriptionHelper.setAvatar(base64Data);
    }

    /**
     * 发送退出登录消息
     */
    public void sendLogoutMessage()
    {
        subscriptionHelper.sendLogoutMessage();
    }

    /**
     * 发送修改密码消息
     *
     * @param password
     */
    public void sendChangePasswordMessage(String password)
    {
        subscriptionHelper.sendChangePasswordMessage(currentUser.getRealName(), currentUser.getPassword(), password);
    }
}
