package com.rc.app;

import com.rc.utils.IconUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by song on 14/06/2017.
 */
public class Test
{
    public static void main(String[] a) throws IOException
    {
        //int[] ret = getImageSize("/Users/song/add_member.png");
        //System.out.println(ret[0] + ", " + ret[1]);

        Desktop.getDesktop().open(new File("/Users/song/Downloads/windows版.rar"));
    }


    private static int[] getImageSize(String file) throws IOException
    {
        // Bitmap image = BitmapFactory.decodeFile(file);

        BufferedImage image = ImageIO.read(new File(file));
        int width = image.getWidth();
        int height = image.getHeight();

        return new int[]{width, height};
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
