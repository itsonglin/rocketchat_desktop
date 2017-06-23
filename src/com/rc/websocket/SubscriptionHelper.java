package com.rc.websocket;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.rc.db.model.Room;
import com.rc.db.service.RoomService;
import com.rc.utils.DbUtils;
import com.rc.utils.MimeTypeUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import sun.misc.BASE64Encoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

/**
 * Created by song on 20/03/2017.
 */

public class SubscriptionHelper
{
    private SqlSession sqlSession = DbUtils.getSqlSession();

    public static final String METHOD_LOGIN_ID = "100";
    public static final String METHOD_RESUME_LOGIN_ID = "101";

    public static final String SUB_USER_MESSAGE = "SUB_USER_MESSAGE";
    public static final String SUB_USER_WEBRTC = "SUB_USER_WEBRTC";
    public static final String SUB_USER_NOTIFICATION = "SUB_USER_NOTIFICATION";
    public static final String SUB_USER_OTR = "SUB_USER_OTR";
    public static final String SUB_USER_ROOMS_CHAMGE = "SUB_USER_ROOMS_CHAMGE";
    public static final String SUB_USER_SUBSCRIPTIONS_CHANGED = "SUB_USER_SUBSCRIPTIONS_CHANGED";
    public static final String SUB_STREAM_ROOM_MESSAGES = "SUB_STREAM_ROOM_MESSAGES";
    public static final String SUB_STREAM_NOTIFY_ROOM_DELETE_MESSAGE = "SUB_STREAM_NOTIFY_ROOM_DELETE_MESSAGE";
    public static final String SUB_STREAM_NOTIFY_ROOM_TYPING = "SUB_STREAM_NOTIFY_ROOM_TYPING";
    public static final String SUB_STREAM_NOTIFY_ROOM_WEBRTC = "SUB_STREAM_NOTIFY_ROOM_WEBRTC";
    public static final String SEND_LOAD_UNREAD_COUNT_AND_LAST_MESSAGE = "SEND_LOAD_UNREAD_COUNT_AND_LAST_MESSAGE";
    public static final String SEND_CREATE_DIRECT_MESSAGE = "SEND_CREATE_DIRECT_MESSAGE";
    public static final String METHOD_UFSCREATE = "METHOD_UFSCREATE";
    public static final String METHOD_UFSCOMPLETE = "METHOD_UFSCOMPLETE";
    public static final String METHOD_SEND_FILE_MESSAGE = "METHOD_SEND_FILE_MESSAGE";
    public static final String METHOD_SEND_CREATE_CHANNEL_OR_GROUP_MESSAGE = "METHOD_SEND_CREATE_CHANNEL_OR_GROUP_MESSAGE";
    public static final String METHOD_SEND_CHANGE_PASSWORD_MESSAGE = "METHOD_SEND_CHANGE_PASSWORD_MESSAGE";
    public static final String METHOD_LOGOUT = "METHOD_LOGOUT";
    public static final String METHOD_CHANNELS_LIST = "METHOD_CHANNELS_LIST";
    public static final String METHOD_SET_AVATAR_FROM_SERVUCE = "METHOD_SET_AVATAR_FROM_SERVUCE";


    /**
     * 我上次主动发起直接聊天的对象的用户名，用于区分是否是由我主动创建的直接聊天
     */
    public static String lastCreateDirectChatUsername;

    private List<String> SUB_STREAM_ROOM_MESSAGE;

    private WebSocket webSocket;
    //private LastUpdateService lastUpdateService = new LastUpdateService();
    private RoomService roomService = new RoomService(sqlSession);


    private Logger logger;

    public SubscriptionHelper(WebSocket webSocket)
    {
        this.webSocket = webSocket;
        logger = Logger.getLogger(this.getClass());
    }

    public void sendNewLoginMessage(String username, String password)
    {
        String message = "{\"msg\":\"method\",\"method\":\"login\",\"params\":[{\"user\":{\"username\":\"" +
                username +
                "\"},\"password\":{\"digest\":\"" +
                password +
                "\",\"algorithm\":\"sha-256\"}}],\"id\":\"" +
                METHOD_LOGIN_ID + "\"}";

        logger.debug("发送新的登录消息: " + message);

        webSocket.sendText(message);

    }

