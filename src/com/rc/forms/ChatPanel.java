package com.rc.forms;

import com.rc.adapter.message.MessageAdapter;
import com.rc.app.Launcher;
import com.rc.components.Colors;
import com.rc.components.GBC;
import com.rc.components.RCBorder;
import com.rc.components.RCListView;
import com.rc.db.model.*;
import com.rc.db.service.*;
import com.rc.entity.MessageItem;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import tasks.HttpGetTask;
import tasks.HttpResponseListener;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * 右侧聊天面板
 * <p>
 * Created by song on 17-5-30.
 */
public class ChatPanel extends ParentAvailablePanel
{
    private static ChatPanel context;

    public static final long TIMESTAMP_8_HOURS = 28800000L;
    public static String CHAT_ROOM_OPEN_ID = "";

    // APP启动时，已加载过远程未读消息的Rooms
    private static List<String> remoteHistoryLoadedRooms = new ArrayList<>();

    private MessagePanel messagePanel;
    private MessageEditorPanel messageEditorPanel;
    private java.util.List<MessageItem> messageItems = new ArrayList<>();
    private MessageAdapter adapter;
    private CurrentUser currentUser;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private Room room; // 当前房间
    private long firstMessageTimestamp = 0L;


    private MessageService messageService = Launcher.messageService;
    private CurrentUserService currentUserService = Launcher.currentUserService;
    private RoomService roomService = Launcher.roomService;
    private ImageAttachmentService imageAttachmentService = Launcher.imageAttachmentService;
    private FileAttachmentService fileAttachmentService = Launcher.fileAttachmentService;


    // 当前消息分页数
    private int page = 1;

    // 每次加载的消息条数
    private static final int PAGE_LENGTH = 10;


    private String roomId;

    private Logger logger = Logger.getLogger(this.getClass());


    public ChatPanel(JPanel parent)
    {
        super(parent);
        context = this;
        currentUser = currentUserService.findAll().get(0);

        initComponents();
        initView();
        initData();
        setListeners();
    }

    private void initComponents()
    {
        messagePanel = new MessagePanel(this);
        messagePanel.setBorder(new RCBorder(RCBorder.BOTTOM, Colors.LIGHT_GRAY));
        adapter = new MessageAdapter(messageItems);
        messagePanel.getMessageListView().setAdapter(adapter);

        messageEditorPanel = new MessageEditorPanel(this);
    }


    private void initView()
    {
        this.setLayout(new GridBagLayout());
        add(messagePanel, new GBC(0, 0).setFill(GBC.BOTH).setWeight(1, 4));
        add(messageEditorPanel, new GBC(0, 1).setFill(GBC.BOTH).setWeight(1, 1));

        if (roomId == null)
        {
            messagePanel.setVisible(false);
            messageEditorPanel.setVisible(false);
        }
    }

    public static ChatPanel getContext()
    {
        return context;
    }

    public void setRoomId(String roomId)
    {
        this.roomId = roomId;
        CHAT_ROOM_OPEN_ID = roomId;
        this.room = roomService.findById(roomId);
    }


    private void initData()
    {
        if (roomId != null)
        {
            // 加载本地消息
            loadLocalHistory(); // 初次打开房间时加载历史消息

            // 从服务器获取本地最后一条消息以后的消息
            if (!remoteHistoryLoadedRooms.contains(roomId))
            {
                long startTs = messageService.findLastMessageTime(roomId) + 1;
                logger.debug("startTs = " + startTs);
                loadRemoteHistory(startTs - TIMESTAMP_8_HOURS, 0, true, false, null);
            }

            updateUnreadCount(0);
        }
    }

    private void setListeners()
    {
        messagePanel.getMessageListView().setScrollToTopListener(new RCListView.ScrollToTopListener()
        {
            @Override
            public void onScrollToTop()
            {
                // 当滚动到顶部时，继续拿前面的消息
                if (roomId != null)
                {
                    System.out.println("到顶啦");
                    List<Message> messages = messageService.findOffset(roomId, messageItems.size(), PAGE_LENGTH);

                    if (messages.size() > 0)
                    {
                        for (Message message : messages)
                        {
                            MessageItem item = new MessageItem(message, currentUser.getUserId());
                            messageItems.add(0, item);
                        }
                    }
                    // 如果本地没有拿到消息，则从服务器拿距现在一个月内的消息
                    else
                    {
                        System.out.println("到顶，本地没有拿到消息，从服务器拿距现在一个月内的消息");
                        loadMoreHistoryFromRemote(false);

                        // 数据库中没有当前房间的消，页码恢复为1
                        if (messageService.countByRoom(roomId) < 1)
                        {
                            page = 1;
                        }
                    }

                    messagePanel.getMessageListView().notifyItemRangeInserted(0, messages.size());
                }
            }
        });
    }

