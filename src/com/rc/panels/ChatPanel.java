package com.rc.panels;

import com.rc.adapter.message.*;
import com.rc.app.Launcher;
import com.rc.app.Toast;
import com.rc.components.Colors;
import com.rc.components.GBC;
import com.rc.components.RCBorder;
import com.rc.components.RCListView;
import com.rc.components.message.FileEditorThumbnail;
import com.rc.components.message.RemindUserPopup;
import com.rc.db.model.*;
import com.rc.db.service.*;
import com.rc.entity.FileAttachmentItem;
import com.rc.entity.ImageAttachmentItem;
import com.rc.entity.MessageItem;
import com.rc.frames.MainFrame;
import com.rc.helper.MessageViewHolderCacheHelper;
import com.rc.listener.ExpressionListener;
import com.rc.utils.*;
import com.rc.websocket.WebSocketClient;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.rc.tasks.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.*;
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
 * Created by song on 17-5-30.
 *
 * <p>下图 #ChatPanel# 对应的位置</p>
 *
 * 显示聊天列表
 *
 * <P>推荐使用Menlo或Consolas字体</P>
 * ┌────────────────────────┬────────────────────────────────────────────────────────┐
 * │ ┌─────┐                │  Room Title                                         ≡  │
 * │ │     │ name         ≡ ├────────────────────────────────────────────────────────┤
 * │ └─────┘                │                                                        │
 * ├────────────────────────┤                                                        │
 * │    search              │                                                        │
 * ├────────────────────────┤                                                        │
 * │  ▆    │    ▆   │   ▆   │                                                        │
 * ├────────────────────────┤                                                        │
 * │ ┌──┐ name         14:01│                      #ChatPanel#                       │
 * │ └──┘ message        99+│                                                        │
 * ├────────────────────────┤                                                        │
 * │                        │                                                        │
 * │                        │                                                        │
 * │                        │                                                        │
 * │          Room          │                                                        │
 * │                        ├────────────────────────────────────────────────────────┤
 * │                        │  ▆   ▆   ▆                                             │
 * │          List          │                                                        │
 * │                        │                                                        │
 * │                        │                                                ┌─────┐ │
 * │                        │                                                └─────┘ │
 * └────────────────────────┴────────────────────────────────────────────────────────┘
 */
public class ChatPanel extends ParentAvailablePanel
{
    private MessagePanel messagePanel;
    private MessageEditorPanel messageEditorPanel;

    private JPanel topPanel;
    private JPanel bottomPanel;
    private JSplitPane splitPane;


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

    private long firstMessageTimestamp = 0L; // 如果是从消息搜索列表中进入房间的，这个属性不为0

    // 房间的用户
    public List<String> roomMembers = new ArrayList<>();


    private MessageService messageService = Launcher.messageService;
    private CurrentUserService currentUserService = Launcher.currentUserService;
    private RoomService roomService = Launcher.roomService;
    private ImageAttachmentService imageAttachmentService = Launcher.imageAttachmentService;
    private FileAttachmentService fileAttachmentService = Launcher.fileAttachmentService;
    public static List<String> uploadingOrDownloadingFiles = new ArrayList<>();
    private FileCache fileCache;


    // 每次加载的消息条数
    private static final int PAGE_LENGTH = 5;


    private String roomId;

    private Logger logger = Logger.getLogger(this.getClass());

    private List<String> remoteRoomMemberLoadedRooms = new ArrayList<>();
    private RemindUserPopup remindUserPopup = new RemindUserPopup();

    private MessageViewHolderCacheHelper messageViewHolderCacheHelper;


    private static final int MAX_SHARE_ATTACHMENT_UPLOAD_COUNT = 1024;

    /**
     * 待上传的外部分享附件队列
     */
    private Queue<String> shareAttachmentUploadQueue = new ArrayDeque<>(MAX_SHARE_ATTACHMENT_UPLOAD_COUNT);
    private com.apple.eawt.Application app = null;
    private Toast newMessageToast;
    private boolean enterRoomJustNow = false;


    public ChatPanel(JPanel parent)
    {

        super(parent);
        context = this;
        currentUser = currentUserService.findAll().get(0);
        messageViewHolderCacheHelper = new MessageViewHolderCacheHelper();

        initComponents();
        initView();
        setListeners();

        initData();

        fileCache = new FileCache();

        if (OSUtil.getOsType() == OSUtil.Mac_OS)
        {
            app = com.apple.eawt.Application.getApplication();
        }
    }

    private void initComponents()
    {
        messagePanel = new MessagePanel(this);
        messagePanel.setBorder(new RCBorder(RCBorder.BOTTOM, Colors.LIGHT_GRAY));
        adapter = new MessageAdapter(messageItems, messagePanel.getMessageListView(), messageViewHolderCacheHelper);
        messagePanel.getMessageListView().setAdapter(adapter);

        messageEditorPanel = new MessageEditorPanel(this);
        messageEditorPanel.setPreferredSize(new Dimension(MainFrame.DEFAULT_WIDTH, MainFrame.DEFAULT_WIDTH / 4));

        topPanel = new JPanel();
        topPanel.setBorder(null);
        topPanel.setLayout(new GridBagLayout());
        topPanel.add(messagePanel, new GBC(0, 0).setFill(GBC.BOTH).setWeight(1, 1));

        bottomPanel = new JPanel();
        bottomPanel.setBorder(null);
        bottomPanel.setLayout(new GridBagLayout());
        bottomPanel.add(messageEditorPanel, new GBC(0, 0).setFill(GBC.BOTH).setWeight(1, 1));


        UIDefaults defaults = UIManager.getDefaults();
        defaults.remove("SplitPane.border");
        defaults.remove("SplitPaneDivider.border");

        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
        splitPane.setBorder(null);
        splitPane.setDividerLocation(400);
        splitPane.setDividerSize(2);
        splitPane.setLeftComponent(topPanel);
        splitPane.setRightComponent(bottomPanel);
    }