    public void sendResumeLoginMessage(String authToken)
    {
        String message = "{\"msg\":\"method\",\"method\":\"login\",\"params\":[{\"resume\":\"" +
                authToken +
                "\"}],\"id\":\"" +
                METHOD_RESUME_LOGIN_ID + "\"}";
        logger.debug("发送恢复登录消息: " + message);
        webSocket.sendText(message);
    }


    /**
     * 发送创建direct message消息
     */
    public void sendCreateDirectMessage(String username)
    {
        String message = "{\"msg\":\"method\",\"method\":\"createDirectMessage\",\"params\":[\"" + username + "\"],\"id\":\"" + SEND_CREATE_DIRECT_MESSAGE + "\"}";
        logger.debug("Send create direct message: " + message);

        lastCreateDirectChatUsername = username;

        webSocket.sendText(message);
    }

    public void subscriptionUserMessage(String userId)
    {
        String message;
        message = "{\"msg\":\"sub\",\"id\":\"" +
                SUB_USER_MESSAGE +
                "\",\"name\":\"stream-notify-user\",\"params\":[\"" +
                userId +
                "/message\",false]}";
        webSocket.sendText(message);
    }

    public void subscriptionUserWebRtc(String userId)
    {
        String message;
        message = "{\"msg\":\"sub\",\"id\":\"" +
                SUB_USER_WEBRTC +
                "\",\"name\":\"stream-notify-user\",\"params\":[\"" +
                userId +
                "/webrtc\",false]}";
        webSocket.sendText(message);
    }

    public void subscriptionUserNotification(String userId)
    {
        String message;
        message = "{\"msg\":\"sub\",\"id\":\"" +
                SUB_USER_NOTIFICATION +
                "\",\"name\":\"stream-notify-user\",\"params\":[\"" +
                userId +
                "/notification\",false]}";
        webSocket.sendText(message);
    }

    public void subscriptionUserOtr(String userId)
    {
        String message;
        message = "{\"msg\":\"sub\",\"id\":\"" +
                SUB_USER_OTR +
                "\",\"name\":\"stream-notify-user\",\"params\":[\"" +
                userId +
                "/otr\",false]}";
        webSocket.sendText(message);
    }

    public void subscriptionUserRoomsChanged(String userId)
    {
        String message;
        message = "{\"msg\":\"sub\",\"id\":\"" +
                SUB_USER_ROOMS_CHAMGE +
                "\",\"name\":\"stream-notify-user\",\"params\":[\"" +
                userId +
                "/rooms-changed\",false]}";
        webSocket.sendText(message);
    }

    public void subscriptionUsersubscriptionsChanged(String userId)
    {
        String message;
        message = "{\"msg\":\"sub\",\"id\":\"" +
                SUB_USER_SUBSCRIPTIONS_CHANGED +
                "\",\"name\":\"stream-notify-user\",\"params\":[\"" +
                userId +
                "/subscriptions-changed\",false]}";
        webSocket.sendText(message);
    }


    public void subscriptionStreamRoomMessages(String roomId)
    {
        String message = "{\"msg\":\"sub\",\"id\":\"" + SUB_STREAM_ROOM_MESSAGES + roomId + "\",\"name\":\"stream-room-messages\",\"params\":[\"" + roomId + "\",false]}";
        webSocket.sendText(message);
    }

    public void subscriptionStreamNotifyRoomDeleteMessage(String roomId)
    {
        String message = "{\"msg\":\"sub\",\"id\":\"" + SUB_STREAM_NOTIFY_ROOM_DELETE_MESSAGE + roomId + "\",\"name\":\"stream-notify-room\",\"params\":[\"" + roomId + "/deleteMessage\",false]}";
        webSocket.sendText(message);
    }

    public void subscriptionStreamNotifyRoomTyping(String roomId)
    {
        String message = "{\"msg\":\"sub\",\"id\":\"" + SUB_STREAM_NOTIFY_ROOM_TYPING + roomId + "\",\"name\":\"stream-notify-room\",\"params\":[\"" + roomId + "/typing\",false]}";
        webSocket.sendText(message);
    }