    /**
     * 加载本地历史消息
     */
    private void loadLocalHistory()
    {
        List<Message> messages = messageService.findByPage(roomId, messageItems.size(), PAGE_LENGTH);

        if (messages.size() > 0)
        {
            for (Message message : messages)
            {
                MessageItem item = new MessageItem(message, currentUser.getUserId());
                messageItems.add(item);
            }
        }
        // 如果本地没有拿到消息，则从服务器拿距现在一个月内的消息
        else
        {
            System.out.println("本地没有拿到消息，从服务器拿距现在一个月内的消息");
            loadMoreHistoryFromRemote(true);
        }

        messagePanel.getMessageListView().notifyDataSetChanged(false);

        if (messageItems.size() <= PAGE_LENGTH)
        {
            messagePanel.getMessageListView().setAutoScrollToBottom();
        }
    }

    /**
     * 从服务器拿更多历史消，如从本地第一条消息起一个月内的消息
     */
    private void loadMoreHistoryFromRemote(boolean firstRequest)
    {
        long firstTime = messageService.findFirstMessageTime(roomId);

        // 再从服务器拿50天前的消息
        final long[] start = {firstTime};
        long end = firstTime - TIMESTAMP_8_HOURS;
        // 数据库中没有该房间的任何消息
        if (start[0] < 0)
        {
            start[0] = System.currentTimeMillis() - (1000L * 60 * 60 * 24 * 30) - TIMESTAMP_8_HOURS;
            //end = System.currentTimeMillis() - TIMESTAMP_8_HOURS;
            end = 0;
        }
        else
        {
            start[0] = firstTime - (1000L * 60 * 60 * 24 * 30) - TIMESTAMP_8_HOURS;
        }

        // 如果是第一次打开该房间，且第一次拿到的历史消息数小于10条，则持续拿
        if (firstRequest)
        {
            RemoteHistoryReceivedListener listener = new RemoteHistoryReceivedListener()
            {
                @Override
                public void onReceived(int newMessageCount)
                {
                    // 如果一个月内没有消息，继续拿
                    if (newMessageCount < 10)
                    {
                        start[0] = start[0] - (1000L * 60 * 60 * 24 * 30) - TIMESTAMP_8_HOURS;

                        if (start[0] > (1483200000000L - TIMESTAMP_8_HOURS)) // 2017/1/1的时间
                        {
                            System.out.println("一个月内没有消息或拿到的消息少于10条，继续拿");
                            loadRemoteHistory(start[0], 0, false, firstRequest, this);
                        }
                        else
                        {
                            System.out.println("年代太久远，不拿了");
                        }
                    }
                }
            };

            loadRemoteHistory(start[0], end, false, firstRequest, listener);
        }
        // 滚动到顶部时的请求
        else
        {
            loadRemoteHistory(start[0], end, false, firstRequest, null);
        }
    }

    private void updateUnreadCount(int count)
    {
        room = roomService.findById(roomId);
        if (count < 0)
        {
            System.out.println(count);
        }
        room.setUnreadCount(count);
        room.setTotalReadCount(room.getMsgSum());
        roomService.update(room);

        // 通知UI更新未读消息数
        RoomsPanel.getContext().updateRoomItem(room.getRoomId());
    }

