package com.rc.app;

import com.rc.utils.IconUtil;
import com.sun.awt.AWTUtilities;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by song on 14/06/2017.
 */

class Test extends JFrame
{
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                JFrame.setDefaultLookAndFeelDecorated(true);
                Test frame = new Test();
                frame.setSize(new Dimension(200, 300));
                frame.setUndecorated(true);

                /** 设置圆角 */
                AWTUtilities.setWindowShape(frame, new RoundRectangle2D.Double(
                        0.0D, 0.0D, frame.getWidth(), frame.getHeight(), 26.0D,
                        26.0D));

                frame.setVisible(true);
            }

        });
    }
}