    public void subscriptionStreamNotifyRoomWebrtc(String roomId)
    {
        String message = "{\"msg\":\"sub\",\"id\":\"" + SUB_STREAM_NOTIFY_ROOM_WEBRTC + roomId + "\",\"name\":\"stream-notify-room\",\"params\":[\"" + roomId + "/webrtc\",false]}";
        webSocket.sendText(message);
    }

    /**
     * 发送连接请求消息
     */
    public void sendConnectRequest()
    {
        System.out.println("===  sendConnectRequest");
        webSocket.sendText("{\"msg\":\"connect\",\"version\":\"1\",\"support\":[\"1\",\"pre2\",\"pre1\"]}");
    }

    /**
     * 响应ping消息
     */
    public void sendPongMessage()
    {
        ///System.out.println("pong...");
        webSocket.sendText("{\"msg\":\"pong\"}");
    }

    public void sendPingMessage()
    {
        String message = "{\"msg\":\"ping\",\"id\":\"" + UUID.randomUUID() + "\"}";
        System.out.println("send:" + message);

        webSocket.sendText(message);
    }

    public void ping()
    {
        String message = "{\"msg\":\"ping\"}";
        System.out.println("send:" + message);
        webSocket.sendText(message);
    }

    /**
     * 获取房间的未读消息数以及最后一条消息
     *
     * @param roomId
     */
    public void sendLoadUnreadCountAndLastMessage(String roomId)
    {
        //List<Room>roomList = roomService.findAll(Realm.getDefaultInstance());
        String id = SEND_LOAD_UNREAD_COUNT_AND_LAST_MESSAGE + roomId;
        /*Realm realm = Realm.getDefaultInstance();
        Room room = roomService.findById(realm, roomId);*/

        Room room = roomService.findById(roomId);

        if (room != null)
        {
            // 获取startTime以前的信息
            String message = "{\"msg\":\"method\",\"method\":\"loadHistory\",\"params\":[\"" + roomId + "\",null,1,{\"$date\":" + 0 + "}],\"id\":\"" + id + "\"}";
            webSocket.sendText(message);
        }
    }

    /**
     * 发送文本聊天消息
     *
     * @param roomId
     * @param messageId
     * @param content
     */
    public void sendTextMessage(String roomId, String messageId, String content)
    {
        String message = "{\"msg\":\"method\",\"method\":\"sendMessage\",\"params\":[{\"_id\":\"" + messageId + "\",\"rid\":\"" + roomId + "\",\"msg\":\"" + content + "\"}],\"id\":\"" + messageId + "\"}";
        webSocket.sendText(message);
    }

    public void sendUfsCreateMessage(String roomId, String file)
    {
        File sendFile = new File(file);
        if (!sendFile.exists())
        {
            throw new RuntimeException("文件不存在");
        }

        String filename = sendFile.getName();
        String type = MimeTypeUtil.getMime(filename.substring(filename.lastIndexOf(".")));
        long size = sendFile.length();
        String message = "{\"msg\":\"method\",\"method\":\"ufsCreate\",\"params\":[{\"name\":\"" + filename + "\",\"size\":" + size + ",\"type\":\"" + type + "\",\"rid\":\"" + roomId + "\",\"description\":\"\",\"store\":\"fileSystem\"}],\"id\":\"" + METHOD_UFSCREATE + "\"}";

        webSocket.sendText(message);
    }

    public void sendUfsCompleteMessage(String fileId, String token)
    {
        final String message = "{\"msg\":\"method\",\"method\":\"ufsComplete\",\"params\":[\"" + fileId + "\",\"fileSystem\",\"" + token + "\"],\"id\":\"" + METHOD_UFSCOMPLETE + "\"}";

        webSocket.sendText(message);
    }

