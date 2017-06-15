package com.rc.forms;

import com.rc.adapter.message.BaseMessageViewHolder;
import com.rc.adapter.message.MessageAdapter;
import com.rc.adapter.message.MessageRightAttachmentViewHolder;
import com.rc.adapter.message.MessageRightImageViewHolder;
import com.rc.app.Launcher;
import com.rc.components.Colors;
import com.rc.components.GBC;
import com.rc.components.RCBorder;
import com.rc.components.RCListView;
import com.rc.db.model.*;
import com.rc.db.service.*;
import com.rc.entity.FileAttachmentItem;
import com.rc.entity.ImageAttachmentItem;
import com.rc.entity.MessageItem;
import com.rc.utils.MimeTypeUtil;
import com.rc.websocket.WebSocketClient;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import tasks.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
    private MessagePanel messagePanel;
    private MessageEditorPanel messageEditorPanel;

    private static ChatPanel context;

    public static final long TIMESTAMP_8_HOURS = 28800000L;
    public static String CHAT_ROOM_OPEN_ID = "";

    // APP启动时，已加载过远程未读消息的Rooms
    private static List<String> remoteHistoryLoadedRooms = new ArrayList<>();

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
    public static List<String> uploadingOrDownloadingFiles = new ArrayList<>();


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
        messageEditorPanel.setPreferredSize(new Dimension(MainFrame.DEFAULT_WIDTH, MainFrame.DEFAULT_WIDTH / 4));
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

        JTextPane editor = messageEditorPanel.getEditor();
        Document document = editor.getDocument();

        editor.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    try
                    {
                        document.insertString(document.getLength(), "\n", null);
                    }
                    catch (BadLocationException e1)
                    {
                        e1.printStackTrace();
                    }
                }
                else if (!e.isControlDown() && e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    if (editor.getText() == null || editor.getText().isEmpty())
                    {
                        return;
                    }

                    sendTextMessage(null, editor.getText());
                    e.consume();
                }
            }
        });

        // 发送按钮
        messageEditorPanel.getSendButton().addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (editor.getText() == null || editor.getText().isEmpty())
                {
                    return;
                }
                sendTextMessage(null, editor.getText());
            }
        });

        // 上传文件按钮
        messageEditorPanel.getUploadFileLabel().addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("请选择上传文件或图片");
                fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

                fileChooser.showDialog(MainFrame.getContext(), "上传");
                File selectedFile = fileChooser.getSelectedFile();

                System.out.println(selectedFile.getAbsolutePath());

                String path = selectedFile.getAbsolutePath();
                sendFileMessage(path);
                showSendingMessage();

                super.mouseClicked(e);
            }
        });


    }

    /**
     * 设置当前打开的房间ID
     *
     * @param roomId
     */
    public void setRoomId(String roomId)
    {
        this.roomId = roomId;
        CHAT_ROOM_OPEN_ID = roomId;
        this.room = roomService.findById(roomId);
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

    /**
     * 更新数据库中的房间未读消息数，以及房间列表中的未读消息数
     *
     * @param count
     */
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
        if (unread < 0)
        {
            System.out.println(unread);
        }
        room.setUnreadCount(unread);
        room.setMsgSum(room.getMsgSum() + totalAdded);
        roomService.update(room);
    }


    /**
     * 通知数据改变，需要重绘整个列表
     */
    public void notifyDataSetChanged()
    {
        messageItems.clear();

        initData();
        messagePanel.setVisible(true);
        messageEditorPanel.setVisible(true);
    }

    /**
     * 添加一条消息到最后，或者更新已有消息
     */
    public void addOrUpdateMessageItem()
    {
        Message message = messageService.findLastMessage(roomId);
        if (message == null || message.isDeleted())
        {
            return;
        }

        int pos = findMessageItemReverse(message.getId());
        if (pos > -1)
        {
            messageItems.get(pos).setUpdatedAt(message.getTimestamp());
            messagePanel.getMessageListView().notifyItemChanged(pos);
            updateUnreadCount(0);
            return;
        }


        MessageItem messageItem = new MessageItem(message, currentUser.getUserId());
        this.messageItems.add(messageItem);
        messagePanel.getMessageListView().notifyItemInserted(messageItems.size() - 1, false);

        // 只有当滚动条在最底部最，新消到来后才自动滚动到底部
        JScrollBar scrollBar = messagePanel.getMessageListView().getVerticalScrollBar();
        if (scrollBar.getValue() == (scrollBar.getModel().getMaximum() - scrollBar.getModel().getExtent()))
        {
            messagePanel.getMessageListView().setAutoScrollToBottom();
        }

        updateUnreadCount(0);
    }

    /**
     * 添加一条消息到消息列表最后
     *
     * @param item
     */
    private void addMessageItemToEnd(MessageItem item)
    {
        this.messageItems.add(item);
        messagePanel.getMessageListView().notifyItemInserted(messageItems.size() - 1, true);
        messagePanel.getMessageListView().setAutoScrollToBottom();

    }


    /**
     * 发送文本消息
     * <p>
     * 如果messageId不为null, 则认为重发该消息，否则发送一条新的消息
     */
    public void sendTextMessage(String messageId, String content)
    {
        //String content = null;
        Message dbMessage = null;
        if (messageId == null)
        {
            MessageItem item = new MessageItem();
            //content = editText.getText().toString();
            if (content == null || content.equals(""))
            {
                return;
            }

            messageId = randomMessageId();
            item.setMessageContent(content);
            //item.setTimestamp(System.currentTimeMillis() - TIMESTAMP_8_HOURS);
            item.setTimestamp(System.currentTimeMillis());
            item.setSenderId(currentUser.getUserId());
            item.setSenderUsername(currentUser.getUsername());
            item.setId(messageId);
            item.setMessageType(MessageItem.RIGHT_TEXT);

            dbMessage = new Message();
            dbMessage.setId(messageId);
            dbMessage.setMessageContent(content);
            dbMessage.setRoomId(roomId);
            dbMessage.setSenderId(currentUser.getUserId());
            dbMessage.setSenderUsername(currentUser.getUsername());
            dbMessage.setTimestamp(item.getTimestamp());
            dbMessage.setNeedToResend(false);

            //mAdapter.addItem(item);
            //recyclerview.smoothScrollToPosition(mAdapter.getItemCount() - 1);
            addMessageItemToEnd(item);

            messageEditorPanel.getEditor().setText("");
            //messageService.insertOrUpdate(Realm.getDefaultInstance(), dbMessage);

            messageService.insert(dbMessage);

        }
        // 已有消息重发
        else
        {
            //dbMessage = messageService.findById(realm, messageId);
            Message msg = messageService.findById(messageId);

            //Message msg = realm.copyFromRealm(dbMessage);
            //msg.setTimestamp(System.currentTimeMillis() - TIMESTAMP_8_HOURS);
            msg.setTimestamp(System.currentTimeMillis());
            msg.setUpdatedAt(0);
            msg.setNeedToResend(false);
            //messageService.insertOrUpdate(realm, msg);
            messageService.insertOrUpdate(msg);

            content = msg.getMessageContent();

            int pos = findMessageItemReverse(msg.getId());
            if (pos > -1)
            {
                messageItems.get(pos).setNeedToResend(false);
                messageItems.get(pos).setUpdatedAt(0);
                messageItems.get(pos).setTimestamp(System.currentTimeMillis());
                messagePanel.getMessageListView().notifyItemChanged(pos);
            }

        }

        // 发送
        WebSocketClient.getContext().sendTextMessage(roomId, messageId, content);


        MessageResendTask task = new MessageResendTask();
        task.setListener(new ResendTaskCallback(10000)
        {
            @Override
            public void onNeedResend(String messageId)
            {
                //Realm realm = Realm.getDefaultInstance();
                //Message msg = messageService.findById(realm, messageId);
                Message msg = messageService.findById(messageId);
                if (msg.getUpdatedAt() == 0)
                {
                    // 更新消息列表
                    int pos = findMessageItemReverse(messageId);
                    if (pos > -1)
                    {
                        messageItems.get(pos).setNeedToResend(true);
                        msg.setNeedToResend(true);
                        messageService.update(msg);
                        messagePanel.getMessageListView().notifyItemChanged(pos);
                    }


                    // 更新房间列表
                    // 注意这里不能用类的成员room，因为可能已经离开了原来的房间
                    Room room = roomService.findById(msg.getRoomId());
                    room.setLastMessage("[有消息发送失败]");
                    room.setLastChatAt(msg.getTimestamp());
                    roomService.update(room);
                    RoomsPanel.getContext().updateRoomItem(msg.getRoomId());
                }
            }
        });
        task.execute(messageId);

        // 显示正在发送...
        //showSendingMessage();
    }

    private void showSendingMessage()
    {
        Room room = roomService.findById(roomId);
        room.setLastMessage("[发送中...]");
        roomService.update(room);
        RoomsPanel.getContext().updateRoomItem(roomId);
    }

    /**
     * 倒序查找指定的消息在messageItems中的位置
     *
     * @param messageId
     * @return 查找成功，返回该消息在messageItems中的位置，否则返回-1
     */
    private int findMessageItemReverse(String messageId)
    {
        for (int i = messageItems.size() - 1; i >= 0; i--)
        {
            // 找到消息列表中对应的消息
            if (messageItems.get(i).getId().equals(messageId))
            {
                return i;
            }
        }

        return -1;
    }


    /**
     * 随机生成MessageId
     *
     * @return
     */
    private String randomMessageId()
    {
        String raw = UUID.randomUUID().toString().replace("-", "");
        return raw;
    }

    /**
     * 发送文件消息
     *
     * @param path
     */
    private void sendFileMessage(String path)
    {
        WebSocketClient.getContext().sendFileMessage(roomId, path);
    }

    /**
     * 重发文件消息
     *
     * @param messageId
     * @param type
     */
    public void resendFileMessage(String messageId, String type)
    {
        //Message dbMessage = messageService.findById(realm, messageId);
        Message dbMessage = messageService.findById(messageId);
        String path = null;

        if (type.equals("file"))
        {
            if (dbMessage.getFileAttachmentId() != null)
            {
                //path = dbMessage.getFileAttachments().get(0).getLink();
                path = fileAttachmentService.findById(dbMessage.getFileAttachmentId()).getLink();
            }
            else
            {
                path = null;
            }
        }
        else
        {
            if (dbMessage.getImageAttachmentId() != null)
            {
                //path = dbMessage.getImageAttachments().get(0).getImageUrl();
                path = imageAttachmentService.findById(dbMessage.getImageAttachmentId()).getImageUrl();
            }
            else
            {
                path = null;

            }
        }


        if (path != null)
        {
            /*int index = -1;
            for (int i = 0; i < messageItems.size(); i++)
            {
                if (messageItems.get(i).getId().equals(messageId))
                {
                    index = i;
                    break;
                }
            }*/

            int index = findMessageItemReverse(messageId);

            if (index > -1)
            {
                messageItems.remove(index);
                messagePanel.getMessageListView().notifyItemRemoved(index);
                messageService.delete(dbMessage.getId());
            }

            sendFileMessage(path);
            showSendingMessage();
        }
    }

    /**
     * 通知开始上传文件
     *
     * @param url
     * @param uploadFilename
     * @param fileId
     * @param token
     */
    public void notifyStartUploadFile(String url, String uploadFilename, String fileId, String token)
    {
        uploadFile(url, uploadFilename, fileId, token);
        uploadingOrDownloadingFiles.add(fileId);
    }

    /**
     * 上传文件
     *
     * @param url
     * @param uploadFilename
     * @param fileId
     * @param token
     */
    private void uploadFile(String url, String uploadFilename, final String fileId, final String token)
    {
        final MessageItem item = new MessageItem();
        String type = MimeTypeUtil.getMime(uploadFilename.substring(uploadFilename.lastIndexOf(".")));
        final boolean isImage = type.startsWith("image/");

        // 发送的是图片
        int[] bounds;
        String name = uploadFilename.substring(uploadFilename.lastIndexOf("/") + 1); // 文件名

        FileAttachment fileAttachment = null;
        ImageAttachment imageAttachment = null;
        Message dbMessage = new Message();
        dbMessage.setProgress(-1);

        if (isImage)
        {
            bounds = getImageSize(uploadFilename);
            imageAttachment = new ImageAttachment();
            imageAttachment.setId(fileId);
            imageAttachment.setWidth(bounds[0]);
            imageAttachment.setHeight(bounds[1]);
            imageAttachment.setImageUrl(uploadFilename);
            imageAttachment.setTitle(name);
            //item.getImageAttachments().add(new ImageAttachmentItem(imageAttachment));
            //dbMessage.getImageAttachments().add(imageAttachment);
            item.setImageAttachment(new ImageAttachmentItem(imageAttachment));
            dbMessage.setImageAttachmentId(imageAttachment.getId());
            imageAttachmentService.insertOrUpdate(imageAttachment);

            item.setMessageType(MessageItem.RIGHT_IMAGE);
        }
        else
        {

            fileAttachment = new FileAttachment();
            fileAttachment.setId(fileId);
            fileAttachment.setLink(uploadFilename);
            fileAttachment.setTitle(name);
            //item.getFileAttachments().add(new FileAttachmentItem(fileAttachment));
            //dbMessage.getFileAttachments().add(fileAttachment);
            item.setFileAttachment(new FileAttachmentItem(fileAttachment));
            dbMessage.setFileAttachmentId(fileAttachment.getId());
            fileAttachmentService.insertOrUpdate(fileAttachment);

            item.setMessageType(MessageItem.RIGHT_ATTACHMENT);
        }


        // 晢时使用fileId作为messageId
        final String messageId = fileId;
        item.setMessageContent(name);
        item.setTimestamp(System.currentTimeMillis());
        item.setSenderId(currentUser.getUserId());
        item.setSenderUsername(currentUser.getUsername());
        item.setId(messageId);
        //item.getFileAttachments().add(fileAttachment);
        item.setProgress(0);


        dbMessage.setId(messageId);
        dbMessage.setMessageContent(name);
        dbMessage.setRoomId(roomId);
        dbMessage.setSenderId(currentUser.getUserId());
        dbMessage.setSenderUsername(currentUser.getUsername());
        dbMessage.setTimestamp(item.getTimestamp());
        dbMessage.setUpdatedAt(-1L);
        //dbMessage.getFileAttachments().add(fileAttachment);

        //mAdapter.addItem(item);
        addMessageItemToEnd(item);
        //recyclerview.smoothScrollToPosition(mAdapter.getItemCount() - 1);

        //editText.setText("");
        //messageService.insertOrUpdate(Realm.getDefaultInstance(), dbMessage);
        messageService.insertOrUpdate(dbMessage);

        File file = new File(uploadFilename);
        if (!file.exists())
        {
            //throw new RuntimeException("文件不存在");
            JOptionPane.showMessageDialog(null, "文件不存在", "上传失败", JOptionPane.ERROR_MESSAGE);
        }
        else
        {
            final List<byte[]> dataParts = cuttingFile(file);
            final int[] index = {1};
//            String type = MimeTypeUtil.getMime(file.getName().substring(file.getName().lastIndexOf(".")));
//            // 发送的是图片
//            int[] bounds = getImageSize(uploadFilename);

            final int[] uploadedBlockCount = {1};
            UploadTaskCallback callback = new UploadTaskCallback()
            {
                @Override
                public void onTaskSuccess()
                {
                    // 当收到上一个分块的响应后，才能开始上传下一个分块，否则容易造成分块接收顺序错乱
                    uploadedBlockCount[0]++;
                    if (uploadedBlockCount[0] <= dataParts.size())
                    {
                        sendDataPart(uploadedBlockCount[0], dataParts, url, type, this);
                    }


                    int progress = (int) ((index[0] * 1.0f / dataParts.size()) * 100);
                    index[0]++;

                    if (progress == 100)
                    {
                        WebSocketClient.getContext().sendUfsCompleteMessage(fileId, token);
                        uploadingOrDownloadingFiles.remove(fileId);
                    }


                    for (int i = messageItems.size() - 1; i >= 0; i--)
                    {
                        if (messageItems.get(i).getId().equals(item.getId()))
                        {
                            messageItems.get(i).setProgress(progress);
                            //messageService.updateProgress(Realm.getDefaultInstance(), messageItems.get(i).getId(), progress);
                            messageService.updateProgress(messageItems.get(i).getId(), progress);


                            BaseMessageViewHolder viewHolder = getViewHolderByPosition(i);
                            if (viewHolder != null)
                            {
                                if (isImage)
                                {
                                    MessageRightImageViewHolder holder = (MessageRightImageViewHolder) viewHolder;
                                    if (progress >= 100)
                                    {
                                        holder.sendingProgress.setVisible(false);
                                    }
                                }
                                else
                                {
                                    MessageRightAttachmentViewHolder holder = (MessageRightAttachmentViewHolder) viewHolder;
                                    //Log.e("progress", messageItems.get(i).getId() + " --- position = " + i + " ---- " + progress);
                                    holder.progressBar.setVisible(true);
                                    holder.progressBar.setValue(progress);
                                    if (progress >= 100)
                                    {
                                        holder.progressBar.setVisible(false);
                                    }
                                }

                            }
                            break;
                        }
                    }

                    logger.debug("file uploading, progress = " + progress + "%");
                }

                @Override
                public void onTaskError()
                {
                }
            };

            sendDataPart(uploadedBlockCount[0], dataParts, url, type, callback);
        }
    }

    private void sendDataPart(int partIndex, List<byte[]> dataParts, String baseUploadUrl, String type, UploadTaskCallback callback)
    {
        logger.debug("发送第" + partIndex + "个分块，共" + dataParts.size() + "个分块");
        float uploadProgress = partIndex * 1.0f / dataParts.size();
        String uploadUrl = baseUploadUrl + "&?progress=" + uploadProgress;
        new UploadTask(callback).execute(uploadUrl, type, dataParts.get(partIndex - 1));
    }

    private int[] getImageSize(String file)
    {
        try
        {
            BufferedImage image = ImageIO.read(new File(file));
            int width = image.getWidth();
            int height = image.getHeight();
            return new int[]{width, height};
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return new int[]{0, 0};
    }

    private static List<byte[]> cuttingFile(File file)
    {
        long size = file.length();

        //int partSize = 512000;
        int partSize = 4140;
        int blockCount;
        blockCount = (int) (size % partSize == 0 ? size / partSize : size / partSize + 1);
        List<byte[]> dataParts = new ArrayList<>(blockCount);
        try
        {
            byte[] buffer = new byte[partSize];
            int len;
            FileInputStream inputStream = new FileInputStream(file);

            while ((len = inputStream.read(buffer)) > -1)
            {
                byte[] dataPart = Arrays.copyOf(buffer, len);
                dataParts.add(dataPart);
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return dataParts;
    }

    private BaseMessageViewHolder getViewHolderByPosition(int position)
    {
        return (BaseMessageViewHolder) messagePanel.getMessageListView().getItem(position);
    }
}

interface RemoteHistoryReceivedListener
{
    void onReceived(int newMessageCount);
}
