package com.rc.websocket;

import com.neovisionaries.ws.client.*;
import com.rc.app.Launcher;
import com.rc.db.model.CurrentUser;
import com.rc.db.model.Room;
import com.rc.db.service.CurrentUserService;
import com.rc.db.service.RoomService;
import com.rc.websocket.handler.WebSocketListenerAdapter;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import sun.security.krb5.Realm;
import tasks.HttpGetTask;
import tasks.HttpResponseListener;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by song on 09/06/2017.
 */
public class WebSocketClient
{

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
    private String hostname = "https://rc.shls-leasing.com";
    private Logger logger;
    private CurrentUserService currentUserService = Launcher.currentUserService;
    private RoomService roomService = Launcher.roomService;
    private CurrentUser currentUser;
    private String currentUserId;


    public WebSocketClient()
    {
        logger = Logger.getLogger(this.getClass());
    }

    public void startClient()
    {
        startWebSocketClient();
    }

    private void startWebSocketClient()
    {
        if (System.currentTimeMillis() - LAST_RECONNECT_TIME < TIMESTAMP_ONE_MINUTES / 2)
        {
            logger.debug("两次发送 重新连接 请求的时间间隔小于30秒，放弃连接");
            //sendBroadcast(MainFrameActivity.WEBSOCKET_TO_ACTIVITY_ACTION, EVENT_ABANDON_CONNECTION);
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
            }

            LAST_RECONNECT_TIME = System.currentTimeMillis();
        }
        else
        {
            logger.debug("*************ConnectionStatus不等于disconnected， 放弃重新连接*****************");
        }
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
                            LAST_RECONNECT_TIME = 0;
                            System.out.println("+++++++onConnectError: " + cause.getMessage());
                           /* if (cause.getMessage().startsWith("Failed to connect to") && !networkDisabled)
                            {
                                Log.e("restartApplication", "restartApplication");
                                restartApplication();
                            }
                            else if (!networkDisabled)
                            {
                                sendBroadcast(MainFrameActivity.WEBSOCKET_TO_ACTIVITY_ACTION, EVENT_CONNECT_ERROR);
                            }
                            else if (networkDisabled)
                            {
                                sendBroadcast(MainFrameActivity.WEBSOCKET_TO_ACTIVITY_ACTION, EVENT_NETWORK_DISABLED);
                            }*/
                        }

                        @Override
                        public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception
                        {
                            System.out.println("+++++++onDisconnected");
                            ConnectionStatus = "disconnected";

                            /*if (!networkDisabled)
                            {
                                System.out.println("==========重新连接。。。。");
                                sendBroadcast(MainFrameActivity.WEBSOCKET_TO_ACTIVITY_ACTION, EVENT_RECONNECTING);
                                startWebSocketClient();
                            }
                            else
                            {
                                Log.e("onDisconnected", "连接已断开，网络不可用，放弃重连");
                            }*/
                        }

                        @Override
                        public void onTextMessage(WebSocket websocket, String text) throws Exception
                        {
                            handleMessage(text);
                            //System.out.println(text);
                        }
                    });


        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 处理新消息路由
     *
     * @param text
     */
    private void handleMessage(String text)
    {
        //Log.d("收到消息", text);

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

               /* if (!msg.equals("ping") && !msg.equals("updated") && !msg.equals("ready")
                        && !id.startsWith("SEND_LOAD_UNREAD_COUNT_AND_LAST_MESSAGE"))*/
                {

                    logger.debug("收到消息  " + text);
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
                    //processSubscriptionResult(jsonText);
                }
                else if (msg.equals("changed"))
                {
                    //processChangedMessage(jsonText);
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
            //processLoadUnreadCountAndLastMessageResult(jsonText);
        }
        else if (msgId.equals(SubscriptionHelper.METHOD_UFSCREATE))
        {
            //processUsfCreate(jsonText);
        }
        else if (msgId.equals(SubscriptionHelper.METHOD_UFSCOMPLETE))
        {
            //processUsfComplete(jsonText);
        }
        else if (msgId.equals(SubscriptionHelper.METHOD_SEND_CHANGE_PASSWORD_MESSAGE))
        {
            //subscriptionHelper.sendLogoutMessage();
        }
        else if (msgId.equals(SubscriptionHelper.METHOD_LOGOUT))
        {
            /*currentUserService.delete(Realm.getDefaultInstance());
            contactsUserService.deleteAll(Realm.getDefaultInstance(), ContactsUser.class);
            roomService.deleteAll(Realm.getDefaultInstance(), Room.class);

            sendBroadcast(MainFrameActivity.WEBSOCKET_TO_ACTIVITY_ACTION, EVENT_LOGIN);*/
        }
        else if (msgId.equals(SubscriptionHelper.METHOD_CHANNELS_LIST))
        {
            //processChannelsList(jsonText);
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
                //String userId = result.getString("id");
                String token = result.getString("token");
                long tokenExpires = result.getJSONObject("tokenExpires").getLong("$date");

                //currentUser = currentUserService.findAll().get(0);
                currentUser.setAuthToken(token);
                currentUser.setExpireDate(tokenExpires + "");
                currentUserService.update(currentUser);

                // 更新Rooms列表
                updateRoomList();

                // 更新通讯录
                //updateContacts();

                // 订阅消息
                //sendSubscriptionUserMessage();

            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void updateRoomList()
    {
        getRooms("channels");
        getRooms("groups");
        getRooms("ims");
    }

    /**
     * 更新Rooms列表
     */
    private void getRooms(final String type)
    {
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

        //final Realm realm = Realm.getDefaultInstance();
        final String currentUsername = currentUser.getUsername();
        HttpGetTask task = new HttpGetTask();
        task.addHeader("X-Auth-Token", currentUser.getAuthToken());
        task.addHeader("X-User-Id", currentUser.getUserId());
        final String finalRoomType = roomType;
        task.setListener(new HttpResponseListener()
        {
            @Override
            public void onResult(JSONObject retJson)
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

                            //Realm r = Realm.getDefaultInstance();
                            //Room oldRoom = roomService.findById(r, room.getRoomId());
                            /*Room oldRoom = roomService.findById(room.getRoomId());
                            if (oldRoom != null)
                            {
                                room.setMsgSum(oldRoom.getMsgSum());
                                room.setUnreadCount(oldRoom.getUnreadCount());
                                room.setMember(oldRoom.getMember());
                                room.setCreatorName(oldRoom.getCreatorName());
                                room.setLastChatAt(oldRoom.getLastChatAt());

                                if (oldRoom.getTotalReadCount() < 0)
                                {
                                    System.out.println("oldRoom.getTotalReadCount()" + oldRoom.getTotalReadCount());
                                }
                                room.setTotalReadCount(oldRoom.getTotalReadCount());
                            }*/

                           // roomService.insertOrUpdate(room);
                        }
                    }

                    // 删除已删除的room
                    //Realm realm1 = Realm.getDefaultInstance();
                    /*List<Room> dbRooms = roomService.find("type", finalRoomType);
                    for (Room r : dbRooms)
                    {
                        if (!newlyRoomIds.contains(r.getRoomId()))
                        {
                            roomService.delete(r.getRoomId());
                        }
                    }*/


                    System.out.println(roomService.findAll());
                    // 订阅Rooms相关消息
                    //sendSubscriptionRoomMessage(finalRoomType);
                    // 获取每个房间的未读消息数以及最后一条消息
                    //loadUnreadCountAndLastMessage(finalRoomType);


                    //lastUpdateService.update(Realm.getDefaultInstance());
                    //Log.i(TAG_NAME, "当前更新时间:" + lastUpdateService.find(Realm.getDefaultInstance()));
                    // 通知UI更新Rooms列表
                    //sendBroadcast(MainFrameActivity.WEBSOCKET_TO_ACTIVITY_ACTION, EVENT_UPDATE_ROOM_ITEMS);

                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });

        task.execute(hostname + "/api/v1/" + api);
    }

    // 判断新接收到的消息中是否有“error”键值
    private boolean isErrorResult(JSONObject jsonObject)
    {
        return jsonObject.has("error");
    }
}