    public void sendFileMessage(String roomId, String fileId, String url, String filename, long size, String type, String identify)
    {
        url = url.substring(url.indexOf("/ufs"));
        if (identify == null || identify.equals(""))
        {
            identify = "";
        }
        else
        {
            identify = "\"identify\":" + identify + ",";
        }


        String message = "{\"msg\":\"method\",\"method\":\"sendFileMessage\",\"params\":[\"" + roomId + "\",null,{\"_id\":\"" + fileId + "\",\"type\":\"" + type + "\",\"size\":" + size + ",\"name\":\"" + filename + "\"," + identify + "\"description\":\"\",\"url\":\"" + url + "\"}],\"id\":\"" + METHOD_SEND_FILE_MESSAGE + "\"}";

        webSocket.sendText(message);
    }


    /**
     * 创建Channel或Group
     *
     * @param name
     * @param members
     * @param privateGroup
     * @param readOnly
     */
    public void sendCreateChannelOrGroupMessage(String name, String members, boolean privateGroup, boolean readOnly)
    {
        String method = "createChannel";
        if (privateGroup)
        {
            method = "createPrivateGroup";
        }
        String message = "{\"msg\":\"method\",\"method\":\"" + method + "\",\"params\":[\"" + name + "\"," + members + "," + readOnly + "],\"id\":\"" + METHOD_SEND_CREATE_CHANNEL_OR_GROUP_MESSAGE + "\"}";
        webSocket.sendText(message);
    }

    public void setWebSocket(WebSocket webSocket)
    {
        this.webSocket = webSocket;
    }

    public void sendChangePasswordMessage(String realName, String oldPassword, String newPassword)
    {
        String message = "{\"msg\":\"method\",\"method\":\"saveUserProfile\",\"params\":[{\"currentPassword\":\"" + oldPassword + "\",\"newPassword\":\"" + newPassword + "\",\"realname\":\"" + realName + "\"},{}],\"id\":\"" + METHOD_SEND_CHANGE_PASSWORD_MESSAGE + "\"}";
        webSocket.sendText(message);
    }

    /**
     * 发送退出登录消息
     */
    public void sendLogoutMessage()
    {
        String message = "{\"msg\":\"method\",\"method\":\"logout\",\"params\":[],\"id\":\"" + METHOD_LOGOUT + "\"}";
        webSocket.sendText(message);

    }

    /**
     * 订阅用户信息
     */
    public void subscriptionUserData()
    {
        String message = "{\"msg\":\"sub\",\"id\":\"tQ7ihuYvNv4eoizj6\",\"name\":\"userData\",\"params\":[]}";
        webSocket.sendText(message);
    }

    public void roomFiles(String roomId)
    {
        String message = "{\"msg\":\"sub\",\"id\":\"" + System.currentTimeMillis() + "\",\"name\":\"roomFiles\",\"params\":[\"" + roomId + "\",50]}";
        webSocket.sendText(message);
    }

    public void sendOnLineMessage()
    {
        String message = "{\"msg\":\"method\",\"method\":\"UserPresence:online\",\"id\":\"" + UUID.randomUUID() + "\"}";
        webSocket.sendText(message);
    }

    public void sendAwayMessage()
    {
        String message = "{\"msg\":\"method\",\"method\":\"UserPresence:away\",\"id\":\"" + UUID.randomUUID() + "\"}";
        webSocket.sendText(message);
    }

    public void sendChannelsListMessage(String name)
    {
        String message = "{\"msg\":\"method\",\"method\":\"channelsList\",\"params\":[\"" + name + "\",\"all\",50,\"name\"],\"id\":\"" + METHOD_CHANNELS_LIST + "\"}";
        webSocket.sendText(message);
    }

    public void sendReadMessages(String roomId)
    {
        String message = "{\"msg\":\"method\",\"method\":\"readMessages\",\"params\":[\"" + roomId + "\"],\"id\":\"READ_MESSAGE\"}";
        webSocket.sendText(message);
    }

    public void setAvatar(String base64Data)
    {
        String message = "{\"msg\":\"method\",\"method\":\"setAvatarFromService\",\"params\":[\"data:image/png;base64,"
                + base64Data + "\",\"image/png\",\"upload\"],\"id\":\"" + METHOD_SET_AVATAR_FROM_SERVUCE + "\"}";

        webSocket.setMaxPayloadSize(1024);
        webSocket.sendText(message);
    }
}
