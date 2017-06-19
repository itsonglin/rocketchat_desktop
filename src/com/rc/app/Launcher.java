package com.rc.app;

import com.rc.db.service.*;
import com.rc.forms.LoginFrame;
import com.rc.forms.MainFrame;
import com.rc.utils.DbUtils;
import org.apache.ibatis.session.SqlSession;

import javax.swing.*;

/**
 * Created by song on 09/06/2017.
 */
public class Launcher
{
    public static SqlSession sqlSession;
    public static RoomService roomService;
    public static CurrentUserService currentUserService;
    public static MessageService messageService;
    public static ContactsUserService contactsUserService;
    public static ImageAttachmentService imageAttachmentService;
    public static FileAttachmentService fileAttachmentService;

    public static final String HOSTNAME = "https://chat1.shls-leasing.com";

    public static String userHome;
    public static String appFilesBasePath;


    static
    {
        sqlSession = DbUtils.getSqlSession();
        roomService = new RoomService(sqlSession);
        currentUserService = new CurrentUserService(sqlSession);
        messageService = new MessageService(sqlSession);
        contactsUserService = new ContactsUserService(sqlSession);
        imageAttachmentService = new ImageAttachmentService(sqlSession);
        fileAttachmentService = new FileAttachmentService(sqlSession);
    }


    public void launch()
    {
        config();

        JFrame frame;
        // 原来登录过
        if (checkLoginInfo())
        {
            frame = new MainFrame();
        }
        // 从未登录过
        else
        {
            frame = new LoginFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
        frame.setVisible(true);
    }

    private void config()
    {
        userHome = System.getProperty("user.home");

        appFilesBasePath = userHome + "/Helichat";
    }

    private boolean checkLoginInfo()
    {
        return currentUserService.findAll().size() > 0;
    }


}