    /**
     * 加载远程历史记录
     */
    private void loadRemoteHistory(final long startTime, final long endTime, boolean loadUnread, boolean firstRequest, RemoteHistoryReceivedListener listener)
    {
        if (!remoteHistoryLoadedRooms.contains(roomId))
        {
            remoteHistoryLoadedRooms.add(roomId);
        }

        HttpGetTask task = new HttpGetTask();
        task.setListener(new HttpResponseListener<JSONObject>()
        {
            @Override
            public void onResult(JSONObject retJson)
            {

                try
                {
                    int newMessageCount;
                    //boolean loadUnread = (startTime != 0 && endTime == 0);
                    newMessageCount = processRoomHistoryResult(retJson, loadUnread, firstRequest);
                    System.out.println("newMessageCount = " + newMessageCount);

                    if (listener != null)
                    {
                        listener.onReceived(newMessageCount);
                    }
                }
                catch (Exception e)
                {
                    System.out.println(e);
                    e.printStackTrace();
                }
            }
        });

        String start = simpleDateFormat.format(new Date(startTime));
        String end;
        if (endTime <= 0)
        {
            // 时间不准时可能出错
            //end = getCurrentUTCTime();
            end = "";
            System.out.println(end);

        }
        else
        {
            end = simpleDateFormat.format(new Date(endTime));
        }

        String t = "c";
        if (room.getType().equals("c"))
        {
            t = "channels";
        }
        else if (room.getType().equals("d"))
        {
            t = "im";
        }
        else if (room.getType().equals("p"))
        {
            t = "groups";
        }
        String url = Launcher.HOSTNAME + "/api/v1/" + t + ".history";

        task.addHeader("X-Auth-Token", currentUser.getAuthToken());
        task.addHeader("X-User-Id", currentUser.getUserId());
        task.addRequestParam("roomId", roomId);
        task.addRequestParam("count", "1000");
        task.addRequestParam("oldest", start);
        task.addRequestParam("latest", end);
        task.addRequestParam("unreads", "true");
        task.execute(url);
    }

