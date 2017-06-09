package com.rc.app;

import com.rc.db.service.CurrentUserService;
import com.rc.db.service.RoomService;
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

    static
    {
        sqlSession = DbUtils.getSqlSession();
        roomService = new RoomService(sqlSession);
        currentUserService = new CurrentUserService(sqlSession);
    }


    public void launch()
    {
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
        }

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private boolean checkLoginInfo()
    {
        return currentUserService.findAll().size() > 0;
    }


}