    private void initView()
    {
        /*this.setLayout(new GridBagLayout());
        add(messagePanel, new GBC(0, 0).setFill(GBC.BOTH).setWeight(1, 4));
        add(messageEditorPanel, new GBC(0, 1).setFill(GBC.BOTH).setWeight(1, 1));*/

        this.setLayout(new GridBagLayout());
        add(splitPane, new GBC(0, 0).setFill(GBC.BOTH).setWeight(1, 1));

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
            // 如果是从搜索列表进入房间的
            if (firstMessageTimestamp != 0L)
            {
                loadMessageWithEarliestTime(firstMessageTimestamp);
            }
            else
            {
                // 加载本地消息
                loadLocalHistory(); // 初次打开房间时加载历史消息

                // 从服务器获取本地最后一条消息以后的消息
                if (!remoteHistoryLoadedRooms.contains(roomId))
                {
                    long startTs = messageService.findLastMessageTime(roomId) + 1;
                    logger.debug("startTs = " + startTs);
                    loadRemoteHistory(startTs - TIMESTAMP_8_HOURS, 0, true, false, new RemoteHistoryReceivedListener()
                    {
                        @Override
                        public void onReceived(int newMessageCount)
                        {
                            if (newMessageCount >= 10)
                            {

                            }
                        }
                    });
                }
            }

            //updateUnreadCount(0);
        }
    }

    private void initNewMessageToast()
    {
        newMessageToast = new Toast(MainFrame.getContext(), "新未读消息");
        newMessageToast.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                messagePanel.getMessageListView().scrollToBottom();
                newMessageToast.setVisible(false);

                super.mouseClicked(e);
            }
        });
    }

    private void setListeners()
    {
        messagePanel.getMessageListView().setScrollListener(new RCListView.ScrollListener()
        {
            @Override
            public void onScrollToTop()
            {
                // 当滚动到顶部时，继续拿前面的消息
                if (roomId != null)
                {
                    // 在刚刚进入房间时，会触发onScrollToTop, 导致多加载一次消息，因此设置enterRoomJustNow标记阻止这次加载
                    // 在第一次加载完本地消息后，会设置enterRoomJustNow = false;避免在后面滚动到顶部时无法加载消息的问题
                    if (enterRoomJustNow)
                    {
                        enterRoomJustNow = false;
                        return;
                    }

                    List<Message> messages = messageService.findOffset(roomId, messageItems.size(), PAGE_LENGTH);

                    if (messages.size() > 0)
                    {
                        for (int i = messages.size() - 1; i >= 0; i--)
                        {
                            MessageItem item = new MessageItem(messages.get(i), currentUser.getUserId());
                            messageItems.add(0, item);
                        }
                    }
                    // 如果本地没有拿到消息，则从服务器拿距现在一个月内的消息
                    else
                    {
                        System.out.println("到顶，本地没有拿到消息，从服务器拿距现在一个月内的消息");
                        loadMoreHistoryFromRemote(false);
                    }

                    messagePanel.getMessageListView().notifyItemRangeInserted(0, messages.size());
                }
            }

            @Override
            public void onScrollToBottom()
            {
                if (newMessageToast != null)
                {
                    newMessageToast.setVisible(false);
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
                // CTRL + 回车换行
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    try
                    {
                        document.insertString(editor.getCaretPosition(), "\n", null);
                    }
                    catch (BadLocationException e1)
                    {
                        e1.printStackTrace();
                    }
                }

                // 回车发送消息
                else if (!e.isControlDown() && e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    sendMessage();
                    e.consume();
                }

                // 输入@，弹出选择用户菜单
                else if (e.getKeyChar() == '@')
                {
                    Point point = editor.getCaret().getMagicCaretPosition();
                    point = point == null ? new Point(10, 0) : point;
                    List<String> users = exceptSelfFromRoomMember();
                    users.add(0, "all");
                    remindUserPopup.setUsers(users);
                    remindUserPopup.show((Component) e.getSource(), point.x, point.y, roomId);
                }

                // 输入退格键，删除最后一个@user
                else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
                {
                    String str = editor.getText();
                    if (str.matches(".*@\\w+\\s"))
                    {
                        try
                        {
                            int startPos = str.lastIndexOf("@");
                            String rmStr = str.substring(startPos);
                            editor.getDocument().remove(startPos + 1, rmStr.length() - 1);
                        }
                        catch (BadLocationException e1)
                        {
                            e1.printStackTrace();
                        }
                    }
                }
            }

        });

        remindUserPopup.setSelectedCallBack(new RemindUserPopup.UserSelectedCallBack()
        {
            @Override
            public void onSelected(String username)
            {
                JTextPane editor = messageEditorPanel.getEditor();
                editor.replaceSelection(username + " ");
            }
        });

        // 发送按钮
        messageEditorPanel.getSendButton().addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                sendMessage();
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

                if (fileChooser.showDialog(MainFrame.getContext(), "上传") == JFileChooser.APPROVE_OPTION)
                {
                    File selectedFile = fileChooser.getSelectedFile();
                    if (selectedFile != null)
                    {
                        String path = selectedFile.getAbsolutePath();
                        sendFileMessage(path);
                        showSendingMessage();
                    }
                }

                super.mouseClicked(e);
            }
        });

        // 插入表情
        messageEditorPanel.setExpressionListener(new ExpressionListener()
        {
            @Override
            public void onSelected(String code)
            {
                if (isCustomExpression(code))
                {
                    sendTextMessage(null, code);
                }
                else
                {
                    editor.replaceSelection(code);
                }
            }
        });
    }

    /**
     * 判断是否是自定义表情（即非emoji表情）
     *
     * @param code
     * @return
     */
    private boolean isCustomExpression(String code)
    {
        return code.matches(" :\\w+: ");
    }


    /**
     * 解析输入框中的内容并发送消息
     */
    private void sendMessage()
    {
        List<Object> inputDatas = parseEditorInput();
        boolean isImageOrFile = false;
        for (Object data : inputDatas)
        {
            if (data instanceof String && !data.equals("\n"))
            {
                sendTextMessage(null, (String) data);
            }
            else if (data instanceof JLabel)
            {
                isImageOrFile = true;
                JLabel label = (JLabel) data;
                ImageIcon icon = (ImageIcon) label.getIcon();
                String path = icon.getDescription();
                if (path != null && !path.isEmpty())
                {
                    /*sendFileMessage(path);
                    showSendingMessage();*/

                    shareAttachmentUploadQueue.add(path);
                }
            }
            else if (data instanceof FileEditorThumbnail)
            {
                isImageOrFile = true;

                FileEditorThumbnail component = (FileEditorThumbnail) data;
                shareAttachmentUploadQueue.add(component.getPath());

            }
        }

        if (isImageOrFile)
        {
            // 先上传第一个图片/文件
            dequeueAndUpload();
        }

        messageEditorPanel.getEditor().setText("");
    }

    /**
     * 解析输入框中的输入数据
     *
     * @return
     */
    private List<Object> parseEditorInput()
    {
        List<Object> inputData = new ArrayList<>();

        Document doc = messageEditorPanel.getEditor().getDocument();
        int count = doc.getRootElements()[0].getElementCount();

        // 是否是纯文本，如果发现有图片或附件，则不是纯文本
        boolean pureText = true;

        for (int i = 0; i < count; i++)
        {
            Element root = doc.getRootElements()[0].getElement(i);

            int elemCount = root.getElementCount();

            for (int j = 0; j < elemCount; j++)
            {
                try
                {
                    Element elem = root.getElement(j);
                    String elemName = elem.getName();
                    switch (elemName)
                    {
                        case "content":
                        {
                            int start = elem.getStartOffset();
                            int end = elem.getEndOffset();
                            String text = doc.getText(elem.getStartOffset(), end - start);
                            inputData.add(text);
                            break;
                        }
                        case "component":
                        {
                            pureText = false;
                            Component component = StyleConstants.getComponent(elem.getAttributes());
                            inputData.add(component);
                            break;
                        }
                        case "icon":
                        {
                            pureText = false;

                            ImageIcon icon = (ImageIcon) StyleConstants.getIcon(elem.getAttributes());
                            inputData.add(icon);
                            break;
                        }
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

        // 如果是纯文本，直接返回整个文本，否则如果出消息中有换行符\n出现，那么每一行都会被解析成一句话，会造成一条消息被分散成多个消息发送
        if (pureText)
        {
            inputData.clear();
            inputData.add(messageEditorPanel.getEditor().getText());
        }

        return inputData;
    }

    /**
     * 从待上传附件队列中出队一个，并上传
     */
    public synchronized void dequeueAndUpload()
    {
        String path = shareAttachmentUploadQueue.poll();

        if (path != null)
        {
            System.out.println("上传文件：" + path);

            sendFileMessage(path);
            showSendingMessage();
        }
    }

    /**
     * @return
     */
    private List<String> exceptSelfFromRoomMember()
    {
        List<String> users = new ArrayList<>();
        users.addAll(roomMembers);
        users.remove(currentUser.getUsername());
        return users;
    }

    /**
     * 进入指定房间
     *
     * @param roomId
     * @param firstMessageTimestamp
     */
    public void enterRoom(String roomId, long firstMessageTimestamp)
    {
        if (roomId == null || roomId.isEmpty())
        {
            return;
        }

        enterRoomJustNow = true;


        this.firstMessageTimestamp = firstMessageTimestamp;

        this.roomId = roomId;
        CHAT_ROOM_OPEN_ID = roomId;
        this.room = roomService.findById(roomId);

        // 更新消息列表
        this.notifyDataSetChanged();

        // 更新房间标题，尤其是成员数
        updateRoomTitle();

        sendReadMessage();


        RoomMembersPanel.getContext().setRoomId(roomId);

        messageEditorPanel.getEditor().setText("");

        // 更新当前房间的未读消息数为0
        updateUnreadCount(0);

        // 更新总未读消息数
        updateTotalUnreadCount();


    }

    /**
     * 更新总未读消息数
     */
    public void updateTotalUnreadCount()
    {
        if (OSUtil.getOsType() == OSUtil.Mac_OS && app != null)
        {
            int unreadCount = roomService.totalUnreadCount();

            if (unreadCount <= 0)
            {
                app.setDockIconBadge("");
            }
            else if (unreadCount > 99)
            {
                app.setDockIconBadge("99+");
            }
            else
            {
                app.setDockIconBadge(unreadCount + "");
            }
        }
    }

    /**
     * 进入指定房间
     *
     * @param roomId
     */
    public void enterRoom(String roomId)
    {
        enterRoom(roomId, 0L);
    }

    public void updateRoomTitle()
    {
        String title = room.getName();
        if (!room.getType().equals("d"))
        {
            // 加载本地群成员
            loadLocalRoomMembers();

            // 远程获取群成员
            if (!remoteRoomMemberLoadedRooms.contains(roomId))
            {
                loadRemoteRoomMembers();
            }

            title += " (" + (roomMembers.size()) + ")";
        }


        // 更新房间标题
        TitlePanel.getContext().updateRoomTitle(title);
    }


    /**
     * 加载指定 firstMessageTimestamp 以后的消息
     *
     * @param firstMessageTimestamp
     */
    private void loadMessageWithEarliestTime(long firstMessageTimestamp)
    {
        List<Message> messages = messageService.findBetween(roomId, firstMessageTimestamp, System.currentTimeMillis());
        if (messages.size() > 0)
        {
            for (Message message : messages)
            {
                if (!message.isDeleted())
                {
                    MessageItem item = new MessageItem(message, currentUser.getUserId());
                    this.messageItems.add(item);
                }
            }

            messagePanel.getMessageListView().notifyDataSetChanged(false);
            messagePanel.getMessageListView().setAutoScrollToTop();
        }

        // 从服务器获取本地最后一条消息以后的消息
        if (!remoteHistoryLoadedRooms.contains(roomId))
        {
            long startTs = messageService.findLastMessageTime(roomId) + 1;
            logger.debug("startTs = " + startTs);
            loadRemoteHistory(startTs - TIMESTAMP_8_HOURS, 0, false, false, null);
        }
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
        enterRoomJustNow = false;

        //if (messageItems.size() <= PAGE_LENGTH)
        {
            messagePanel.getMessageListView().setAutoScrollToBottom();
        }

    }

    private long loadRemoteStartTime = 0;

    /**
     * 从服务器拿更多历史消息，如从本地第一条消息起一个月内的消息
     */
    private void loadMoreHistoryFromRemote(boolean firstRequest)
    {
        long firstTime = messageService.findFirstMessageTime(roomId);

        // 再从服务器拿30天前的消息
        loadRemoteStartTime = firstTime;

        long end = firstTime - TIMESTAMP_8_HOURS;


        // 数据库中没有该房间的任何消息
        if (loadRemoteStartTime < 0)
        {
            loadRemoteStartTime = System.currentTimeMillis() - (1000L * 60 * 60 * 24 * 30) - TIMESTAMP_8_HOURS;
            //end = System.currentTimeMillis() - TIMESTAMP_8_HOURS;
            end = 0;
        }
        else
        {
            loadRemoteStartTime = firstTime - (1000L * 60 * 60 * 24 * 30) - TIMESTAMP_8_HOURS;
        }

        // 如果是第一次打开该房间，且第一次拿到的历史消息数小于10条，则持续拿
        if (firstRequest)
        {
            RemoteHistoryReceivedListener listener = new RemoteHistoryReceivedListener()
            {
                @Override
                public void onReceived(int newMessageCount)
                {
                    Room room = roomService.findById(roomId);

                    // 总消息数小于10，继续拿
                    if (messageService.countByRoom(room.getRoomId()) < 10)
                    {
                        long lastStartTime = loadRemoteStartTime - 1;
                        loadRemoteStartTime = loadRemoteStartTime - (1000L * 60 * 60 * 24 * 30) - TIMESTAMP_8_HOURS;

                        if (loadRemoteStartTime > (1483200000000L - TIMESTAMP_8_HOURS)) // 2017/1/1的时间
                        {
                            System.out.println("一个月内没有消息或拿到的消息少于10条，继续拿");
                            loadRemoteHistory(loadRemoteStartTime, lastStartTime, false, firstRequest, this);

                            //System.out.println("start = " + loadRemoteStartTime + ",  " + room.getCreatedAt());
                        }
                        else
                        {
                            System.out.println("距离现在时间过长，放弃获取历史消息");
                        }
                    }
                }
            };

            loadRemoteHistory(loadRemoteStartTime, end, false, firstRequest, listener);
        }
        // 滚动到顶部时的请求
        else
        {
            loadRemoteHistory(loadRemoteStartTime, end, false, firstRequest, null);
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

        if (room == null)
        {
            RoomsPanel.getContext().notifyDataSetChanged(true);
            return;
        }
        room.setUnreadCount(count);
        room.setTotalReadCount(room.getMsgSum());
        roomService.update(room);

        // 通知UI更新未读消息数
        RoomsPanel.getContext().updateRoomItem(room.getRoomId());
    }


    /**
     * 加载远程历史记录
     *
     * @param startTime    最早一条消息的时间
     * @param endTime      最后一条消息的时间
     * @param loadUnread   是否是加载未读消息，如果loadUnread = true，加载的消息会追加到现有消息列表后面，并滚动到底部。
     * @param firstRequest 是否是第一次加载，即加载第一页的消息，firstRequest = true，会清空消息列表已有的消息，只从数据库获取 {@linkplain this#PAGE_LENGTH}条消息,
     *                     如果firstRequest = true且本地总消息数小于10条，则会继续从服务器中获取历史消，直到时间早于2017/1/1
     * @param listener     远程加载完成后的回调，可选
     */
    private void loadRemoteHistory(final long startTime, final long endTime, boolean loadUnread, boolean firstRequest, RemoteHistoryReceivedListener listener)
    {
        TitlePanel.getContext().showStatusLabel("加载中...");
        if (!remoteHistoryLoadedRooms.contains(roomId))
        {
            remoteHistoryLoadedRooms.add(roomId);
        }

        HttpGetTask task = new HttpGetTask();
        task.setListener(new HttpResponseListener<JSONObject>()
        {
            @Override
            public void onSuccess(JSONObject retJson)
            {
                try
                {
                    int newMessageCount;
                    //boolean loadUnread = (startTime != 0 && endTime == 0);
                    newMessageCount = processRoomHistoryResult(retJson, loadUnread, firstRequest, startTime);
                    if (newMessageCount > 0)
                    {
                        System.out.println("newMessageCount = " + newMessageCount);
                    }

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

                TitlePanel.getContext().hideStatusLabel();
            }

            @Override
            public void onFailed()
            {
                System.out.println("消息获取失败：" + room.getName());
            }
        });

        String start = simpleDateFormat.format(new Date(startTime));
        String end;
        if (endTime <= 0)
        {
            // 时间不准时可能出错
            //end = getCurrentUTCTime();
            end = "";
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
     * @param startTime
     * @throws JSONException
     */
    private int processRoomHistoryResult(JSONObject jsonText, boolean loadUnread, boolean firstRequest, long startTime) throws JSONException, ParseException
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

            // 处理附件
            if (message.has("attachments") && message.get("attachments") instanceof JSONArray
                    && message.has("file")
                    && !message.getString("msg").startsWith("[ ]("))
            {
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

                        messageContent = "[图片]";

                        dbMessage.setImageAttachmentId(imageAttachment.getId());
                        imageAttachmentService.insertOrUpdate(imageAttachment);


                        //dbMessage.getImageAttachments().add(imageAttachment);
                        //dbMessage.setMessageContent("[图片]");

                    }
                    ///////////////////
                    else if (attachment.has("title_link"))
                    {
                        FileAttachment fileAttachment = new FileAttachment();
                        fileAttachment.setId(message.getJSONObject("file").getString("_id"));
                        fileAttachment.setTitle(attachment.getString("title").substring(15));
                        fileAttachment.setDescription(attachment.getString("description"));
                        fileAttachment.setLink(attachment.getString("title_link"));
                        //dbMessage.getFileAttachments().add(fileAttachment);
                        messageContent = fileAttachment.getTitle();

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

            // 通知UI更新消息列表
            notifyNewMessageLoaded(loadUnread, firstRequest, startTime);


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
    private void notifyNewMessageLoaded(boolean loadUnread, boolean firstRequest, long startTime) throws ParseException
    {
        if (messageItems != null)
        {

            //long utcCurr = simpleDateFormat.parse(getCurrentUTCTime()).getTime();
            // 下面这句在系统时间不准时会出错
            //long utcCurr = System.currentTimeMillis();
            long utcCurr = 9999999999999L;

            // 如果是加載未读消息，则新的消息追加到现有消息后
            if (loadUnread)
            {
                // 已有消息，追加
                if (messageItems.size() > 0)
                {
                    List<Message> messages = messageService.findBetween(roomId, startTime + TIMESTAMP_8_HOURS, utcCurr);
                    int size = messages.size();
                    if (size > 5)
                    {
                        messageItems.clear();
                    }

                    /*for (Message message : messages)
                    {
                        if (!message.isDeleted() && !inMessageItems(message.getId()))
                        {
                            messageItems.add(new MessageItem(message, currentUser.getUserId()));
                        }
                    }*/

                    // 如果未读消息总数大于等于10， 只加载10条
                    int startIndex = size >= 10 ? size - 10 : 0;
                    for (int i = startIndex; i < size; i++)
                    {
                        Message message = messages.get(i);
                        if (!message.isDeleted() && !inMessageItems(message.getId()))
                        {
                            messageItems.add(new MessageItem(message, currentUser.getUserId()));
                        }
                    }


                    //recyclerview.getAdapter().notifyDataSetChanged();
                    if (messages.size() > 0)
                    {
                        messagePanel.getMessageListView().notifyDataSetChanged(false);

                        //if (page <= 2)
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

    private boolean inMessageItems(String messageId)
    {
        for (MessageItem item : messageItems)
        {
            if (item.getId().equals(messageId))
            {
                return true;
            }
        }

        return false;
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

        messagePanel.getMessageListView().setVisible(false);
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                // 重置ViewHolder缓存
                messageViewHolderCacheHelper.reset();

                //long start = System.currentTimeMillis();
                initData();
                //System.out.println("花费时间 ：" + (System.currentTimeMillis() - start));
                messagePanel.setVisible(true);
                messageEditorPanel.setVisible(true);
                messagePanel.getMessageListView().setVisible(true);

                TitlePanel.getContext().hideRoomMembersPanel();
                checkIsMuted();
            }
        }).start();
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

        // 已有消息更新状态
        int pos = findMessageItemPositionInViewReverse(message.getId());
        if (pos > -1)
        {
            messageItems.get(pos).setUpdatedAt(message.getTimestamp());

            messagePanel.getMessageListView().notifyItemChanged(pos);
            //updateUnreadCount(0);
            return;
        }

        /*for (int i = messageItems.size() - 1; i >= 0; i--)
        {
            if (messageItems.get(i).getId().equals(message.getId()))
            {
                messageItems.get(i).setUpdatedAt(message.getTimestamp());
                messagePanel.getMessageListView().notifyItemChanged(i);
                updateUnreadCount(0);
                return;
            }
        }*/


        // 插入新的消息
        MessageItem messageItem = new MessageItem(message, currentUser.getUserId());
        this.messageItems.add(messageItem);
        messagePanel.getMessageListView().notifyItemInserted(messageItems.size() - 1, false);

        // 只有当滚动条在最底部最，新消到来后才自动滚动到底部
        JScrollBar scrollBar = messagePanel.getMessageListView().getVerticalScrollBar();
        if (scrollBar.getValue() == (scrollBar.getModel().getMaximum() - scrollBar.getModel().getExtent()))
        {
            messagePanel.getMessageListView().setAutoScrollToBottom();
        }

        if (message.getFileAttachmentId() != null)
        {
            downloadFile(fileAttachmentService.findById(message.getFileAttachmentId()), message.getId());
        }

        JScrollBar messageScrollBar = messagePanel.getMessageListView().getVerticalScrollBar();
        int val = messageScrollBar.getValue();
        int max = messageScrollBar.getMaximum();
        int extent = messageScrollBar.getModel().getExtent();

        if ((max - (val + extent)) > 0)
        {
            if (newMessageToast == null)
            {
                initNewMessageToast();
            }

            newMessageToast.setVisible(true);
        }


        //updateUnreadCount(0);
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

            //messageEditorPanel.getEditor().setText("");
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

            int pos = findMessageItemPositionInViewReverse(msg.getId());
            if (pos > -1)
            {
                messageItems.get(pos).setNeedToResend(false);
                messageItems.get(pos).setUpdatedAt(0);
                messageItems.get(pos).setTimestamp(System.currentTimeMillis());
                messagePanel.getMessageListView().notifyItemChanged(pos);
            }

            /*for (int i = messageItems.size() - 1; i >= 0; i--)
            {
                if (messageItems.get(i).getId().equals(msg.getId()))
                {
                    messageItems.get(i).setNeedToResend(false);
                    messageItems.get(i).setUpdatedAt(0);
                    messageItems.get(i).setTimestamp(System.currentTimeMillis());
                    messagePanel.getMessageListView().notifyItemChanged(i);
                    break;
                }
            }*/

        }

        content = StringEscapeUtils.escapeJava(content);

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
                    /*for (int i = messageItems.size() - 1; i >= 0; i--)
                    {
                        if (messageItems.get(i).getId().equals(messageId))
                        {
                            messageItems.get(i).setNeedToResend(true);
                            msg.setNeedToResend(true);
                            messageService.update(msg);
                            messagePanel.getMessageListView().notifyItemChanged(i);
                        }
                    }*/

                    int pos = findMessageItemPositionInViewReverse(messageId);
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
     * 倒序查找指定的消息在消息列表中的位置中的位置
     *
     * @param messageId
     * @return 查找成功，返回该消息在消息列表中的位置，否则返回-1
     */
    private int findMessageItemPositionInViewReverse(String messageId)
    {
        for (int i = messageItems.size() - 1; i >= 0; i--)
        {
            // 找到消息列表中对应的消息
            if (messageItems.get(i).getId().equals(messageId))
            {
                return i;
            }
        }

        //tmpItems = null;

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
        try
        {
            WebSocketClient.getContext().sendFileMessage(roomId, path);
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(null, e.getMessage(), "文件发送失败", JOptionPane.ERROR_MESSAGE);
        }

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

            int index = findMessageItemPositionInViewReverse(messageId);

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
        System.out.println(uploadingOrDownloadingFiles);
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
        String name = uploadFilename.substring(uploadFilename.lastIndexOf(File.separator) + 1); // 文件名

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
        item.setProgress(0);


        dbMessage.setId(messageId);
        dbMessage.setMessageContent(name);
        dbMessage.setRoomId(roomId);
        dbMessage.setSenderId(currentUser.getUserId());
        dbMessage.setSenderUsername(currentUser.getUsername());
        dbMessage.setTimestamp(item.getTimestamp());
        dbMessage.setUpdatedAt(-1L);

        addMessageItemToEnd(item);


        messageService.insertOrUpdate(dbMessage);

        File file = new File(uploadFilename);
        if (!file.exists())
        {
            JOptionPane.showMessageDialog(null, "文件不存在", "上传失败", JOptionPane.ERROR_MESSAGE);
        }
        else
        {
            final List<byte[]> dataParts = cuttingFile(file);
            final int[] index = {1};

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

                    // 上传完成
                    if (progress == 100)
                    {
                        WebSocketClient.getContext().sendUfsCompleteMessage(fileId, token);
                        uploadingOrDownloadingFiles.remove(fileId);

                        if (uploadFilename.startsWith(ClipboardUtil.CLIPBOARD_TEMP_DIR))
                        {
                            File file = new File(uploadFilename);
                            file.delete();
                        }

                    }


                    for (int i = messageItems.size() - 1; i >= 0; i--)
                    {
                        if (messageItems.get(i).getId().equals(item.getId()))
                        {
                            messageItems.get(i).setProgress(progress);
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

                                    // 隐藏"等待上传"，并显示进度条
                                    holder.sizeLabel.setVisible(false);
                                    holder.progressBar.setVisible(true);
                                    holder.progressBar.setValue(progress);

                                    if (progress >= 100)
                                    {
                                        holder.progressBar.setVisible(false);
                                        holder.sizeLabel.setVisible(true);
                                        holder.sizeLabel.setText(fileCache.fileSizeString(uploadFilename));
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

    /**
     * 获取图片的宽高
     *
     * @param file
     * @return
     */
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

    /**
     * 分割大文件，分块上传
     *
     * @param file
     * @return
     */
    private static List<byte[]> cuttingFile(File file)
    {
        long size = file.length();

        int partSize = 512000;
        //int partSize = 4140;
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


    private void sendReadMessage()
    {
        WebSocketClient.getContext().sendReadMessage(roomId);
    }

    private BaseMessageViewHolder getViewHolderByPosition(int position)
    {
        if (position < 0)
        {
            return null;
        }

        try
        {
            return (BaseMessageViewHolder) messagePanel.getMessageListView().getItem(position);
        }
        catch (Exception e)
        {
            return null;
        }
    }


    /**
     * 打开文件，如果文件不存在，则下载
     *
     * @param messageId
     */
    public void downloadOrOpenFile(String messageId)
    {
        Message message = messageService.findById(messageId);
        FileAttachment fileAttachment;
        if (message == null)
        {
            // 如果没有messageId对应的message, 尝试寻找messageId对应的file attachment，因为在自己上传文件时，此时是以fileId作为临时的messageId
            fileAttachment = fileAttachmentService.findById(messageId);
        }
        else
        {
            fileAttachment = fileAttachmentService.findById(message.getFileAttachmentId());
        }

        if (fileAttachment == null)
        {
            JOptionPane.showMessageDialog(null, "无效的附件消息", "消息无效", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String filepath = fileCache.tryGetFileCache(fileAttachment.getId(), fileAttachment.getTitle());
        if (filepath == null)
        {
            // 服务器上的文件
            if (fileAttachment.getLink().startsWith("/file-upload"))
            {
                downloadFile(fileAttachment, messageId);
            }
            // 本地的文件
            else
            {
                openFileWithDefaultApplication(fileAttachment.getLink());
            }
        }
        else
        {
            openFileWithDefaultApplication(filepath);
        }
    }

    /**
     * 下载文件
     *
     * @param fileAttachment
     * @param messageId
     */
    private void downloadFile(FileAttachment fileAttachment, String messageId)
    {
        if (uploadingOrDownloadingFiles.contains(fileAttachment.getId()))
        {
            System.out.println("文件正在下载...");
            return;
        }

        uploadingOrDownloadingFiles.add(fileAttachment.getId());

        final DownloadTask task = new DownloadTask(new HttpUtil.ProgressListener()
        {
            @Override
            public void onProgress(int progress)
            {
                int pos = findMessageItemPositionInViewReverse(messageId);
                MessageAttachmentViewHolder holder = (MessageAttachmentViewHolder) getViewHolderByPosition(pos);

                //System.out.println("文件下载进度：" + progress);
                if (pos < 0 || holder == null)
                {
                    return;
                }

                if (progress >= 0 && progress < 100)
                {
                    if (holder.sizeLabel.isVisible())
                    {
                        holder.sizeLabel.setVisible(false);
                    }
                    if (!holder.progressBar.isVisible())
                    {
                        holder.progressBar.setVisible(true);
                    }

                    holder.progressBar.setValue(progress);
                }
                else if (progress >= 100)
                {
                    holder.progressBar.setVisible(false);
                    holder.sizeLabel.setVisible(true);
                }
            }
        });

        task.setListener(new HttpResponseListener<byte[]>()
        {
            @Override
            public void onSuccess(byte[] data)
            {
                //System.out.println(data);
                String path = fileCache.cacheFile(fileAttachment.getId(), fileAttachment.getTitle(), data);

                int pos = findMessageItemPositionInViewReverse(messageId);
                MessageAttachmentViewHolder holder = (MessageAttachmentViewHolder) getViewHolderByPosition(pos);

                if (pos < 0 || holder == null)
                {
                    return;
                }
                if (path == null)
                {
                    holder.sizeLabel.setVisible(true);
                    holder.sizeLabel.setText("文件获取失败");
                    holder.progressBar.setVisible(false);
                }
                else
                {
                    holder.sizeLabel.setVisible(true);
                    System.out.println("文件已缓存在 " + path);
                    holder.sizeLabel.setText(fileCache.fileSizeString(path));
                    uploadingOrDownloadingFiles.remove(fileAttachment.getId());
                }
            }

            @Override
            public void onFailed()
            {
                int pos = findMessageItemPositionInViewReverse(messageId);
                MessageAttachmentViewHolder holder = (MessageAttachmentViewHolder) getViewHolderByPosition(pos);
                holder.sizeLabel.setVisible(true);
                holder.sizeLabel.setText("文件获取失败");
                holder.progressBar.setVisible(false);
            }
        });

        String url = Launcher.HOSTNAME + fileAttachment.getLink() + "?rc_uid=" + currentUser.getUserId() + "&rc_token=" + currentUser.getAuthToken();
        task.execute(url);
    }

    /**
     * 使用默认程序打开文件
     *
     * @param path
     */
    private void openFileWithDefaultApplication(String path)
    {
        try
        {
            Desktop.getDesktop().open(new File(path));
        }
        catch (IOException e1)
        {
            JOptionPane.showMessageDialog(null, "文件打开失败，没有找到关联的应用程序", "打开失败", JOptionPane.ERROR_MESSAGE);
            e1.printStackTrace();
        }
        catch (IllegalArgumentException e2)
        {
            JOptionPane.showMessageDialog(null, "文件不存在，可能已被删除", "打开失败", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 加载本地房间用户
     */
    public void loadLocalRoomMembers()
    {
        roomMembers.clear();
        String members = room.getMember();

        if (members != null)
        {
            String[] userArr = members.split(",");
            for (int i = 0; i < userArr.length; i++)
            {
                if (!roomMembers.contains(userArr[i]))
                {
                    roomMembers.add(userArr[i]);
                }
            }
        }
    }

    /**
     * 加载远程房间用户
     */
    public void loadRemoteRoomMembers()
    {
        if (!remoteRoomMemberLoadedRooms.contains(roomId))
        {
            remoteRoomMemberLoadedRooms.add(roomId);
        }

        logger.debug("远程获取房间 " + room.getName() + " 的群成员");
        String url = null;
        String arrayName = "";
        //room = roomService.findById(roomId);
        if (room.getType().equals("c"))
        {
            url = Launcher.HOSTNAME + "/api/v1/channels.info?roomId=" + room.getRoomId();
            arrayName = "channel";
        }
        else if (room.getType().equals("p"))
        {
            url = Launcher.HOSTNAME + "/api/v1/groups.info?roomId=" + room.getRoomId();
            arrayName = "group";
        }
        else if (room.getType().equals("d"))
        {
            return;
        }

        HttpGetTask task = new HttpGetTask();
        task.addHeader("X-Auth-Token", currentUser.getAuthToken());
        task.addHeader("X-User-Id", currentUser.getUserId());

        final String finalArrayName = arrayName;
        task.setListener(new HttpResponseListener<JSONObject>()
        {
            @Override
            public void onSuccess(JSONObject retJson)
            {
                String creator = "";
                try
                {
                    boolean newUserAdded = false;
                    boolean userRemoved = false;
                    JSONObject obj = retJson.getJSONObject(finalArrayName);
                    if (obj.has("u"))
                    {
                        creator = obj.getJSONObject("u").getString("username");
                        if (!roomMembers.contains(creator))
                        {
                            roomMembers.add(creator);
                            newUserAdded = true;
                        }
                        //roomService.updateCreatorUsername(Realm.getDefaultInstance(), room.getRoomId(), creator);
                        room.setCreatorName(creator);
                        roomService.update(room);
                    }

                    JSONArray members = obj.getJSONArray("usernames");
                    List<String> memberList = new ArrayList<>();
                    for (int i = 0; i < members.length(); i++)
                    {
                        memberList.add(members.getString(i));

                        if (!roomMembers.contains(members.getString(i)))
                        {
                            roomMembers.add(members.getString(i));
                            newUserAdded = true;
                        }
                    }

                    List<String> removedList = new ArrayList<>();
                    for (String name : roomMembers)
                    {
                        if (!memberList.contains(name))
                        {
                            removedList.add(name);
                            userRemoved = true;
                        }
                    }
                    roomMembers.removeAll(removedList);


                    // 有人加入或移除时，更新本地信息
                    if (newUserAdded || userRemoved)
                    {
                        // 更新本地members
                        updateLocalDBRoomMembers(roomMembers);

                        // 更新房间名中的成员数
                        //String title = room.getName() + " (" + (roomMembers.size()) + ")";

                        // 更新房间标题
                        //TitlePanel.getContext().updateRoomTitle(title);
                        updateRoomTitle();

                        // 如果成员面板打开，则更新
                        if (RoomMembersPanel.getContext().isVisible())
                        {
                            RoomMembersPanel.getContext().updateUI();
                        }

                        // 重新生成群头像
                        System.out.println("删除原来群头像: " + room.getName());
                        //AvatarUtil.deleteGroupAvatar(room.getName());
                        AvatarUtil.createGroupAvatar(room.getName(), roomMembers.toArray(new String[]{}));
                    }

                    //roomMembers.remove(creator);


                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed()
            {
                System.out.println("成员获取失败：" + room.getName());
            }
        });

        task.execute(url);
    }

    /**
     * 更新本地数据库中的房间成员
     *
     * @param users
     */
    public void updateLocalDBRoomMembers(List<String> users)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < users.size(); i++)
        {
            sb.append(users.get(i));
            if (i < users.size() - 1)
            {
                sb.append(",");
            }
        }

        room.setMember(sb.toString());
        roomService.update(room);

        //loadLocalRoomMembers();

        remindUserPopup.reset();

        //roomService.updateMembers(Realm.getDefaultInstance(), room.getRoomId(), sb.toString());
        //room = roomService.findById(realm, room.getRoomId());
        //System.out.println(room);
    }

    /**
     * 检查是否被禁言，如果已被禁言，输入框与发送按钮不可用
     */
    public void checkIsMuted()
    {
        room = roomService.findById(roomId);
        if (room.getMuted() != null && room.getMuted().indexOf("\"" + currentUser.getUsername() + "\"") > -1)
        {
            messageEditorPanel.setVisible(false);
        }
        else
        {
            messageEditorPanel.setVisible(true);
        }
    }

    /**
     * 删除消息
     *
     * @param messageId
     */
    public void deleteMessage(String messageId)
    {
        /*int i = 0;
        for (; i < messageItems.size(); i++)
        {
            if (messageItems.get(i).getId().equals(messageId))
            {
                break;
            }
        }*/

        int pos = findMessageItemPositionInViewReverse(messageId);
        if (pos > -1)
        {
            messageItems.remove(pos);
            messagePanel.getMessageListView().notifyItemRemoved(pos);
            messageService.markDeleted(messageId);
        }
    }

    /**
     * 粘贴
     */
    public void paste()
    {
        messageEditorPanel.getEditor().paste();
        messageEditorPanel.getEditor().requestFocus();
    }

    public void restoreRemoteHistoryLoadedRooms()
    {
        remoteHistoryLoadedRooms.clear();
    }
}

interface RemoteHistoryReceivedListener
{
    void onReceived(int newMessageCount);
}
