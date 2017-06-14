package com.rc.app;

import com.rc.utils.IconUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;

/**
 * Created by song on 14/06/2017.
 */
public class Test
{
    public static void main(String[] a)
    {
        MyFrame frame = new MyFrame();

        frame.setSize(300, 400);
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

    }


    static class MyFrame extends JFrame
    {
        public MyFrame()
        {
            setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

            SystemTray systemTray = SystemTray.getSystemTray();//获取系统托盘
            try
            {
                Image icon = IconUtil.getIcon(this, "/image/ic_launcher.png").getImage();

                TrayIcon trayIcon = new TrayIcon(icon, "和理通");

                trayIcon.addMouseListener(new MouseAdapter()
                {
                    @Override
                    public void mouseClicked(MouseEvent e)
                    {
                        trayIcon.displayMessage("通知：", "程序最小化到系统托盘", TrayIcon.MessageType.NONE);

                        MyFrame.this.setVisible(true);
                        super.mouseClicked(e);
                    }
                });

                systemTray.add(trayIcon);

            }
            catch (AWTException e)
            {
                e.printStackTrace();
            }



        }
    }
}
