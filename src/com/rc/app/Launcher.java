package com.rc.app;

import com.rc.db.service.*;
import com.rc.frames.LoginFrame;
import com.rc.frames.MainFrame;
import com.rc.tasks.HttpGetTask;
import com.rc.tasks.HttpResponseListener;
import com.rc.utils.DbUtils;
import org.apache.ibatis.session.SqlSession;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by song on 09/06/2017.
 */
public class Launcher
{
    private static Launcher context;

    public static SqlSession sqlSession;
    public static RoomService roomService;
    public static CurrentUserService currentUserService;
    public static MessageService messageService;
    public static ContactsUserService contactsUserService;
    public static ImageAttachmentService imageAttachmentService;
    public static FileAttachmentService fileAttachmentService;

    public static final String HOSTNAME = "https://chat1.shls-leasing.com";
    public static final String UPDATE_HOSTNAME = "https://apk.shls-leasing.com";
    public static final String APP_VERSION = "1.0.15";


    public static String userHome;
    public static String appFilesBasePath;

    public static Map<Integer, KeyStroke> hotKeyMap;


    /**
     * 截取屏幕
     */
    public static final int HOT_KEY_SCREEN_SHOT = 1;


    static
    {
        sqlSession = DbUtils.getSqlSession();
        roomService = new RoomService(sqlSession);
        currentUserService = new CurrentUserService(sqlSession);
        messageService = new MessageService(sqlSession);
        contactsUserService = new ContactsUserService(sqlSession);
        imageAttachmentService = new ImageAttachmentService(sqlSession);
        fileAttachmentService = new FileAttachmentService(sqlSession);

        hotKeyMap = new HashMap<>();
        hotKeyMap.put(HOT_KEY_SCREEN_SHOT, KeyStroke.getKeyStroke('A', InputEvent.CTRL_DOWN_MASK|InputEvent.ALT_DOWN_MASK));

    }

    private JFrame currentFrame;


    public Launcher()
    {
        context = this;
    }

    public void launch()
    {
        config();

        if (!isApplicationRunning())
        {
            openFrame();

            System.out.println("检查更新中...");

            // 检查更新
            checkUpdate();
        }
        else
        {
            System.exit(-1);
        }




        copyLib();
    }

    private void copyLib()
    {
        try
        {
            InputStream inputStream = getClass().getResourceAsStream("/libs/NsUserNotificationsBridge.dylib");

            File libDir = new File(appFilesBasePath + File.separator + "libs");
            if (!libDir.exists())
            {
                System.out.println("创建目录：" + libDir.getAbsolutePath());
                libDir.mkdirs();
            }

            File destDylibFile = new File(libDir, "NsUserNotificationsBridge.dylib");
            if (!destDylibFile.exists())
            {

                FileOutputStream outputStream = new FileOutputStream(destDylibFile);
                byte[] buffer = new byte[1024];
                int len = -1;
                while ((len = inputStream.read(buffer)) > -1)
                {
                    outputStream.write(buffer, 0, len);
                }
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
    }


    private void openFrame()
    {
        // 原来登录过
        if (checkLoginInfo())
        {
            currentFrame = new MainFrame();
        }
        // 从未登录过
        else
        {
            currentFrame = new LoginFrame();
            currentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
        currentFrame.setVisible(true);
    }

    private void config()
    {
        userHome = System.getProperty("user.home");

        appFilesBasePath = userHome + System.getProperty("file.separator") + "Helichat";
    }

    private boolean checkLoginInfo()
    {
        return currentUserService.findAll().size() > 0;
    }

    /**
     * 通过文件锁来判断程序是否正在运行
     *
     * @return 如果正在运行返回true，否则返回false
     */
    private static boolean isApplicationRunning()
    {
        boolean rv = false;
        try
        {
            String path = appFilesBasePath + System.getProperty("file.separator") + "appLock";
            File dir = new File(path);
            if (!dir.exists())
            {
                dir.mkdirs();
            }

            File lockFile = new File(path + System.getProperty("file.separator") + "appLaunch.lock");
            if (!lockFile.exists())
            {
                lockFile.createNewFile();
            }

            //程序名称
            RandomAccessFile fis = new RandomAccessFile(lockFile.getAbsolutePath(), "rw");
            FileChannel fileChannel = fis.getChannel();
            FileLock fileLock = fileChannel.tryLock();
            if (fileLock == null)
            {
                System.out.println("程序已在运行.");
                rv = true;
            }
        }
        catch (FileNotFoundException e1)
        {
            e1.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return rv;
    }

    public void reLogin(String username)
    {
        MainFrame.getContext().setVisible(false);
        MainFrame.getContext().dispose();

        currentFrame = new LoginFrame(username);
        currentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        currentFrame.setVisible(true);
    }

    /**
     * 检查是否有更新
     */
    private void checkUpdate()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                String url = UPDATE_HOSTNAME + "/Update/updateDesktop";

                HttpGetTask task = new HttpGetTask();
                task.setListener(new HttpResponseListener<JSONObject>()
                {
                    @Override
                    public void onSuccess(JSONObject retJson)
                    {
                        try
                        {
                            JSONArray messages = retJson.getJSONArray("message");
                            StringBuilder sb = new StringBuilder();
                            for (int i = 0; i < messages.length(); i++)
                            {
                                sb.append("* " + messages.get(i) + "\r\n");
                            }

                            String version = retJson.getString("version");
                            if (!version.equals(Launcher.APP_VERSION))
                            {
                                System.out.println("发现新版：" + version);
                                File updateSignalFile = new File(appFilesBasePath + System.getProperty("file.separator") + "update.dat");
                                if (!updateSignalFile.exists())
                                {
                                    updateSignalFile.createNewFile();
                                }
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed()
                    {

                    }
                });

                task.execute(url);
            }
        }).start();
    }

    public static Launcher getContext()
    {
        return context;
    }


}