    public String getCurrentUTCTime()
    {
        StringBuffer utcTimebuffer = new StringBuffer();
        // 1、取得本地时间：
        Calendar cal = Calendar.getInstance();
        // 2、取得时间偏移量：
        int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);
        // 3、取得夏令时差：
        int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);
        // 4、从本地时间里扣除这些差量，即可以取得UTC时间：
        cal.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        int mSecond = cal.get(Calendar.MILLISECOND);

        utcTimebuffer.append(year).append("-")
                .append(month < 10 ? ("0" + month) : month).append("-")
                .append(day < 10 ? ("0" + day) : day);

        utcTimebuffer.append("T").append(hour < 10 ? ("0" + hour) : hour).append(":")
                .append(minute < 10 ? ("0" + minute) : minute).append(":")
                .append(second < 10 ? ("0" + second) : second).append(".");

        if (mSecond < 10)
        {
            utcTimebuffer.append("00" + mSecond).append("Z");
        }
        else if (mSecond < 100)
        {
            utcTimebuffer.append("0" + mSecond).append("Z");
        }
        else
        {
            utcTimebuffer.append(mSecond).append("Z");
        }

        return utcTimebuffer.toString();
    }

    /**
     * 处理房间加载历史消息回调
     *
     * @param jsonText
     * @param firstRequest
     * @throws JSONException
     */
    private int processRoomHistoryResult(JSONObject jsonText, boolean loadUnread, boolean firstRequest) throws JSONException, ParseException
    {
        //String roomId = jsonText.getString("id").replace(SubscriptionHelper.SEND_LOAD_UNREAD_COUNT_AND_LAST_MESSAGE, "");
        JSONArray messages = jsonText.getJSONArray("messages");

        List<Message> messageList = new ArrayList<>();
        for (int i = 0; i < messages.length(); i++)
        {
            JSONObject message = messages.getJSONObject(i);

            String messageContent = message.getString("msg");

            Message dbMessage = new Message();

            if (message.has("t"))
            {
                String t = message.getString("t");

                if (t.equals("message_pinned"))
                {
                    continue;
                }
                else if (t.equals("au") || t.equals("uj"))
                {
                    messageContent = messageContent + "加入群聊";
                    dbMessage.setSystemMessage(true);
                }
                else if (t.equals("r"))
                {
                    String creator = message.getJSONObject("u").getString("username");

                    messageContent = creator + " 更改群名称为：" + messageContent;
                    dbMessage.setSystemMessage(true);
                }
                else if (t.equals("ru"))
                {
                    messageContent = messageContent + " 被移出群聊";
                    dbMessage.setSystemMessage(true);
                }
                else if (t.equals("ul"))
                {
                    messageContent = messageContent + " 退出群聊";
                    dbMessage.setSystemMessage(true);
                }
                else if (t.equals("user-muted"))
                {
                    messageContent = messageContent + " 被禁言";
                    dbMessage.setSystemMessage(true);
                }
                else if (t.equals("user-unmuted"))
                {
                    messageContent = messageContent + " 被取消禁言";
                    dbMessage.setSystemMessage(true);
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

                    dbMessage.setSystemMessage(true);
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

                    dbMessage.setSystemMessage(true);
                }
            }


            dbMessage.setId(message.getString("_id"));
            dbMessage.setRoomId(message.getString("rid"));
            long timestamp = simpleDateFormat.parse(message.getString("ts")).getTime() + TIMESTAMP_8_HOURS;
            //String cn = cnSimpleDateFormat.format(timestamp);
            dbMessage.setTimestamp(timestamp);

            dbMessage.setSenderId(message.getJSONObject("u").getString("_id"));
            dbMessage.setSenderUsername(message.getJSONObject("u").getString("username"));
            dbMessage.setUpdatedAt(simpleDateFormat.parse(message.getString("_updatedAt")).getTime() + TIMESTAMP_8_HOURS);

            if (message.has("groupable"))
            {
                dbMessage.setGroupable(message.getBoolean("groupable"));
            }

            // 处理消息内容
            if (message.getString("msg").startsWith("[ ]("))
            {
                //dbMessage.setMessageContent(message.getString("msg").replaceAll("\\[ \\]\\(.*\\)\\s*", ""));
                messageContent = message.getString("msg").replaceAll("\\[ \\]\\(.*\\)\\s*", "");
            }
            /*else
            {
                dbMessage.setMessageContent(message.getString("msg"));
            }*/

            // 处理附件
            if (message.has("attachments") && !message.getString("msg").startsWith("[ ]("))
            {
                Object obj = message.get("attachments");
                if (!(obj instanceof JSONArray))
                {
                    continue;
                }
                JSONArray attachments = message.getJSONArray("attachments");
                for (int j = 0; j < attachments.length(); j++)
                {
                    JSONObject attachment = attachments.getJSONObject(j);
                    if (attachment.has("image_url"))
                    {
                        ImageAttachment imageAttachment = new ImageAttachment();
                        imageAttachment.setId(message.getJSONObject("file").getString("_id"));
                        imageAttachment.setTitle(attachment.getString("title"));
                        imageAttachment.setDescription(attachment.get("description").toString());
                        imageAttachment.setImageUrl(attachment.getString("image_url"));
                        imageAttachment.setImagesize(attachment.getLong("image_size"));
                        if (attachment.has("image_dimensions"))
                        {
                            imageAttachment.setWidth(attachment.getJSONObject("image_dimensions").getInt("width"));
                            imageAttachment.setHeight(attachment.getJSONObject("image_dimensions").getInt("height"));
                        }


                        //dbMessage.getImageAttachments().add(imageAttachment);
                        //dbMessage.setMessageContent("[图片]");

                        messageContent = "[图片]";

                        dbMessage.setImageAttachmentId(imageAttachment.getId());
                        imageAttachmentService.insertOrUpdate(imageAttachment);
                    }
                    else
                    {
                        FileAttachment fileAttachment = new FileAttachment();
                        fileAttachment.setId(message.getJSONObject("file").getString("_id"));
                        fileAttachment.setTitle(attachment.getString("title"));
                        fileAttachment.setDescription(attachment.getString("description"));
                        fileAttachment.setLink(attachment.getString("title_link"));
                        //dbMessage.getFileAttachments().add(fileAttachment);
                        messageContent = fileAttachment.getTitle().replace("File Uploaded:", "");

                        dbMessage.setFileAttachmentId(fileAttachment.getId());
                        fileAttachmentService.insertOrUpdate(fileAttachment);

                    }
                }
            }

            dbMessage.setMessageContent(messageContent);
            messageList.add(dbMessage);
        }

        if (messageList.size() > 0)
        {
            //messageService.insertOrUpdateAll(Realm.getDefaultInstance(), messageList);
            //int count = messageService.insertAll(messageList);

            for (Message msg : messageList)
            {
                messageService.insertOrUpdate(msg);
            }

            System.out.println("新增消息数：" + messageList.size());

            // 通知UI更新消息列表
            notifyNewMessageLoaded(loadUnread, firstRequest);


            //if (loadUnread)
            {
                updateTotalAndUnreadCount(messageList.size(), 0);
            }
        }

        return messageList.size();
    }

    /**
     * 通知有新的聊天记录添加到列表中
     *
     * @throws ParseException
     */
    private void notifyNewMessageLoaded(boolean loadUnread, boolean firstRequest) throws ParseException
    {
        if (messageItems != null)
        {

            //long utcCurr = simpleDateFormat.parse(getCurrentUTCTime()).getTime();
            // 下面这句在系统时间不准时会出错
            //long utcCurr = System.currentTimeMillis();
            long utcCurr = 9999999999999L;

            // 如果是加載未读消息，则新的消息追回到现有消息后
            if (loadUnread)
            {
                // 已有消息，追加
                if (messageItems.size() > 0)
                {
                    long from = messageItems.get(messageItems.size() - 1).getTimestamp();
                    //List<Message> messages = messageService.findBetween(realm, roomId, from + 1, utcCurr);
                    List<Message> messages = messageService.findBetween(roomId, from + 1, utcCurr);

                    for (Message message : messages)
                    {
                        if (!message.isDeleted())
                        {
                            messageItems.add(new MessageItem(message, currentUser.getUserId()));
                        }
                    }

                    //recyclerview.getAdapter().notifyDataSetChanged();
                    if (messages.size() > 0)
                    {
                        messagePanel.getMessageListView().notifyDataSetChanged(false);

                        if (page <= 2)
                        {
                            messagePanel.getMessageListView().setAutoScrollToBottom();
                        }
                    }
                }
                // 无消息,加载本地消息
                else
                {
                    loadLocalHistory();
                }
            }
            // 第一次请求该房间历史消，通常是一个月内的消息
            else if (firstRequest)
            {
                messageItems.clear();
                List<Message> messages = messageService.findOffset(roomId, messageItems.size(), PAGE_LENGTH);

                for (Message message : messages)
                {
                    if (!message.isDeleted())
                    {
                        messageItems.add(new MessageItem(message, currentUser.getUserId()));
                    }
                }

                if (messages.size() > 0)
                {
                    //Collections.sort(messageItems);
                    messagePanel.getMessageListView().notifyDataSetChanged(false);
                    messagePanel.getMessageListView().setAutoScrollToBottom();
                }
            }
        }
    }

    /**
     * 更新列表中的未读消息及消息总数
     */
    private void updateTotalAndUnreadCount(int totalAdded, int unread)
    {
        //Room room = roomService.findById(realm, roomId);

        //int total = room.getMsgSum();
        //int unreadc = room.getUnreadCount();

        if (unread < 0)
        {
            System.out.println(unread);
        }
        //roomService.updateUnreadCount(Realm.getDefaultInstance(), room, unread);
        //roomService.updateMessageSum(Realm.getDefaultInstance(), room, room.getMsgSum() + totalAdded);

        room.setUnreadCount(unread);
        room.setMsgSum(room.getMsgSum() + totalAdded);
        roomService.update(room);

        /*Intent intent = new Intent();
        intent.putExtra("roomId", roomId);
        intent.putExtra("unreadCount", 0);
        this.setResult(OPEN_CHAT_ROOM_RESULT, intent);*/
    }


    public void notifyDataSetChanged()
    {
        messageItems.clear();

        initData();
        messagePanel.setVisible(true);
        messageEditorPanel.setVisible(true);

    }

    public void addOrUpdateMessageItem()
    {
        Message message = messageService.findLastMessage(roomId);
        if (message == null || message.isDeleted())
        {
            return;
        }

        for (MessageItem msg : messageItems)
        {
            if (message.getId().equals(msg.getId()))
            {
                msg.setUpdatedAt(message.getTimestamp());
                messagePanel.getMessageListView().notifyDataSetChanged(false);
                return;
            }
        }

        MessageItem messageItem = new MessageItem(message, currentUser.getUserId());
        this.messageItems.add(messageItem);
        messagePanel.getMessageListView().notifyItemInserted(messageItems.size() - 1);
        messagePanel.getMessageListView().setAutoScrollToBottom();
        //recyclerview.scrollToPosition(recyclerview.getAdapter().getItemCount() - 1);

        updateUnreadCount(0);
    }
}

interface RemoteHistoryReceivedListener
{
    void onReceived(int newMessageCount);
}
