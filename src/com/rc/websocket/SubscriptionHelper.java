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

    public void setAvatar()
    {
        String messageData = "{\"msg\":\"method\",\"method\":\"setAvatarFromService\",\"params\":[\"data:image/jpeg;base64,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…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\",\"image/jpeg\",\"upload\"],\"id\":\"15\"}";
        String data = getImageStr("/Users/song/desktop/timg.jpeg");

        System.out.println(data);

        String  message = "{\"msg\":\"method\",\"method\":\"setAvatarFromService\",\"params\":[\"data:image/png;base64,"+data+"\",\"image/png\",\"upload\"],\"id\":\"14\"}";
        String ss = "{\"msg\":\"method\",\"method\":\"setAvatarFromService\",\"params\":[\"data:image/png;base64,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\",\"image/png\",\"upload\"],\"id\":\"14\"}";

        webSocket.setMaxPayloadSize(1024);
        webSocket.sendText(message);
    }

    public static String getImageStr(String imgFile) {
        InputStream inputStream = null;
        byte[] data = null;
        try {
            inputStream = new FileInputStream(imgFile);
            data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(Base64.encodeBase64(data));
        //return encoder.encode(data);
    }
}
